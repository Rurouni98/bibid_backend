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
    private String alertCate; // "경매", "메시지"
    private boolean read;

    public Notification toEntiy(Member member) {
        return Notification.builder()
                .notificationIndex(this.notificationIndex)
                .member(member)
                .alertTitle(this.alertTitle)
                .alertContent(this.alertContent)
                .alertDate(this.alertDate)
                .read(this.read)
                .alertCate(this.alertCate)
                .build();
    }

}
