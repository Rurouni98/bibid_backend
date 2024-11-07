package bibid.entity;

import bibid.dto.AccountDto;
import bibid.dto.AddressDto;
import bibid.dto.MemberDto;
import bibid.dto.QnADto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @ManyToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String qnaTitle;
    private String qnaContent;
    @ManyToOne
    @JoinColumn(name = "auctionIndex")
    private Auction auction;
    private LocalDateTime regDate;

    public QnADto toDto() {
        return QnADto.builder()
                .qnaIndex(this.qnaIndex)
                .memberIndex(this.member.getMemberIndex())
                .qnaTitle(this.qnaTitle)
                .qnaContent(this.qnaContent)
                .auctionIndex(this.qnaIndex)
                .regDate(this.regDate)
                .build();
    }









}
