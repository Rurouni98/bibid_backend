package bibid.repository.auction;

import bibid.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionDetailRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom{
}
