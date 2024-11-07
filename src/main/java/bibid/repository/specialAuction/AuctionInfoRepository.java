package bibid.repository.specialAuction;

import bibid.entity.Auction;
import bibid.entity.AuctionInfo;
import bibid.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionInfoRepository extends JpaRepository<AuctionInfo, Long> {

    Optional<AuctionInfo> findByAuction_AuctionIndex(Long auctionIndex);

    List<AuctionInfo> findTop3ByAuction_AuctionIndexOrderByAuctionInfoIndexDesc(Long auctionIndex);

    long countByAuction_AuctionIndex(Long auctionIndex);

    @Query("SELECT MAX(a.bidAmount) FROM AuctionInfo a WHERE a.auction.auctionIndex = :auctionIndex")
    Optional<Long> findMaxBidAmountByAuctionIndex(@Param("auctionIndex") Long auctionIndex);

    Optional<AuctionInfo> findTopByAuction_AuctionIndexOrderByBidAmountDescBidTimeDesc(Long auctionIndex);

    List<AuctionInfo> findByBidder_MemberIndex(Long memberIndex);

    List<AuctionInfo> findByAuctionAndBidderOrderByBidTimeDesc(Auction auction, Member bidder);

    Optional<AuctionInfo> findFirstByAuctionAndBidderNotAndBidTimeAfterOrderByBidAmountDesc(Auction auction, Member bidder, LocalDateTime bidTime);

    List<AuctionInfo> findByAuctionOrderByBidTimeDesc(Auction auction);
}
