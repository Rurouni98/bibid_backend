package bibid.repository.auction;

import bibid.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT a FROM Auction a WHERE a.category = :category AND a.auctionType = '일반 경매'")
    Page<Auction> findByCategory(@Param("category") String category, Pageable sortedByViewCount);

    @Query("SELECT a FROM Auction a WHERE a.auctionType = '일반 경매'")
    Page<Auction> findAllGeneralAuction(Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.category = :category AND a.auctionType = '일반 경매' ORDER BY a.regdate DESC")
    Page<Auction> findByCategory2(@Param("category") String category, Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.endingLocalDateTime > :currentTime AND a.auctionType= '일반 경매'")
    Page<Auction> findConveyor(@Param("currentTime") LocalDateTime currentTime, Pageable sortedByEndingLocalDateTime);

    @Query("SELECT a FROM Auction a WHERE " +
            "(:searchCondition IS NULL OR " +
            "(:searchCondition = 'productName' AND a.productName LIKE %:searchKeyword%) OR " +
            "(:searchCondition = 'category' AND a.category LIKE %:searchKeyword%) OR " +
            "(:searchCondition = 'productDescription' AND a.productDescription LIKE %:searchKeyword%)) " +
            "AND a.auctionType = '일반 경매' " +
            "ORDER BY a.regdate DESC")
    Page<Auction> searchAll(@Param("searchCondition") String searchCondition,
                            @Param("searchKeyword") String searchKeyword,
                            Pageable pageable);
}
