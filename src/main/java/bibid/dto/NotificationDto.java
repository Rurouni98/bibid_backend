package bibid.dto;

import bibid.entity.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

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
    private String alertContent;
    private LocalDateTime alertDate;
    @Enumerated(EnumType.STRING)
    private NotificationType alertCategory;
    private boolean isViewed;
    private Long referenceIndex;

    public Notification toEntity(Member member) {
        return Notification.builder()
                .notificationIndex(this.notificationIndex)
                .member(member)
                .alertTitle(this.alertTitle)
                .alertContent(this.alertContent)
                .alertDate(this.alertDate)
                .isViewed(this.isViewed)
                .alertCategory(this.alertCategory)
                .referenceIndex(this.referenceIndex)
                .build();
    }

}
