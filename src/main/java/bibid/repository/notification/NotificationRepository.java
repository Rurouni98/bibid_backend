package bibid.repository.notification;

import bibid.entity.Notification;
import bibid.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMember_MemberIndexAndIsViewedFalse(Long memberIndex); // 특정 회원의 읽지 않은 알림 조회
    List<Notification> findByMember_MemberIndexOrderByAlertDateDesc(Long memberIndex); // 특정 회원의 모든 알림 조회 (최근순)

    List<Notification> findByMember_MemberIndex(Long memberIndex);

    List<Notification> findByIsSentFalse();

    List<Notification> findByMember_MemberIndexAndIsSentTrue(Long memberIndex);

    // 수정된 메서드: alertCategory 사용
    List<Notification> findByMember_MemberIndexAndIsSentTrueAndAlertCategory(Long memberIndex, NotificationType alertCategory);

    // 수정된 메서드: alertCategory 사용
    List<Notification> findByMember_MemberIndexAndAlertCategoryNot(Long memberIndex, NotificationType alertCategory);
}
