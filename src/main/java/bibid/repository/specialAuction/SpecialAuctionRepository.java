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
    // 경매 시작 시간이 30분 이내인 경매들 찾기
    @Query("SELECT b FROM Auction b WHERE b.startingLocalDateTime BETWEEN :past AND :now")
    List<Auction> findAuctionsStartingBefore(@Param("past") LocalDateTime past, @Param("now") LocalDateTime now);


    // 경매 타입과 시작 시간이 현재 이후인 경매 목록을 페이징 처리하여 조회
    @Query("SELECT a FROM Auction a WHERE a.auctionType = :auctionType AND a.startingLocalDateTime > :currentTime")
    Page<Auction> findAuctionsByType(@Param("auctionType") String auctionType,
                                     @Param("currentTime") LocalDateTime currentTime,
                                     Pageable pageable);
}
