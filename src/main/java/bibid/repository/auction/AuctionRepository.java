package bibid.repository.auction;

import bibid.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    // 경매 타입과 시작 시간이 현재 이후인 경매 목록을 페이징 처리하여 조회
    @Query("SELECT a FROM Auction a WHERE a.auctionType = :auctionType AND a.startingLocalDateTime > :currentTime")
    Page<Auction> findAuctionsByType(@Param("auctionType") String auctionType,
                                     @Param("currentTime") LocalDateTime currentTime,
                                     Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.category = :category AND a.auctionType = '일반 경매'")
    Page<Auction> findByCategory(@Param("category") String category, Pageable sortedByViewCount);

    @Query("SELECT a FROM Auction a WHERE a.endingLocalDateTime > :currentTime AND a.auctionType= '일반 경매'")
    Page<Auction> findConveyor(@Param("currentTime") LocalDateTime currentTime, Pageable sortedByEndingLocalDateTime);
}
