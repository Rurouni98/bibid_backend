package bibid.dto;

import bibid.entity.*;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccountUseHistoryDto {
    private Long accountUseHistoryIndex;
    private Long memberIndex;
    private Long auctionIndex;
    private Long historyIndex;
    private String useType;
    private String changeAccount;

    public AccountUseHistory toEntiy(Member member, Auction auction) {
        return AccountUseHistory.builder()
                .accountUseHistoryIndex(this.accountUseHistoryIndex)
                .member(member)
                .auction(auction)
                .useType(this.useType)
                .changeAccount(this.changeAccount)
                .build();
    }
}
