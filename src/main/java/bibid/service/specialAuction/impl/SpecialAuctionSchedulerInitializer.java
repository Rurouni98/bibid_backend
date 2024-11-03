package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.LiveStationChannel;
import bibid.entity.Notification;
import bibid.entity.NotificationType;
import bibid.repository.notification.NotificationRepository;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.livestation.LiveStationPoolManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionSchedulerInitializer {

    private final SpecialAuctionRepository specialAuctionRepository;
    private final SpecialAuctionScheduler specialAuctionScheduler;
    private final LiveStationPoolManager liveStationPoolManager;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void rescheduleAuctionsAfterRestart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        List<Auction> realTimeAuctions = specialAuctionRepository.findAllWithChannelByAuctionTypeAndStartingLocalDateTimeAfter(
                "실시간 경매", now
        );

        for (Auction auction : realTimeAuctions) {
            LocalDateTime auctionStartTime = auction.getStartingLocalDateTime();

            if (auction.getLiveStationChannel() == null) {
                if (auctionStartTime.isBefore(thirtyMinutesLater)) {
                    LiveStationChannel channel = liveStationPoolManager.testCreateNewChannel();
                    auction.setAuctionStatus("준비중");
                    auction.setLiveStationChannel(channel);
                    specialAuctionRepository.save(auction);

                    log.info("즉시 채널 할당: 경매 ID {} 채널 인덱스: {}", auction.getAuctionIndex(), channel.getLiveStationChannelIndex());
                } else {
                    specialAuctionScheduler.scheduleChannelAllocation(
                            auction.getAuctionIndex(), auctionStartTime.minusMinutes(30)
                    );
                    log.info("30분 전 스케줄 등록: 경매 ID {}", auction.getAuctionIndex());
                }
            }
        }

        List<Auction> auctionsToRelease = specialAuctionRepository.findAllByAuctionTypeAndStartingLocalDateTimeBeforeAndLiveStationChannelIsNotNull(
                "실시간 경매", now.minusMinutes(30)
        );

        for (Auction auction : auctionsToRelease) {
            liveStationPoolManager.releaseChannel(auction.getLiveStationChannel());
            auction.setLiveStationChannel(null);
            specialAuctionRepository.save(auction);
            log.info("30분 경과로 강제 채널 반납: 경매 ID {}", auction.getAuctionIndex());
        }

        List<Auction> auctionsToEnd = specialAuctionRepository.findAllByAuctionTypeAndEndingLocalDateTimeAfter(
                "실시간 경매", now
        );

        for (Auction auction : auctionsToEnd) {
            specialAuctionScheduler.scheduleAuctionEnd(auction.getAuctionIndex(), auction.getEndingLocalDateTime());
            log.info("경매 종료 스케줄 등록: 경매 ID {}", auction.getAuctionIndex());
        }
    }

    @Transactional
    private void rescheduleNotificationsOnStartup() {
        List<Notification> notifications = notificationRepository.findByIsSentFalse();

        notifications.forEach(notification -> {
            if (notification.getAlertCategory() == NotificationType.AUCTION_START) {
                Auction auction = specialAuctionRepository.findByIdWithChannel(notification.getReferenceIndex()).orElse(null);
                if (auction != null) {
                    Long memberIndex = notification.getMember().getMemberIndex();
                    specialAuctionScheduler.registerAlarmForUser(auction, memberIndex);
                    log.info("기존 알림 스케줄 재등록: 경매 ID {}, 사용자 ID {}", auction.getAuctionIndex(), memberIndex);
                }
            }
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleOnStartup() {
        rescheduleAuctionsAfterRestart();
        rescheduleNotificationsOnStartup();
    }
}
