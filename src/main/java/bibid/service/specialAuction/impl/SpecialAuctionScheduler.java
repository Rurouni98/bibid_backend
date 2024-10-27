package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.AuctionDetail;
import bibid.entity.AuctionInfo;
import bibid.entity.LiveStationChannel;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.auction.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionScheduler {

    private final SimpMessagingTemplate messagingTemplate;

    private final AuctionRepository auctionRepository;
    private final TaskScheduler taskScheduler;
    private final LiveStationPoolManager liveStationPoolManager;
    private final SpecialAuctionRepository specialAuctionRepository;
    private ScheduledFuture<?> futureTask;

    public void scheduleChannelAllocation(Long auctionIndex, LocalDateTime startingLocalDateTime) {
        LocalDateTime allocationTime = startingLocalDateTime.minusMinutes(30);
        Date scheduleDate = Date.from(allocationTime.atZone(ZoneId.systemDefault()).toInstant());

        futureTask = taskScheduler.schedule(() -> {
            try {
                log.info("채널 할당 작업 시작 for auctionIndex: {}", auctionIndex);

                LiveStationChannel allocatedChannel = liveStationPoolManager.allocateChannel();

                Auction auction = auctionRepository.findById(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("옥션을 찾을 수 없습니다."));

                auction.setAuctionStatus("준비중");
                auction.setLiveStationChannel(allocatedChannel);
                auctionRepository.save(auction);

                log.info("채널 할당 및 경매 상태 업데이트 성공 for auctionIndex: {}", auctionIndex);
            } catch (Exception e) {
                log.error("채널 할당 중 오류 발생 for auctionIndex: {}. 오류: {}", auctionIndex, e.getMessage(), e);
            }
        }, scheduleDate);
    }

    public void scheduleChannelRelease(Long auctionIndex, LocalDateTime startingLocalDateTime) {
        LocalDateTime releaseTime = startingLocalDateTime.plusHours(1);
        Date releaseDate = Date.from(releaseTime.atZone(ZoneId.systemDefault()).toInstant());

        taskScheduler.schedule(() -> {
            Auction auction = auctionRepository.findById(auctionIndex)
                    .orElseThrow(() -> new RuntimeException("옥션을 찾을 수 없습니다."));

            LiveStationChannel channel = auction.getLiveStationChannel();
            if (channel == null) {
                log.info("채널이 이미 반납된 상태입니다. 경매 ID: {}", auctionIndex);
                return;
            }

            liveStationPoolManager.releaseChannel(channel);
            auction.setLiveStationChannel(null);
            auction.setAuctionStatus("경매종료");
            auctionRepository.save(auction);
            log.info("1시간 경과로 강제 채널 반납: 경매 ID {}", auctionIndex);
        }, releaseDate);
    }

    public void scheduleAuctionEnd(Long auctionIndex, LocalDateTime endingLocalDateTime) {
        // 경매 종료 시간이므로 종료 시각을 Date 객체로 변환
        Date endDate = Date.from(endingLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        taskScheduler.schedule(() -> {
            try {
                // 경매 정보 조회
                Auction auction = auctionRepository.findById(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("옥션을 찾을 수 없습니다."));

                // 마지막 입찰 정보를 가져오기
                AuctionInfo lastBidInfo = auction.getAuctionInfoList()
                        .stream()
                        .max(Comparator.comparing(AuctionInfo::getBidTime))
                        .orElse(null);

                if (lastBidInfo != null) {
                    // 낙찰자 및 낙찰 금액 설정
                    AuctionDetail auctionDetail = new AuctionDetail();
                    auctionDetail.setAuction(auction); // 관계 설정
                    auctionDetail.setWinnerIndex(lastBidInfo.getBidder().getMemberIndex());
                    auctionDetail.setWinningBid(lastBidInfo.getBidAmount());

                    // 경매 상태 업데이트
                    auction.setAuctionStatus("낙찰");
                    auction.setAuctionDetail(auctionDetail); // AuctionDetail을 설정
                } else {
                    auction.setAuctionStatus("유찰");
                }

                Auction savedAuction = auctionRepository.save(auction);

                // WebSocket을 통해 낙찰자 정보 전송
                sendAuctionEndDetails(savedAuction);

                log.info("경매 종료 처리 완료: 경매 ID {}", auctionIndex);
            } catch (Exception e) {
                log.error("경매 종료 처리 중 오류 발생: 경매 ID {}. 오류: {}", auctionIndex, e.getMessage(), e);
            }
        }, endDate);
    }

    public void cancelScheduledTask() {
        if (futureTask != null && !futureTask.isCancelled()) {
            futureTask.cancel(true);
            log.info("Scheduled task cancelled for future task: {}", futureTask);
        }
    }

    public void sendAuctionEndDetails(Auction auction) {
        log.info("Sending auction end details for auction ID: {}", auction.getAuctionIndex());
        messagingTemplate.convertAndSend("/topic/auction/" + auction.getAuctionIndex(), auction.getAuctionDetail().toDto());
    }

}