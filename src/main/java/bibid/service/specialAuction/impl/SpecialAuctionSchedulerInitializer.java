package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.LiveStationChannel;
import bibid.repository.livestation.LiveStationChannelRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialAuctionSchedulerInitializer {

    private final SpecialAuctionRepository specialAuctionRepository;
    private final SpecialAuctionScheduler specialAuctionScheduler;
    private final LiveStationPoolManager liveStationPoolManager;
    private final LiveStationChannelRepository liveStationChannelRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleAuctionsAfterRestart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        // 현재 시간 이후에 시작할 모든 실시간 경매들을 조회
        List<Auction> realTimeAuctions = specialAuctionRepository.findAllByAuctionTypeAndStartingLocalDateTimeAfter(
                "실시간 경매", now
        );

        for (Auction auction : realTimeAuctions) {
            LocalDateTime auctionStartTime = auction.getStartingLocalDateTime();

            // 경매에 아직 채널이 할당되지 않은 경우
            if (auction.getLiveStationChannel() == null) {
                if (auctionStartTime.isBefore(thirtyMinutesLater)) {
                    LiveStationChannel channel = liveStationPoolManager.allocateChannel();


                    auction.setAuctionStatus("준비중");
                    auction.setLiveStationChannel(channel);
                    specialAuctionRepository.save(auction);

                    System.out.println("즉시 채널 할당: 경매 ID " + auction.getAuctionIndex() + " 채널 인덱스 : " + channel.getLiveStationChannelIndex());

                } else {
                    // 경매 시작까지 30분 이상 남은 경우: 30분 전에 스케줄 등록
                    specialAuctionScheduler.scheduleChannelAllocation(
                            auction.getAuctionIndex(), auctionStartTime.minusMinutes(30)
                    );
                    System.out.println("30분 전 스케줄 등록: 경매 ID " + auction.getAuctionIndex());
                }
            }
        }
    }

}
