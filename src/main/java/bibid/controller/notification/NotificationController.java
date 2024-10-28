package bibid.controller.notification;

import bibid.dto.NotificationDto;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    // 알림을 특정 회원에게 전송하는 메서드
    @MessageMapping("/notifications/send")
    public void sendNotification(NotificationDto notificationDto) {

        log.info("Sending notification to member ID: {}", notificationDto.getMemberIndex());
        messagingTemplate.convertAndSend("/topic/notifications/" + notificationDto.getMemberIndex(), notificationDto);
        log.info("Notification sent to /topic/notifications/{}", notificationDto.getMemberIndex());
    }

    @GetMapping("/send-test-notification")
    public ResponseEntity<String> sendTestNotification() {
        Map<String, String> message = new HashMap<>();
        message.put("title", "Test Notification");
        message.put("content", "This is a test message.");
        messagingTemplate.convertAndSend("/topic/notifications", message);
        return ResponseEntity.ok("Notification sent!");
    }


}
