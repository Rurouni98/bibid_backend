package bibid.entity;

import bibid.dto.AuctionInfoDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "auctionInfoSeqGenerator",
        sequenceName = "AUCTION_INFO_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionInfo {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "auctionInfoSeqGenerator"
    )
    private Long auctionInfoIndex;

    @ManyToOne
    @JoinColumn(name = "auctionIndex", nullable = true) // 외래 키: 경매 ID
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "bidderIndex", nullable = true) // 외래 키로 변경
    private Member bidder; // 입찰자 정보

    private LocalDateTime bidTime; // 입찰시간
    private Long bidAmount; // 입찰금액
    private String bidderNickname;

    public AuctionInfoDto toDto() {
        return AuctionInfoDto.builder()
                .auctionInfoIndex(this.auctionInfoIndex)
                .auctionIndex(this.auction.getAuctionIndex())
                .bidderIndex(this.bidder.getMemberIndex())
                .bidTime(this.bidTime)
                .bidAmount(this.bidAmount)
                .bidderNickname(this.bidderNickname)
                .build();
    }

}
