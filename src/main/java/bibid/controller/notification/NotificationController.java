package bibid.controller.notification;

import bibid.dto.NotificationDto;
import bibid.entity.Auction;
import bibid.entity.Member;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;

    // 특정 멤버의 모든 알림 조회
    @GetMapping("/api/notifications/{memberIndex}")
    public ResponseEntity<?> getNotifications(@PathVariable("memberIndex") Long memberIndex) {
        log.info("Fetching notifications for member ID: {}", memberIndex);
        List<NotificationDto> notifications = notificationService.getNotificationsForMember(memberIndex);

        return ResponseEntity.ok(notifications);
    }

    // 경매 시작 알림 전송
    @PostMapping("/api/notifications/auctionStart")
    public ResponseEntity<?> sendAuctionStartNotification(@RequestBody Map<String, Long> payload) {
        Long memberIndex = payload.get("memberIndex");
        Long auctionIndex = payload.get("auctionIndex");

        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("옥션을 찾을 수 없습니다."));
        notificationService.sendAuctionStartNotificationToUser(auction, memberIndex);
        return ResponseEntity.ok("경매 시작 알림 전송 성공");
    }

    // 경매 낙찰 알림 전송
    @PostMapping("/api/notifications/auctionWin")
    public ResponseEntity<?> sendAuctionWinNotification(@RequestBody Map<String, Long> payload) {
        Long memberIndex = payload.get("memberIndex");
        Long auctionIndex = payload.get("auctionIndex");

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        notificationService.notifyAuctionWin(member, auctionIndex);
        return ResponseEntity.ok("낙찰 알림 전송 성공");
    }

    // 경매 판매 확인 알림 전송
    @PostMapping("/api/notifications/auctionSold")
    public ResponseEntity<?> sendAuctionSoldNotification(@RequestBody Map<String, Long> payload) {
        Long memberIndex = payload.get("memberIndex");
        Long auctionIndex = payload.get("auctionIndex");

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        notificationService.notifyAuctionSold(member, auctionIndex);
        return ResponseEntity.ok("경매 낙찰 알림 전송 성공");
    }

//    // 상위 입찰자 알림 전송
//    @PostMapping("/api/notifications/higherBid")
//    public ResponseEntity<?> sendHigherBidNotification(@RequestBody Map<String, Long> payload) {
//        Long memberIndex = payload.get("memberIndex");
//        Long auctionIndex = payload.get("auctionIndex");
//
//        log.info("Received higherBid notification request for memberIndex: {}, auctionIndex: {}", memberIndex, auctionIndex);
//
//        Member member = memberRepository.findById(memberIndex)
//                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
//
//        notificationService.notifyHigherBid(member, auctionIndex);
//
//        return ResponseEntity.ok("상위 입찰자 알림 전송 성공");
//    }

    // 서버 점검 알림 전송
    @PostMapping("/api/notifications/serverMaintenance")
    public ResponseEntity<?> sendServerMaintenanceNotification(@RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String content = payload.get("content");

        notificationService.notifyServerMaintenance(title, content);
        return ResponseEntity.ok("서버 점검 알림 전송 성공");
    }

    // 배송 확인 알림 전송
    @PostMapping("/api/notifications/deliveryConfirmation")
    public ResponseEntity<?> sendDeliveryConfirmation(@RequestBody Map<String, Long> payload) {
        Long senderIndex = payload.get("senderIndex");
        Long receiverIndex = payload.get("receiverIndex");
        Long auctionIndex = payload.get("auctionIndex");

        Member sender = memberRepository.findById(senderIndex)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사용자를 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(receiverIndex)
                .orElseThrow(() -> new IllegalArgumentException("받는 사용자를 찾을 수 없습니다."));
        notificationService.notifyDeliveryConfirmation(sender, receiver, auctionIndex);
        return ResponseEntity.ok("배송 확인 알림 전송 성공");
    }

    // 환전 알림 전송
    @PostMapping("/api/notifications/exchange")
    public ResponseEntity<?> sendExchangeNotification(@RequestBody Map<String, Object> payload) {
        Long memberIndex = ((Number) payload.get("memberIndex")).longValue();
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        notificationService.notifyExchange(member, title, content);
        return ResponseEntity.ok("환전 알림 전송 성공");
    }

    // 입금 알림 전송
    @PostMapping("/api/notifications/deposit")
    public ResponseEntity<?> sendDepositNotification(@RequestBody Map<String, Object> payload) {
        Long memberIndex = ((Number) payload.get("memberIndex")).longValue();
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        notificationService.notifyDeposit(member, title, content);
        return ResponseEntity.ok("입금 알림 전송 성공");
    }

    // DM 알림 전송
    @PostMapping("/api/notifications/directMessage")
    public ResponseEntity<?> sendDirectMessageNotification(@RequestBody Map<String, Object> payload) {
        Long senderIndex = ((Number) payload.get("senderIndex")).longValue();
        Long receiverIndex = ((Number) payload.get("receiverIndex")).longValue();
        String content = (String) payload.get("content");
        Long auctionIndex = ((Number) payload.get("auctionIndex")).longValue();

        Member sender = memberRepository.findById(senderIndex)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사용자를 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(receiverIndex)
                .orElseThrow(() -> new IllegalArgumentException("받는 사용자를 찾을 수 없습니다."));
        notificationService.notifyDirectMessage(sender, receiver, content, auctionIndex);
        return ResponseEntity.ok("직접 메시지 알림 전송 성공");
    }
}
