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

    // 특정 멤버의 모든 알림 조회
    @GetMapping("/api/notifications/{memberIndex}")
    public ResponseEntity<?> getNotifications(@PathVariable("memberIndex") Long memberIndex) {
        log.info("Fetching notifications for member ID: {}", memberIndex);
        List<NotificationDto> notifications = notificationService.getNotificationsForMember(memberIndex);

        return ResponseEntity.ok(notifications);
    }
}
