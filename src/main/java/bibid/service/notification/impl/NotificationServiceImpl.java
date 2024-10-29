package bibid.service.notification.impl;

import bibid.dto.NotificationDto;
import bibid.entity.Auction;
import bibid.entity.Member;
import bibid.entity.Notification;
import bibid.entity.NotificationType;
import bibid.repository.notification.NotificationRepository;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationDto createNotification(Member member, String title, String content, NotificationType category, Long referenceIndex) {
        log.info("Creating notification for member ID: {}", member.getMemberIndex());
        Notification notification = Notification.builder()
                .member(member)
                .alertTitle(title)
                .alertContent(content)
                .alertDate(LocalDateTime.now())
                .alertCategory(category)
                .referenceIndex(referenceIndex)
                .isViewed(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", savedNotification.getNotificationIndex());
        return savedNotification.toDto();
    }

    public void createAndSendNotification(Member member, String title, String content, NotificationType category, Long referenceIndex) {
        log.info("Creating and sending notification to member ID: {}", member.getMemberIndex());

        Notification notification = Notification.builder()
                .member(member)
                .alertTitle(title)
                .alertContent(content)
                .alertDate(LocalDateTime.now())
                .alertCategory(category)
                .referenceIndex(referenceIndex)
                .isViewed(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification saved with ID: {} for member ID: {}", savedNotification.getNotificationIndex(), member.getMemberIndex());

        // WebSocket을 통해 전송 시도
        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + member.getMemberIndex(), savedNotification.toDto());
            log.info("Notification sent to WebSocket for member ID: {}", member.getMemberIndex());
        } catch (Exception e) {
            log.warn("WebSocket 전송 실패 - 유저가 오프라인 상태일 수 있습니다. 알림은 DB에 저장되어 있습니다. member ID: {}", member.getMemberIndex(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> getUnreadNotifications(Long memberIndex) {
        log.info("Fetching unread notifications for member ID: {}", memberIndex);
        List<NotificationDto> unreadNotifications = notificationRepository.findByMember_MemberIndexAndIsViewedFalse(memberIndex)
                .stream()
                .map(Notification::toDto)
                .collect(Collectors.toList());

        log.info("Fetched {} unread notifications for member ID: {}", unreadNotifications.size(), memberIndex);
        return unreadNotifications;
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> getAllNotifications(Long memberIndex) {
        log.info("Fetching all notifications for member ID: {}", memberIndex);
        List<NotificationDto> allNotifications = notificationRepository.findByMember_MemberIndexOrderByAlertDateDesc(memberIndex)
                .stream()
                .map(Notification::toDto)
                .collect(Collectors.toList());

        log.info("Fetched {} total notifications for member ID: {}", allNotifications.size(), memberIndex);
        return allNotifications;
    }

    @Transactional
    @Override
    public void markAsViewed(Long notificationIndex) {
        log.info("Marking notification as viewed: ID {}", notificationIndex);
        Notification notification = notificationRepository.findById(notificationIndex)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림을 찾을 수 없습니다. ID: " + notificationIndex));
        notification.setViewed(true);
        notificationRepository.save(notification);
        log.info("Notification marked as viewed: ID {}", notificationIndex);
    }

    @Override
    public void sendAuctionStartNotification(Auction auction) {
        log.info("Sending auction start notification for auction ID: {}", auction.getAuctionIndex());
        createAndSendNotification(auction.getMember(), "경매 시작 알림",
                "경매 " + auction.getAuctionIndex() + "가 곧 시작됩니다.",
                NotificationType.AUCTION_START, auction.getAuctionIndex());
    }

    @Override
    public void notifyServerMaintenance(String title, String content) {
        log.info("Sending server maintenance notification");
        Notification notification = Notification.builder()
                .alertTitle(title)
                .alertContent(content)
                .alertDate(LocalDateTime.now())
                .alertCategory(NotificationType.SERVER_MAINTENANCE)
                .isViewed(false)
                .build();

        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/global", notification.toDto());
        log.info("Server maintenance notification sent: {}", content);
    }

    @Override
    public void notifyAuctionSold(Member seller, Long auctionIndex) {
        log.info("Sending auction sold notification for auction ID: {}", auctionIndex);
        createAndSendNotification(seller, "경매 낙찰 알림",
                "경매 " + auctionIndex + "가 낙찰되었습니다.",
                NotificationType.AUCTION_SOLD, auctionIndex);
    }

    @Override
    public void notifyHigherBid(Member bidder, Long auctionIndex) {
        log.info("Sending higher bid notification for auction ID: {}", auctionIndex);
        createAndSendNotification(bidder, "상위 입찰자 등장",
                "경매 " + auctionIndex + "에서 새로운 입찰자가 등장했습니다.",
                NotificationType.HIGHER_BID, auctionIndex);
    }

    @Override
    public void notifyAuctionWin(Member winner, Long auctionIndex) {
        log.info("Sending auction win notification for auction ID: {}", auctionIndex);
        createAndSendNotification(winner, "낙찰 알림",
                "축하합니다! 경매 " + auctionIndex + "에서 낙찰되었습니다.",
                NotificationType.AUCTION_WIN, auctionIndex);
    }

    @Override
    public void notifyDeliveryConfirmation(Member sender, Member receiver, Long auctionIndex) {
        log.info("Sending delivery confirmation notification for auction ID: {}", auctionIndex);
        createAndSendNotification(receiver, "배송 확인 요청",
                "경매 " + auctionIndex + "의 배송이 확인되었습니다.",
                NotificationType.DELIVERY_CONFIRMATION, auctionIndex);
    }

    public List<NotificationDto> getNotificationsForMember(Long memberIndex) {
        return notificationRepository.findByMember_MemberIndex(memberIndex).stream()
                .map(Notification::toDto)
                .toList();
    }

    @Override
    public void notifyExchange(Member member, String title, String content) {
        log.info("Sending exchange notification for member ID: {}", member.getMemberIndex());
        createAndSendNotification(member, title, content, NotificationType.EXCHANGE_NOTIFICATION, null);
    }

    @Override
    public void notifyDeposit(Member member, String title, String content) {
        log.info("Sending deposit notification for member ID: {}", member.getMemberIndex());
        createAndSendNotification(member, title, content, NotificationType.DEPOSIT_NOTIFICATION, null);
    }

    @Override
    public void notifyDirectMessage(Member sender, Member receiver, String content, Long auctionIndex) {
        log.info("Sending direct message notification from member ID: {} to member ID: {}", sender.getMemberIndex(), receiver.getMemberIndex());
        createAndSendNotification(receiver, "새로운 메시지 도착", content, NotificationType.DIRECT_MESSAGE, auctionIndex);
    }

}
