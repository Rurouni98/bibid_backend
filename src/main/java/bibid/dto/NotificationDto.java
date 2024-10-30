package bibid.dto;

import bibid.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationDto {
    private Long notificationIndex;
    private Long memberIndex;
    private String alertTitle;
    private Map<String, Object> alertContent; // JSON 객체로 파싱된 내용
    private LocalDateTime alertDate;

    @Enumerated(EnumType.STRING)
    private NotificationType alertCategory;
    private boolean isViewed;
    private Long referenceIndex;
    private Boolean isSent;

    // Notification 엔티티로부터 DTO를 생성하는 생성자
    public NotificationDto(Notification notification) {
        this.notificationIndex = notification.getNotificationIndex();
        this.memberIndex = notification.getMember().getMemberIndex();
        this.alertTitle = notification.getAlertTitle();
        this.alertDate = notification.getAlertDate();
        this.alertCategory = notification.getAlertCategory();
        this.isViewed = notification.isViewed();
        this.referenceIndex = notification.getReferenceIndex();
        this.isSent = notification.getIsSent();

        // alertContent 필드를 JSON 문자열에서 Map으로 파싱
        try {
            this.alertContent = new ObjectMapper().readValue(notification.getAlertContent(), Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            this.alertContent = new HashMap<>(); // 파싱 실패 시 빈 Map
        }
    }

    // DTO를 Notification 엔티티로 변환
    public Notification toEntity(Member member) {
        String contentJson;
        try {
            // Map을 JSON 문자열로 변환
            contentJson = new ObjectMapper().writeValueAsString(this.alertContent);
        } catch (Exception e) {
            e.printStackTrace();
            contentJson = "";
        }

        return Notification.builder()
                .notificationIndex(this.notificationIndex)
                .member(member)
                .alertTitle(this.alertTitle)
                .alertContent(contentJson) // JSON 문자열로 저장
                .alertDate(this.alertDate)
                .isViewed(this.isViewed)
                .alertCategory(this.alertCategory)
                .referenceIndex(this.referenceIndex)
                .isSent(this.isSent)
                .build();
    }
}
