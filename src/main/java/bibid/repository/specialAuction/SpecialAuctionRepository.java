package bibid.repository.specialAuction;

import bibid.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpecialAuctionRepository extends JpaRepository<Auction, Long> {

    @Query("SELECT a FROM Auction a WHERE a.auctionType = :auctionType AND a.endingLocalDateTime > :oneDayAgo")
    Page<Auction> findAuctionsByType(@Param("auctionType") String auctionType,
                                     @Param("oneDayAgo") LocalDateTime oneDayAgo,
                                     Pageable pageable);

    @Query("SELECT a FROM Auction a JOIN FETCH a.liveStationChannel WHERE a.auctionIndex = :auctionIndex")
    Optional<Auction> findByIdWithChannel(@Param("auctionIndex") Long auctionIndex);

    @Query("SELECT a FROM Auction a JOIN FETCH a.liveStationChannel WHERE a.auctionType = :auctionType AND a.startingLocalDateTime > :now")
    List<Auction> findAllWithChannelByAuctionTypeAndStartingLocalDateTimeAfter(@Param("auctionType") String auctionType,
                                                                               @Param("now") LocalDateTime now);

    List<Auction> findAllByAuctionTypeAndStartingLocalDateTimeBeforeAndLiveStationChannelIsNotNull(String auctionType, LocalDateTime time);

    List<Auction> findAllByAuctionTypeAndEndingLocalDateTimeAfter(String auctionType, LocalDateTime time);
}
