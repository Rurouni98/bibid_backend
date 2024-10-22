package bibid.repository.specialAuction;

import bibid.entity.Streaming;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamingRepository extends JpaRepository<Streaming, Long> {

    public Optional<Streaming> findByAuction_AuctionIndex(Long auctionIndex);
}
