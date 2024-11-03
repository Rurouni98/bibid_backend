package bibid.repository.auction;

import bibid.entity.Auction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {
//    @Query("SELECT a FROM Auction a WHERE a.category = :category AND a.auctionType = '일반 경매' ORDER BY a.regdate DESC")
//    Page<Auction> findByCategory(@Param("category") String category, Pageable sortedByViewCount);
//
//    @Query("SELECT a FROM Auction a WHERE a.auctionType = '일반 경매'")
//    Page<Auction> findAllGeneralAuction(Pageable pageable);
//
//    @Query("SELECT a FROM Auction a WHERE a.category = :category AND a.auctionType = '일반 경매' ORDER BY a.regdate DESC")
//    Page<Auction> findByCategory2(@Param("category") String category, Pageable pageable);
//
//    @Query("SELECT a FROM Auction a WHERE a.endingLocalDateTime > :currentTime AND a.auctionType= '일반 경매'")
//    Page<Auction> findConveyor(@Param("currentTime") LocalDateTime currentTime, Pageable sortedByEndingLocalDateTime);
//
//    @Query("SELECT a FROM Auction a WHERE " +
//            "(:searchCondition = 'all' AND (a.productName LIKE %:searchKeyword% OR " +
//            "a.category LIKE %:searchKeyword% OR " +
//            "a.productDescription LIKE %:searchKeyword%)) OR " +
//            "(:searchCondition = 'productName' AND a.productName LIKE %:searchKeyword%) OR " +
//            "(:searchCondition = 'category' AND a.category LIKE %:searchKeyword%) OR " +
//            "(:searchCondition = 'productDescription' AND a.productDescription LIKE %:searchKeyword%) " +
//            "AND a.auctionType = '일반 경매' " +
//            "ORDER BY a.regdate DESC")
//    Page<Auction> searchAll(@Param("searchCondition") String searchCondition,
//                            @Param("searchKeyword") String searchKeyword,
//                            Pageable pageable);

    long countByMember_MemberIndex(Long memberIndex);

    @Modifying
    @Query("UPDATE Auction a SET a.auctionStatus = '경매 시작' WHERE a.startingLocalDateTime <= :currentTime AND a.auctionStatus = '대기중' AND a.auctionType = :auctionType")
    void updateOngoingAuctions(@Param("currentTime") LocalDateTime currentTime, @Param("auctionType") String auctionType);

    List<Auction> findByEndingLocalDateTimeBeforeAndAuctionStatusAndAuctionType(LocalDateTime currentTime, String auctionStatus, String auctionType);

    List<Auction> findByMember_MemberIndex(Long memberIndex);

    @Modifying
    @Transactional
    @Query("DELETE FROM Auction a WHERE a.auctionIndex = :auctionIndex")
    void deleteByAuctionIndex(@Param("auctionIndex") Long auctionIndex);


    @Modifying
    @Transactional
    @Query("UPDATE Auction a SET a.viewCnt = a.viewCnt + 1 WHERE a.auctionIndex = :auctionIndex")
    void updateAuctionViewCnt(Long auctionIndex);

    @Query("SELECT a FROM Auction a " +
            "LEFT JOIN FETCH a.auctionInfoList " +
            "LEFT JOIN FETCH a.auctionDetail " +
            "LEFT JOIN FETCH a.liveStationChannel " +
            "WHERE a.auctionIndex = :auctionIndex")
    Optional<Auction> findByIdWithAllDetails(@Param("auctionIndex") Long auctionIndex);

    @Query("SELECT a FROM Auction a LEFT JOIN FETCH a.liveStationChannel WHERE a.auctionIndex = :auctionIndex")
    Optional<Auction> findByIdWithChannel(@Param("auctionIndex") Long auctionIndex);
}
