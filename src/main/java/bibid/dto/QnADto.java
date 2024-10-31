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
public class QnADto {
    private Long qnaIndex;
    private Long memberIndex;
    private String qnaTitle;
    private String qnaContent;
    private Long auctionIndex;
    private LocalDateTime regDate;
    private String nickname;

    public QnA toEntity(Member member) {
        return QnA.builder()
                .qnaIndex(this.qnaIndex)
                .member(member)
                .qnaTitle(this.qnaTitle)
                .qnaContent(this.qnaContent)
                .regDate(this.regDate)
                .build();
    }

    public QnA toEntiy(Auction auction) {
        return QnA.builder()
                .qnaIndex(this.qnaIndex)
                .auction(auction)
                .qnaTitle(this.qnaTitle)
                .qnaContent(this.qnaContent)
                .regDate(this.regDate)
                .build();
    }

}
