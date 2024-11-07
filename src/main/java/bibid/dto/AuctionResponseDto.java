package bibid.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuctionResponseDto {
    private AuctionDto auctionItem;
    private MemberDto seller;
    private List<AuctionInfoDto> auctionBidInfo;
    private List<MemberDto> biddingMember;
    private List<String> infoExtension;
    private SellerInfoDto sellerDetailInfo;
    private List<String> auctionImages;
    private List<QnADto> qnAList;
}
