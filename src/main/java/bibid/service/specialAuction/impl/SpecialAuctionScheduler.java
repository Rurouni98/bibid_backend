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
import java.util.Comparator;
import java.util.Date;
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
    private final Map<Long, ScheduledFuture<?>> scheduledNotifications = new ConcurrentHashMap<>();

    // 경매 채널 할당 스케줄링
    public void scheduleChannelAllocation(Long auctionIndex, LocalDateTime startingLocalDateTime) {
        LocalDateTime allocationTime = startingLocalDateTime.minusMinutes(30);
        Date scheduleDate = Date.from(allocationTime.atZone(ZoneId.systemDefault()).toInstant());

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
        LocalDateTime releaseTime = startingLocalDateTime.plusHours(1);
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
                auction.setAuctionStatus("경매종료");
                auctionRepository.save(auction);
                log.info("1시간 경과로 강제 채널 반납 완료: 경매 ID {}", auctionIndex);
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
                    AuctionDetail auctionDetail = new AuctionDetail();
                    auctionDetail.setAuction(auction);
                    auctionDetail.setWinnerIndex(lastBidInfo.getBidder().getMemberIndex());
                    auctionDetail.setWinningBid(lastBidInfo.getBidAmount());

                    auction.setAuctionStatus("낙찰");
                    auction.setAuctionDetail(auctionDetail);
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

    // 알림 등록 및 스케줄링
    public boolean registerAlarm(Auction auction) {
        Long auctionIndex = auction.getAuctionIndex();
        LocalDateTime auctionStartTime = auction.getStartingLocalDateTime().minusMinutes(30);

        if (scheduledNotifications.containsKey(auctionIndex)) {
            log.info("알림 스케줄이 이미 존재합니다. 경매 ID: {}", auctionIndex);
            return false;
        }

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> notificationService.sendAuctionStartNotification(auction),
                Date.from(auctionStartTime.atZone(ZoneId.systemDefault()).toInstant())
        );

        scheduledNotifications.put(auctionIndex, scheduledTask);
        log.info("알림 스케줄링 등록 완료: 경매 ID {}", auctionIndex);
        return true;
    }

    // WebSocket을 통해 경매 종료 세부 정보 전송
    private void sendAuctionEndDetails(Auction auction) {
        log.info("Sending auction end details for auction ID: {}", auction.getAuctionIndex());
        messagingTemplate.convertAndSend("/topic/auction/" + auction.getAuctionIndex(), auction.getAuctionDetail().toDto());
    }
}
