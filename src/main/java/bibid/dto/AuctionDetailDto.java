package bibid.dto;

import bibid.entity.Auction;
import bibid.entity.AuctionDetail;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuctionDetailDto {

    private Long auctionDetailIndex;

    private Long auctionIndex;

    private String shippingMethod; // 배송 방법 ('택배', '우편', '직거래')
    private String costResponsibility; // 비용 부담 ('선불', '착불')
    private Long winnerIndex; // 낙찰자 id
    private Long winningBid; // 낙찰 금액
    private String winnerNickname; // 낙찰자 닉네임

    public AuctionDetail toEntity(Auction auction){
        return AuctionDetail.builder()
                .auctionDetailIndex(this.auctionDetailIndex)
                .auction(auction)
                .shippingMethod(this.shippingMethod)
                .costResponsibility(this.costResponsibility)
                .winnerIndex(this.winnerIndex)
                .winningBid(this.winningBid)
                .winnerNickname(this.winnerNickname)
                .build();
    }


}
