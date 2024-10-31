package bibid.dto;

import bibid.entity.*;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;

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
    private String productName;
    private String auctionType;
    private String useType;
    private String changeAccount;
    private Long accountIndex;
    private String beforeBalance;
    private String afterBalance;
    private LocalDateTime createdTime;

    public AccountUseHistory toEntity(Member member, Auction auction, Account account) {
        return AccountUseHistory.builder()
                .accountUseHistoryIndex(this.accountUseHistoryIndex)
                .member(member)
                .auction(auction)
                .useType(this.useType)
                .changeAccount(this.changeAccount)
                .account(account)
                .beforeBalance(this.beforeBalance)
                .afterBalance(this.afterBalance)
                .createdTime(this.createdTime)
                .build();
    }
}
