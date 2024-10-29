package bibid.entity;

import bibid.dto.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "evaluationSeqGenerator",
        sequenceName = "EVALUATION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "evaluationSeqGenerator"
    )
    private Long evaluationIndex;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String evaluationItem;
    private String evaluationRate;

    public EvaluationDto toDto() {
        return EvaluationDto.builder()
                .memberIndex(this.member.getMemberIndex())
                .evaluationItem(this.evaluationItem)
                .evaluationRate(this.evaluationRate)
                .build();
    }









}
