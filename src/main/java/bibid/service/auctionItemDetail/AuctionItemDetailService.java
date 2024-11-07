package bibid.service.auctionItemDetail;

import bibid.dto.*;
import bibid.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface AuctionItemDetailService {
    AuctionDto findAuctionItem(Long auctionIndex);

    AuctionInfoDto findAuctionBidInfo(Long auctionIndex);

    MemberDto findSeller(Long auctionIndex);

    List<AuctionInfoDto> findLastBidder(Long auctionIndex);

    List<MemberDto> findLastBidderName(List<AuctionInfoDto> auctionBidInfo);

    List<String> findAuctionInfoEtc(Long auctionIndex);

    SellerInfoDto findSellerInfo(Long auctionIndex);

    AuctionInfoDto updateAuctionItemDetail(Long auctionIndex, BidRequestDto bidRequestDto, Member member);

    @Scheduled(fixedRate = 60000)
    @Transactional
    void updateOngoingAuctions();

    @Scheduled(fixedRate = 60000)
    @Transactional
    void updateCompletedAuctionStatus();

    List<String> findAuctionImagesByAuctionIndex(Long auctionIndex);

    String auctionChecking(Long auctionIndex);

    void plusAuctionView(Long auctionIndex);

    String biddingItem(Long auctionIndex, BidRequestDto bidRequestDto, Long memberIndex);

    // 맴버 인덱스도 추가해주어야됨
    String biddingItem(Long auctionIndex, BidRequestDto bidRequestDto, MemberDto memberDto);
}
