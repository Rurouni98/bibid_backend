package bibid.service.notification;

import bibid.dto.NotificationDto;
import bibid.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    // 특정 회원의 읽지 않은 알림 조회 메서드
    @Transactional(readOnly = true)
    List<NotificationDto> getUnreadNotifications(Long memberIndex);

    // 특정 회원의 모든 알림 조회 메서드 (최신순)
    @Transactional(readOnly = true)
    List<NotificationDto> getAllNotifications(Long memberIndex);

    // 특정 알림을 읽음 상태로 업데이트
    @Transactional
    void markAsViewed(Long notificationIndex);

    // 서버 점검 알림 등록
    void notifyServerMaintenance(String title, String content);

    void sendAuctionStartNotificationToUser(Auction auction, Long memberIndex, Long notificationIndex);

    Notification createScheduledNotification(Auction auction, Long memberIndex);

    // 내가 경매에서 낙찰된 경우 알림
    void notifyAuctionWin(Member winner, Long auctionIndex);

    List<NotificationDto> getNotificationsForMember(Long memberIndex);

    void notifyAuctionSold(Member seller, AuctionInfo lastBidInfo, Long auctionIndex);

    void notifyHigherBid(Member bidder, Long auctionIndex, Long higherBid, Long lowerBid);

    void notifyDeliveryConfirmation(Member sender, Member receiver, Long auctionIndex);

    void notifyExchange(Member member, String title, String content);

    void notifyDeposit(Member member, String title, String content);

    void notifyDirectMessage(Member sender, Member receiver, String content, Long auctionIndex);

}
