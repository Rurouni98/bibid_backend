package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.LiveStationChannel;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.auction.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionScheduler {

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
            auction.setAuctionStatus("종료");
            auctionRepository.save(auction);
            log.info("1시간 경과로 강제 채널 반납: 경매 ID {}", auctionIndex);
        }, releaseDate);
    }

    public void cancelScheduledTask() {
        if (futureTask != null && !futureTask.isCancelled()) {
            futureTask.cancel(true);
            log.info("Scheduled task cancelled for future task: {}", futureTask);
        }
    }

}