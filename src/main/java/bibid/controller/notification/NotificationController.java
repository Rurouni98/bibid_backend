package bibid.controller.notification;

import bibid.dto.NotificationDto;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // /api/notifications/{memberIndex} 엔드포인트 추가
    @GetMapping("/api/notifications/{memberIndex}")
    public ResponseEntity<?> getNotifications(@PathVariable("memberIndex") Long memberIndex) {
        log.info("Fetching notifications for member ID: {}", memberIndex);
        List<NotificationDto> notifications = notificationService.getNotificationsForMember(memberIndex);
        return ResponseEntity.ok(notifications);
    }

}
