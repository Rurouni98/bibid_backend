package bibid.repository.auction;

import bibid.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuctionRepositoryCustom {
    Page<Auction> findByCategory(String category, Pageable pageable);
    Page<Auction> findByCategory2(String category, Pageable pageable);
    Page<Auction> findBest(Pageable pageable);
    Page<Auction> findAllGeneralAuction(Pageable pageable);
    Page<Auction> findConveyor(LocalDateTime currentTime, Pageable pageable);
    Page<Auction> searchAll(String searchCondition, String searchKeyword, Pageable pageable);
}