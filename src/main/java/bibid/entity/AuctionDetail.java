package bibid.entity;

import bibid.dto.AuctionDetailDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "auctionDetailSeqGenerator",
        sequenceName = "AUCTION_DETAIL_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionDetail {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "auctionDetailSeqGenerator"
    )
    private Long auctionDetailIndex;

    @OneToOne
    @JoinColumn(name = "auctionIndex")
    @JsonBackReference
    private Auction auction;

    private String shippingMethod; // 배송 방법 ('택배', '우편', '직거래')
    private String costResponsibility; // 비용 부담 ('선불', '착불')
    private Long winnerIndex; // 낙찰자 id
    private Long winningBid; // 낙찰 금액
    private String winnerNickname; // 낙찰자 닉네임

    public AuctionDetailDto toDto(){
        return AuctionDetailDto.builder()
                .auctionDetailIndex(this.auctionDetailIndex)
                .shippingMethod(this.shippingMethod)
                .costResponsibility(this.costResponsibility)
                .winnerIndex(this.winnerIndex)
                .winningBid(this.winningBid)
                .winnerNickname(this.winnerNickname)
                .auctionIndex(this.auction.getAuctionIndex())
                .build();
    }

}
