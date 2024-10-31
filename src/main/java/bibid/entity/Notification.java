package bibid.entity;

import bibid.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Enumerated(EnumType.STRING)
    private NotificationType  alertCategory; // "경매 시작", "낙찰", "서버 점검", "상위 입찰" etc

    private boolean isViewed;
    private Long referenceIndex;
    private Boolean isSent;

    public NotificationDto toDto() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parsedAlertContent;

        // alertContent를 JSON 문자열에서 Map으로 변환
        try {
            parsedAlertContent = objectMapper.readValue(this.alertContent, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            parsedAlertContent = new HashMap<>(); // 파싱 실패 시 빈 Map
        }

        // DTO 생성 시 파싱된 alertContent 전달
        return NotificationDto.builder()
                .notificationIndex(this.notificationIndex)
                .memberIndex(this.member.getMemberIndex())
                .alertTitle(this.alertTitle)
                .alertContent(parsedAlertContent)
                .alertDate(this.alertDate)
                .isViewed(this.isViewed)
                .alertCategory(this.alertCategory)
                .referenceIndex(this.referenceIndex)
                .isSent(this.isSent)
                .build();
    }
}
