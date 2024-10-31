package bibid.entity;

import bibid.dto.AccountDto;
import bibid.dto.AddressDto;
import bibid.dto.MemberDto;
import bibid.dto.QnADto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "qnaSeqGenerator",
        sequenceName = "QNA_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnA {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "qnaSeqGenerator"
    )
    private Long qnaIndex;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String qnaContent;

    public QnADto toDto() {
        return QnADto.builder()
                .qnaIndex(this.qnaIndex)
                .memberIndex(this.member.getMemberIndex())
                .qnaContent(this.qnaContent)
                .build();
    }









}
