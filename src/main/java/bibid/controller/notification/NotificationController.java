package bibid.controller.notification;

import bibid.dto.NotificationDto;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

}
