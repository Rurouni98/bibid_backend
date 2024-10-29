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
    private String useType;
    private String changeAccount;
    private Long accountIndex;

    public AccountUseHistory toEntity(Member member, Auction auction, Account account) {
        return AccountUseHistory.builder()
                .accountUseHistoryIndex(this.accountUseHistoryIndex)
                .member(member)
                .auction(auction)
                .useType(this.useType)
                .changeAccount(this.changeAccount)
                .account(account)
                .build();
    }
}
