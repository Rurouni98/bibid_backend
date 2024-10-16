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
    private boolean viewEd;
    private String alertCat;

    public Notification toEntiy(Member member) {
        return Notification.builder()
                .notificationIndex(this.notificationIndex)
                .member(member)
                .alertTitle(this.alertTitle)
                .alertContent(this.alertContent)
                .alertDate(this.alertDate)
                .viewEd(this.viewEd)
                .alertCat(this.alertCat)
                .build();
    }

}
