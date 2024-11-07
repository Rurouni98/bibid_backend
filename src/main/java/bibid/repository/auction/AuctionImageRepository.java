package bibid.repository.auction;

import bibid.entity.AuctionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long> {

    List<AuctionImage> findByAuction_AuctionIndex(Long auctionIndex);

}
