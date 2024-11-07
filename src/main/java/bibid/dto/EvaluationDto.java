package bibid.dto;

import bibid.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EvaluationDto {
    private Long evaluationIndex;
    private Long memberIndex;
    private String evaluationItem;
    private String evaluationRate;

    public Evaluation toEntity(Member member) {
        return Evaluation.builder()
                .evaluationIndex(this.evaluationIndex)
                .member(member)
                .evaluationItem(this.evaluationItem)
                .evaluationRate(this.evaluationRate)
                .build();
    }

}
