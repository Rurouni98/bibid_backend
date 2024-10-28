package bibid.dto;

import bibid.entity.*;
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
    private String alertCategory; // "구매/판매", "입금/출금","상위 입찰", "실시간 경매 시작"
    private boolean isViewed;

    public Notification toEntity(Member member) {
        return Notification.builder()
                .notificationIndex(this.notificationIndex)
                .member(member)
                .alertTitle(this.alertTitle)
                .alertContent(this.alertContent)
                .alertDate(this.alertDate)
                .isViewed(this.isViewed)
                .alertCategory(this.alertCategory)
                .build();
    }

}
