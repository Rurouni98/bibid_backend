package bibid.service.specialAuction.impl;

import bibid.entity.*;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.auction.AuctionRepository;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionScheduler {

    private final SimpMessagingTemplate messagingTemplate;
    private final AuctionRepository auctionRepository;
    private final TaskScheduler taskScheduler;
    private final LiveStationPoolManager liveStationPoolManager;
    private final NotificationService notificationService;
    private final Map<Long, Map<Long, ScheduledFuture<?>>> scheduledNotifications = new ConcurrentHashMap<>();


    // 경매 채널 할당 스케줄링
    public void scheduleChannelAllocation(Long auctionIndex, LocalDateTime startingLocalDateTime) {
//        LocalDateTime allocationTime = startingLocalDateTime.minusMinutes(30);
//        LocalDateTime allocationTime = startingLocalDateTime.minusMinutes(5);

        // 5분 전으로 설정 (KST)
        LocalDateTime allocationTimeKST = startingLocalDateTime.minusMinutes(5);

//        Date scheduleDate = Date.from(allocationTime.atZone(ZoneId.systemDefault()).toInstant());

        // KST를 UTC로 변환
        ZonedDateTime allocationTimeUTC = allocationTimeKST.atZone(ZoneId.of("Asia/Seoul"))
                .withZoneSameInstant(ZoneId.of("UTC"));

        Date scheduleDate = Date.from(allocationTimeUTC.toInstant());


        ScheduledFuture<?> allocationTask = taskScheduler.schedule(() -> {
            try {
                log.info("채널 할당 작업 시작 for auctionIndex: {}", auctionIndex);
                LiveStationChannel allocatedChannel = liveStationPoolManager.allocateChannel();
                Auction auction = auctionRepository.findById(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("경매를 찾을 수 없습니다. ID: " + auctionIndex));

                auction.setAuctionStatus("준비중");
                auction.setLiveStationChannel(allocatedChannel);
                auctionRepository.save(auction);
                log.info("채널 할당 및 경매 상태 업데이트 성공 for auctionIndex: {}", auctionIndex);
            } catch (Exception e) {
                log.error("채널 할당 중 오류 발생 for auctionIndex: {}. 오류: {}", auctionIndex, e.getMessage(), e);
            }
        }, scheduleDate);

        log.info("경매 채널 할당 스케줄링 완료: auctionIndex={}, scheduleDate={}", auctionIndex, scheduleDate);
    }

    // 경매 종료 후 채널 반납 스케줄링
    public void scheduleChannelRelease(Long auctionIndex, LocalDateTime startingLocalDateTime) {
        // LocalDateTime releaseTime = startingLocalDateTime.plusHours(1);
        LocalDateTime releaseTime = startingLocalDateTime.plusMinutes(10);
        Date releaseDate = Date.from(releaseTime.atZone(ZoneId.systemDefault()).toInstant());

        taskScheduler.schedule(() -> {
            try {
                Auction auction = auctionRepository.findById(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("경매를 찾을 수 없습니다. ID: " + auctionIndex));
                LiveStationChannel channel = auction.getLiveStationChannel();
                if (channel == null) {
                    log.info("채널이 이미 반납된 상태입니다. 경매 ID: {}", auctionIndex);
                    return;
                }
                liveStationPoolManager.releaseChannel(channel);
                auction.setLiveStationChannel(null);
                auctionRepository.save(auction);
//                log.info("1시간 경과로 강제 채널 반납 완료: 경매 ID {}", auctionIndex);
                log.info("10분 경과로 강제 채널 반납 완료: 경매 ID {}", auctionIndex);
            } catch (Exception e) {
                log.error("채널 반납 중 오류 발생 for auctionIndex: {}. 오류: {}", auctionIndex, e.getMessage(), e);
            }
        }, releaseDate);

        log.info("경매 종료 후 채널 반납 스케줄링 완료: auctionIndex={}, releaseDate={}", auctionIndex, releaseDate);
    }

    // 경매 종료 스케줄링
    public void scheduleAuctionEnd(Long auctionIndex, LocalDateTime endingLocalDateTime) {
        Date endDate = Date.from(endingLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        taskScheduler.schedule(() -> {
            try {
                Auction auction = auctionRepository.findById(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("경매를 찾을 수 없습니다. ID: " + auctionIndex));
                AuctionInfo lastBidInfo = auction.getAuctionInfoList()
                        .stream()
                        .max(Comparator.comparing(AuctionInfo::getBidTime))
                        .orElse(null);

                if (lastBidInfo != null) {

                    // 낙찰자 정보 및 상세 정보 설정
                    AuctionDetail auctionDetail = auction.getAuctionDetail();
                    auctionDetail.setWinnerIndex(lastBidInfo.getBidder().getMemberIndex());
                    auctionDetail.setWinningBid(lastBidInfo.getBidAmount());
                    auctionDetail.setWinnerNickname(lastBidInfo.getBidder().getNickname());

                    auction.setAuctionStatus("낙찰"); // 경매 상태를 '낙찰'로 설정

                    // 낙찰자와 판매자에게 알림 전송
                    notificationService.notifyAuctionWin(lastBidInfo.getBidder(), auctionIndex);
                    notificationService.notifyAuctionSold(auction.getMember(), auctionIndex);

                    log.info("Auction finalized with winner for auction ID: {}, winner ID: {}, winning bid: {}",
                            auctionIndex, lastBidInfo.getBidder().getMemberIndex(), lastBidInfo.getBidAmount());

                } else {
                    auction.setAuctionStatus("유찰");
                }

                auctionRepository.save(auction);
                sendAuctionEndDetails(auction);
                log.info("경매 종료 처리 완료: 경매 ID {}", auctionIndex);
            } catch (Exception e) {
                log.error("경매 종료 처리 중 오류 발생: 경매 ID {}. 오류: {}", auctionIndex, e.getMessage(), e);
            }
        }, endDate);

        log.info("경매 종료 스케줄링 완료: auctionIndex={}, endDate={}", auctionIndex, endDate);
    }

    public boolean registerAlarmForUser(Auction auction, Long memberIndex) {
        Long auctionIndex = auction.getAuctionIndex();
//        LocalDateTime auctionStartTime = auction.getStartingLocalDateTime().minusMinutes(30);
        LocalDateTime auctionStartTime = auction.getStartingLocalDateTime().minusMinutes(10);

        // 중복 스케줄링 확인
        if (scheduledNotifications.containsKey(auctionIndex) && scheduledNotifications.get(auctionIndex).containsKey(memberIndex)) {
            log.info("사용자가 이미 알림 신청을 완료했습니다. 경매 ID: {}, 사용자 ID: {}", auctionIndex, memberIndex);
            return false;
        }

        // DB에 알림을 "전송 예정" 상태로 저장
        Notification savedNotification = notificationService.createScheduledNotification(auction, memberIndex);
        Long notificationIndex = savedNotification.getNotificationIndex();


        // 예약된 시간에 알림 전송
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> notificationService.sendAuctionStartNotificationToUser(auction, memberIndex, notificationIndex),
                Date.from(auctionStartTime.atZone(ZoneId.systemDefault()).toInstant())
        );

        scheduledNotifications.computeIfAbsent(auctionIndex, k -> new HashMap<>()).put(memberIndex, scheduledTask);
        log.info("알림 스케줄링 등록 완료: 경매 ID {}, 사용자 ID {}", auctionIndex, memberIndex);
        return true;
    }

    // WebSocket을 통해 경매 종료 세부 정보 전송
    private void sendAuctionEndDetails(Auction auction) {
        log.info("Sending auction end details for auction ID: {}", auction.getAuctionIndex());
        messagingTemplate.convertAndSend("/topic/auction/" + auction.getAuctionIndex(), auction.getAuctionDetail().toDto());
    }
}
