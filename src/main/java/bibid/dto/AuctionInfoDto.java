package bibid.dto;

import bibid.entity.Auction;
import bibid.entity.AuctionInfo;
import bibid.entity.Member;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuctionInfoDto {
    private Long auctionInfoIndex;
    private Long auctionIndex;
    private Long bidderIndex;
    private LocalDateTime bidTime; // 입찰시간
    private Long bidAmount; // 입찰금액
    private String bidderNickname;

    public AuctionInfo toEntity(Auction auction, Member bidder) {
        return AuctionInfo.builder()
                .auctionInfoIndex(this.auctionInfoIndex)
                .auction(auction)
                .bidder(bidder)
                .bidTime(this.bidTime)
                .bidAmount(this.bidAmount)
                .bidderNickname(this.bidderNickname)
                .build();
    }
}
