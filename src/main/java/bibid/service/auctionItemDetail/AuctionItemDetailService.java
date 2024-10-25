package bibid.service.auctionItemDetail;

import bibid.dto.*;
import bibid.entity.Member;

import java.util.List;

public interface AuctionItemDetailService {
    AuctionDto findAuctionItem(Long auctionIndex);

    AuctionInfoDto findAuctionBidInfo(Long auctionIndex);

    MemberDto findSeller(Long auctionIndex);

    List<AuctionInfoDto> findLastBidder(Long auctionIndex);

    List<MemberDto> findLastBidderName(List<AuctionInfoDto> auctionBidInfo);

    List<String> findAuctionInfoEtc(Long auctionIndex);

    SellerInfoDto findSellerInfo(Long auctionIndex);

    AuctionInfoDto updateAuctionItemDetail(Long auctionIndex, BidRequestDto bidRequestDto);

    void updateAuctionBiddingState();

    List<String> findAuctionImagesByAuctionIndex(Long auctionIndex);
}
