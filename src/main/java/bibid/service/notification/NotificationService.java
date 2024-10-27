package bibid.service.notification;

import bibid.dto.NotificationDto;
import bibid.entity.Member;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {
    // 알림 생성 메서드
    @Transactional
    NotificationDto createNotification(Member member, String title, String content, String category);

    // 특정 회원의 읽지 않은 알림 조회 메서드
    @Transactional(readOnly = true)
    List<NotificationDto> getUnreadNotifications(Long memberIndex);

    // 특정 회원의 모든 알림 조회 메서드 (최신순)
    @Transactional(readOnly = true)
    List<NotificationDto> getAllNotifications(Long memberIndex);

    // 특정 알림을 읽음 상태로 업데이트
    @Transactional
    void markAsViewed(Long notificationIndex);
}
