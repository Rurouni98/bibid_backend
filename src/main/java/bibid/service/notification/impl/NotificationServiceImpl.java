package bibid.service.notification.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import bibid.dto.NotificationDto;
import bibid.entity.*;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.repository.notification.NotificationRepository;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<NotificationDto> getNotificationsForMember(Long memberIndex) {
        List<Notification> notifications = notificationRepository.findByMember_MemberIndex(memberIndex);
        return notifications.stream()
                .map(Notification::toDto)
                .collect(Collectors.toList());
    }

    private void sendNotificationData(Member member, Map<String, Object> notificationData) {
        messagingTemplate.convertAndSend("/topic/notifications/" + member.getMemberIndex(), notificationData);
        log.info("Notification sent to WebSocket for member ID: {}", member.getMemberIndex());
    }

    @Override
    public NotificationDto createNotification(Member member, String title, String content, NotificationType category, Long referenceIndex) {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("title", title);
        contentMap.put("referenceIndex", referenceIndex);
        contentMap.put("content", content);

        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(contentMap);
        } catch (Exception e) {
            log.error("Failed to serialize notification content", e);
            contentJson = content;
        }

        Notification notification = Notification.builder()
                .member(member)
                .alertTitle(title)
                .alertContent(contentJson)
                .alertDate(LocalDateTime.now())
                .alertCategory(category)
                .referenceIndex(referenceIndex)
                .isViewed(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", savedNotification.getNotificationIndex());
        return savedNotification.toDto();
    }

    public void createAndSendNotification(Member member, Map<String, Object> notificationData) {
        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(notificationData);
        } catch (Exception e) {
            log.error("Failed to serialize notification content", e);
            contentJson = (String) notificationData.getOrDefault("content", "");
        }

        Notification notification = Notification.builder()
                .member(member)
                .alertTitle((String) notificationData.get("title"))
                .alertContent(contentJson)
                .alertDate(LocalDateTime.now())
                .alertCategory((NotificationType) notificationData.get("notificationType"))
                .referenceIndex((Long) notificationData.get("referenceIndex"))
                .isViewed(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification saved with ID: {} for member ID: {}", savedNotification.getNotificationIndex(), member.getMemberIndex());

        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + member.getMemberIndex(), notificationData);
            log.info("Notification sent to WebSocket for member ID: {}", member.getMemberIndex());
        } catch (Exception e) {
            log.warn("WebSocket 전송 실패 - 유저가 오프라인 상태일 수 있습니다. 알림은 DB에 저장되어 있습니다. member ID: {}", member.getMemberIndex(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> getUnreadNotifications(Long memberIndex) {
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
        Notification notification = notificationRepository.findById(notificationIndex)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림을 찾을 수 없습니다. ID: " + notificationIndex));
        notification.setViewed(true);
        notificationRepository.save(notification);
        log.info("Notification marked as viewed: ID {}", notificationIndex);
    }

    @Override
    public void sendAuctionStartNotificationToUser(Auction auction, Long memberIndex) {
        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: ID " + memberIndex));

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "실시간 경매 공지");
        notificationData.put("content", "경매 " + auction.getAuctionIndex() + "가 곧 시작됩니다.");
        notificationData.put("auctionIndex", auction.getAuctionIndex());
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        notificationData.put("notificationType", NotificationType.AUCTION_START);

        createAndSendNotification(member, notificationData);
    }

    @Override
    public void notifyAuctionWin(Member winner, Long auctionIndex) {
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("경매가 없습니다."));

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "참여중인 경매 낙찰 공지");
        notificationData.put("content", "일반 경매 " + auction.getProductName() + "이/(가) 낙찰되었습니다.");
        notificationData.put("winnerNickname", winner.getNickname());
        notificationData.put("winningBid", auction.getAuctionDetail().getWinningBid());
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        notificationData.put("notificationType", NotificationType.AUCTION_WIN);

        createAndSendNotification(winner, notificationData);
    }

    @Override
    public void notifyServerMaintenance(String title, String content) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        notificationData.put("notificationType", NotificationType.SERVER_MAINTENANCE);

        messagingTemplate.convertAndSend("/topic/notifications/global", notificationData);
        log.info("Server maintenance notification sent: {}", content);
    }

    @Override
    public void notifyAuctionSold(Member seller, Long auctionIndex) {
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("경매가 없습니다."));

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "낙찰");
        notificationData.put("content", "경매 " + auction.getProductName() + "가 낙찰되었습니다.");
        notificationData.put("notificationType", NotificationType.AUCTION_SOLD);
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));

        createAndSendNotification(seller, notificationData);
    }

    @Override
    public void notifyHigherBid(Member bidder, Long auctionIndex) {
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("경매가 없습니다."));

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "상위 입찰자 공지");
        notificationData.put("content", "경매 " + auction.getProductName() + "에서 새로운 입찰자가 등장했습니다.");
        notificationData.put("notificationType", NotificationType.HIGHER_BID);
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));

        createAndSendNotification(bidder, notificationData);
    }

    @Override
    public void notifyDeliveryConfirmation(Member sender, Member receiver, Long auctionIndex) {
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("경매가 없습니다."));

        Map<String, Object> buyerNotificationData = new HashMap<>();
        buyerNotificationData.put("title", "구매한 물품 정산 공지");
        buyerNotificationData.put("content", "구매한 \"" + auction.getProductName() + "\"에 대한 물품 수령 확인이 되어 물품 금액이 정산처리 되었습니다.");
        buyerNotificationData.put("price", auction.getAuctionDetail().getWinningBid());
        buyerNotificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        buyerNotificationData.put("notificationType", NotificationType.PURCHASE_CONFIRMATION);

        createAndSendNotification(receiver, buyerNotificationData);

        Map<String, Object> sellerNotificationData = new HashMap<>();
        sellerNotificationData.put("title", "판매된 물품 정산 공지");
        sellerNotificationData.put("content", "판매된 \"" + auction.getProductName() + "\"을(를) 구매자가 수령하여 물품에 대한 금액이 정산처리 되었습니다.");
        sellerNotificationData.put("price", auction.getAuctionDetail().getWinningBid()*0.9);
        sellerNotificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        sellerNotificationData.put("notificationType", NotificationType.SALE_CONFIRMATION);

        createAndSendNotification(sender, sellerNotificationData);
    }

    @Override
    public void notifyExchange(Member member, String title, String content) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "환전");
        notificationData.put("content", content);
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        notificationData.put("notificationType", NotificationType.EXCHANGE_NOTIFICATION);

        createAndSendNotification(member, notificationData);
    }

    @Override
    public void notifyDeposit(Member member, String title, String content) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "충전");
        notificationData.put("content", content);
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        notificationData.put("notificationType", NotificationType.DEPOSIT_NOTIFICATION);

        createAndSendNotification(member, notificationData);
    }

    @Override
    public void notifyDirectMessage(Member sender, Member receiver, String content, Long auctionIndex) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "새로운 메시지 도착");
        notificationData.put("content", content);
        notificationData.put("auctionIndex", auctionIndex);
        notificationData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm")));
        notificationData.put("notificationType", NotificationType.DIRECT_MESSAGE);

        createAndSendNotification(receiver, notificationData);
    }
}
