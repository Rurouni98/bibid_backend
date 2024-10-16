package bibid.dto;

import bibid.entity.Account;
import bibid.entity.Address;
import bibid.entity.Member;
import bibid.entity.QnA;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QnADto {
    private Long qnaIndex;
    private Long memberIndex;
    private String qnaContent;

    public QnA toEntiy(Member member) {
        return QnA.builder()
                .qnaIndex(this.qnaIndex)
                .member(member)
                .qnaContent(this.qnaContent)
                .build();
    }

}
