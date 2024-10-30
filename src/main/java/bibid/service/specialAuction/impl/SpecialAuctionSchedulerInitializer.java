package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.LiveStationChannel;
import bibid.entity.Notification;
import bibid.entity.NotificationType;
import bibid.repository.livestation.LiveStationChannelRepository;
import bibid.repository.notification.NotificationRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionSchedulerInitializer {

    private final SpecialAuctionRepository specialAuctionRepository;
    private final SpecialAuctionScheduler specialAuctionScheduler;
    private final LiveStationPoolManager liveStationPoolManager;
    private final NotificationRepository notificationRepository; // NotificationRepository 추가

    public void rescheduleAuctionsAfterRestart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        List<Auction> realTimeAuctions = specialAuctionRepository.findAllByAuctionTypeAndStartingLocalDateTimeAfter(
                "실시간 경매", now
        );

        for (Auction auction : realTimeAuctions) {
            LocalDateTime auctionStartTime = auction.getStartingLocalDateTime();

            if (auction.getLiveStationChannel() == null) {
                if (auctionStartTime.isBefore(thirtyMinutesLater)) {
                    LiveStationChannel channel = liveStationPoolManager.allocateChannel();
//                    LiveStationChannel channel = liveStationPoolManager.testCreateNewChannel();

                    auction.setAuctionStatus("준비중");
                    auction.setLiveStationChannel(channel);
                    specialAuctionRepository.save(auction);

                    log.info("즉시 채널 할당: 경매 ID {} 채널 인덱스 : {}", auction.getAuctionIndex(), channel.getLiveStationChannelIndex());

                } else {
                    // 경매 시작까지 30분 이상 남은 경우: 30분 전에 스케줄 등록
                    specialAuctionScheduler.scheduleChannelAllocation(
                            auction.getAuctionIndex(), auctionStartTime.minusMinutes(30)
                    );
                    log.info("30분 전 스케줄 등록: 경매 ID {}", auction.getAuctionIndex());
                }
            }
        }

        // 서버 재시작 시, 이미 경매가 시작된 지 1시간 지난 경매 중 아직 채널이 반납되지 않은 경매에 대해 강제 반납 스케줄링
        List<Auction> auctionsToRelease = specialAuctionRepository.findAllByAuctionTypeAndStartingLocalDateTimeBeforeAndLiveStationChannelIsNotNull(
                "실시간 경매", now.minusHours(1)
        );

        for (Auction auction : auctionsToRelease) {
            // 이미 1시간이 지난 경매는 즉시 반납 처리
            liveStationPoolManager.releaseChannel(auction.getLiveStationChannel());
            auction.setLiveStationChannel(null);
            auction.setAuctionStatus("종료");
            specialAuctionRepository.save(auction);
            log.info("1시간 경과로 강제 채널 반납: 경매 ID {}", auction.getAuctionIndex());
        }

        // 경매 종료 스케줄링 추가
        List<Auction> auctionsToEnd = specialAuctionRepository.findAllByAuctionTypeAndEndingLocalDateTimeAfter(
                "실시간 경매", now
        );

        for (Auction auction : auctionsToEnd) {
            specialAuctionScheduler.scheduleAuctionEnd(auction.getAuctionIndex(), auction.getEndingLocalDateTime());
            log.info("경매 종료 스케줄 등록: 경매 ID {}", auction.getAuctionIndex());
        }

    }

    // 알림 스케줄링 메서드 추가
    private void rescheduleNotificationsOnStartup() {
        List<Notification> notifications = notificationRepository.findAll();

        notifications.forEach(notification -> {
            if (notification.getAlertCategory() == NotificationType.AUCTION_START) {
                Auction auction = specialAuctionRepository.findById(notification.getReferenceIndex()).orElse(null);
                if (auction != null) {
                    Long memberIndex = notification.getMember().getMemberIndex(); // 알림 신청한 사용자 정보 가져오기
                    specialAuctionScheduler.registerAlarmForUser(auction, memberIndex); // 사용자별 알림 스케줄링 등록
                    log.info("기존 알림 스케줄 재등록: 경매 ID {}, 사용자 ID {}", auction.getAuctionIndex(), memberIndex);
                }
            }
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleOnStartup() {
        rescheduleAuctionsAfterRestart(); // 기존 경매 스케줄링 메서드 호출
        rescheduleNotificationsOnStartup(); // 알림 스케줄링 메서드 호출
    }

}
