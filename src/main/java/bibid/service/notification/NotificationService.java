package bibid.service.notification;

import bibid.dto.NotificationDto;
import bibid.entity.Auction;
import bibid.entity.Member;
import bibid.entity.NotificationType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {

    // 알림 생성 메서드
    @Transactional
    NotificationDto createNotification(Member member, String title, String content, NotificationType category, Long referenceIndex);

    // 특정 회원의 읽지 않은 알림 조회 메서드
    @Transactional(readOnly = true)
    List<NotificationDto> getUnreadNotifications(Long memberIndex);

    // 특정 회원의 모든 알림 조회 메서드 (최신순)
    @Transactional(readOnly = true)
    List<NotificationDto> getAllNotifications(Long memberIndex);

    // 특정 알림을 읽음 상태로 업데이트
    @Transactional
    void markAsViewed(Long notificationIndex);

    // 알림 전송 로직 (SimpMessagingTemplate 등을 사용하여 구현 가능)
    void sendAuctionStartNotificationToUser(Auction auction, Long memberIndex);

    // 서버 점검 알림 등록
    void notifyServerMaintenance(String title, String content);

    // 내가 올린 경매가 낙찰된 경우 알림
    void notifyAuctionSold(Member seller, Long auctionIndex);

    // 상위 입찰자 등장 시 알림
    void notifyHigherBid(Member bidder, Long auctionIndex);

    // 내가 경매에서 낙찰된 경우 알림
    void notifyAuctionWin(Member winner, Long auctionIndex);

    // 물품 배송/수령 확인 알림
    void notifyDeliveryConfirmation(Member sender, Member receiver, Long auctionIndex);

    List<NotificationDto> getNotificationsForMember(Long memberIndex);

    void notifyExchange(Member member, String title, String content);

    void notifyDeposit(Member member, String title, String content);

    void notifyDirectMessage(Member sender, Member receiver, String content, Long auctionIndex);

}
