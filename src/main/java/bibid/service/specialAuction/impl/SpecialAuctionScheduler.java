package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.LiveStationChannel;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.auction.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class SpecialAuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final TaskScheduler taskScheduler;
    private final LiveStationPoolManager liveStationPoolManager;
    private ScheduledFuture<?> futureTask;

    public void scheduleChannelAllocation(Long auctionIndex, LocalDateTime startingLocalDateTime) {
        LocalDateTime allocationTime = startingLocalDateTime.minusMinutes(30);
        Date scheduleDate = Date.from(allocationTime.atZone(ZoneId.systemDefault()).toInstant());

        futureTask = taskScheduler.schedule(() -> {
            try {
                LiveStationChannel allocatedChannel = liveStationPoolManager.allocateChannel();

                Auction auction = auctionRepository.findById(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("옥션을 찾을 수 없습니다."));

                auction.setAuctionStatus("준비중");
                auction.setLiveStationChannel(allocatedChannel);
                auctionRepository.save(auction);
                
            } catch (Exception e) {
                System.out.println("채널 할당 중 오류 발생: " + e.getMessage());
            }
        }, scheduleDate);
    }
}