package bibid.repository;

import bibid.entity.AuctionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface auctionImageRepository extends JpaRepository<AuctionImage, Long> {

    List<AuctionImage> findByAuction_AuctionIndex(Long auctionIndex);

}
