package bibid.repository.specialAuction;

import bibid.entity.AuctionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionInfoRepository extends JpaRepository<AuctionInfo, Long> {
}
