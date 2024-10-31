package bibid.entity;

import bibid.dto.AccountDto;
import bibid.dto.AccountUseHistoryDto;
import bibid.dto.AddressDto;
import bibid.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
            strategy = GenerationType.SEQUENCE,
            generator = "accountUseHistorySeqGenerator"
    )
    private Long accountUseHistoryIndex;

    @ManyToOne
    @JoinColumn(name = "memberIndex")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "auctionIndex", nullable = true)
    private Auction auction;

    private String useType; // 입찰(-), 반환(+), 충전(+), 환전(-)
    private String changeAccount;

    @ManyToOne
    @JoinColumn(name = "accountIndex")
    private Account account;

    private String beforeBalance;
    private String afterBalance;

    @CreationTimestamp
    private LocalDateTime createdTime;

    public AccountUseHistoryDto toDto() {
        return AccountUseHistoryDto.builder()
                .accountUseHistoryIndex(this.accountUseHistoryIndex)
                .memberIndex(this.member.getMemberIndex())
                .auctionIndex(this.auction != null ? this.auction.getAuctionIndex() : null)
                .useType(this.useType)
                .changeAccount(this.changeAccount)
                .accountIndex(this.account.getAccountIndex())
                .beforeBalance(this.beforeBalance)
                .afterBalance(this.afterBalance)
                .createdTime(this.createdTime)
                .productName(this.auction != null ? this.auction.getProductName() : null)
                .auctionType(this.auction != null ? this.auction.getAuctionType() : null)
                .build();
    }









}
