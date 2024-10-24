package bibid.repository.specialAuction;

import bibid.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpecialAuctionRepository extends JpaRepository<Auction, Long> {

    @Query("SELECT b FROM Auction b WHERE b.startingLocalDateTime BETWEEN :now AND :thirtyMinutesLater")
    List<Auction> findAuctionsStartingWithinThirtyMinutes(@Param("now") LocalDateTime now, @Param("thirtyMinutesLater") LocalDateTime thirtyMinutesLater);

    @Query("SELECT a FROM Auction a WHERE a.auctionType = :auctionType AND a.endingLocalDateTime > :oneDayAgo")
    Page<Auction> findAuctionsByType(@Param("auctionType") String auctionType,
                                     @Param("oneDayAgo") LocalDateTime oneDayAgo,
                                     Pageable pageable);

    @Query("SELECT COUNT(m) FROM Auction a JOIN a.member m WHERE a.auctionIndex = :auctionId AND m.memberIndex = :memberIndex")
    Long countMembersByAuctionIdAndType(@Param("auctionId") Long auctionId);

    List<Auction> findByAuctionStatusInAndAuctionType(List<String> statusList, String auctionType);

    List<Auction> findAllByAuctionTypeAndStartingLocalDateTimeAfter(String auctionType, LocalDateTime now);
}
