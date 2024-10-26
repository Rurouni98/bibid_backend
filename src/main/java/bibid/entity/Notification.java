package bibid.entity;

import bibid.dto.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "notificationSeqGenerator",
        sequenceName = "NOTIFICATION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "notificationSeqGenerator"
    )
    private Long notificationIndex;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String alertTitle;
    private String alertContent;
    private LocalDateTime alertDate;
    private String alertCate;
    private boolean read;

    public NotificationDto toDto() {
        return NotificationDto.builder()
                .notificationIndex(this.notificationIndex)
                .memberIndex(this.member.getMemberIndex())
                .alertTitle(this.alertTitle)
                .alertContent(this.alertContent)
                .alertDate(this.alertDate)
                .read(this.read)
                .alertCate(this.alertCate)
                .build();
    }









}
