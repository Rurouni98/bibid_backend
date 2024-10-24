package bibid.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidRequestDto {
    private String userBiddingType;
    private String userBiddingItemName;
    private String userBiddingCategory;
    private Long userBiddingPrice;
    private Long userBiddingTotalPrice;

}
