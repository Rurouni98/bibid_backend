package bibid.service.notification.impl;

import bibid.dto.NotificationDto;
import bibid.entity.Member;
import bibid.entity.Notification;
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

    // 알림 생성 메서드
    @Transactional
    @Override
    public NotificationDto createNotification(Member member, String title, String content, String category) {
        Notification notification = Notification.builder()
                .member(member)
                .alertTitle(title)
                .alertContent(content)
                .alertDate(LocalDateTime.now())
                .alertCategory(category)
                .isViewed(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return savedNotification.toDto();
    }

    // 특정 이벤트 발생 시 알림 생성 및 전송 메서드
    public void createAndSendNotification(Member member, String title, String content, String category) {
        Notification notification = Notification.builder()
                .member(member) // Member 엔티티로 매핑
                .alertTitle(title)
                .alertContent(content)
                .alertDate(LocalDateTime.now())
                .alertCategory(category)
                .isViewed(false)
                .build();

        // 알림을 저장
        notificationRepository.save(notification);

        // WebSocket으로 클라이언트에 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications/" + member.getMemberIndex(), notification.toDto());
    }

    // 특정 회원의 읽지 않은 알림 조회 메서드
    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> getUnreadNotifications(Long memberIndex) {
        return notificationRepository.findByMember_MemberIndexAndIsViewedFalse(memberIndex)
                .stream()
                .map(Notification::toDto)
                .collect(Collectors.toList());
    }

    // 특정 회원의 모든 알림 조회 메서드 (최신순)
    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> getAllNotifications(Long memberIndex) {
        return notificationRepository.findByMember_MemberIndexOrderByAlertDateDesc(memberIndex)
                .stream()
                .map(Notification::toDto)
                .collect(Collectors.toList());
    }

    // 특정 알림을 읽음 상태로 업데이트
    @Transactional
    @Override
    public void markAsViewed(Long notificationIndex) {
        Notification notification = notificationRepository.findById(notificationIndex)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림을 찾을 수 없습니다."));
        notification.setViewed(true);
        notificationRepository.save(notification);
    }

}
