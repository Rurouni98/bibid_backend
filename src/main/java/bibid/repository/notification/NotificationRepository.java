package bibid.repository.notification;

import bibid.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMember_MemberIndexAndIsViewedFalse(Long memberIndex); // 특정 회원의 읽지 않은 알림 조회
    List<Notification> findByMember_MemberIndexOrderByAlertDateDesc(Long memberIndex); // 특정 회원의 모든 알림 조회 (최근순)
}
