package bibid.entity;

import bibid.dto.AccountDto;
import bibid.dto.AccountUseHistoryDto;
import bibid.dto.AddressDto;
import bibid.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "accountUseHistorySeqGenerator",
        sequenceName = "ACCOUNTUSEHISTORY_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUseHistory {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "accountUseHistorySeqGenerator"
    )
    private Long accountUseHistoryIndex;

    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;

    @OneToOne
    @JoinColumn(name = "auctionIndex")
    private Auction auction;

    private String useType;
    private String changeAccount;

    @ManyToOne
    @JoinColumn(name = "accountIndex")
    private Account account;

    public AccountUseHistoryDto toDto() {
        return AccountUseHistoryDto.builder()
                .accountUseHistoryIndex(this.accountUseHistoryIndex)
                .memberIndex(this.member.getMemberIndex())
                .auctionIndex(this.auction.getAuctionIndex())
                .useType(this.useType)
                .changeAccount(this.changeAccount)
                .accountIndex(this.account.getAccountIndex())
                .build();
    }









}
