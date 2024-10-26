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
            strategy = GenerationType.SEQUENCE,
            generator = "notificationSeqGenerator"
    )
    private Long notificationIndex;

    @ManyToOne
    @JoinColumn(name = "memberIndex")
    private Member member;

    private String alertTitle;
    private String alertContent;
    private LocalDateTime alertDate;
    private String alertCategory; // "구매/판매", "입금/출금","상위 입찰", "실시간 경매 시작"
    private boolean isViewed;

    public NotificationDto toDto() {
        return NotificationDto.builder()
                .notificationIndex(this.notificationIndex)
                .memberIndex(this.member.getMemberIndex())
                .alertTitle(this.alertTitle)
                .alertContent(this.alertContent)
                .alertDate(this.alertDate)
                .isViewed(this.isViewed)
                .alertCategory(this.alertCategory)
                .build();
    }









}
