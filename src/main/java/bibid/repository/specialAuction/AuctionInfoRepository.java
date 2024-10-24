package bibid.repository.specialAuction;

import bibid.entity.AuctionInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuctionInfoRepository extends JpaRepository<AuctionInfo, Long> {

    Optional<AuctionInfo> findByAuction_AuctionIndex(Long auctionIndex);

    List<AuctionInfo> findTop3ByAuction_AuctionIndexOrderByAuctionInfoIndexDesc(Long auctionIndex);

    long countByAuction_AuctionIndex(Long auctionIndex);

    @Query("SELECT MAX(a.bidAmount) FROM AuctionInfo a WHERE a.auction.auctionIndex = :auctionIndex")
    Optional<Long> findMaxBidAmountByAuctionIndex(@Param("auctionIndex") Long auctionIndex);

}
