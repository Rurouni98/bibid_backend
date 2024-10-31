package bibid.repository.qna;

import bibid.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<QnA, Long> {

    List<QnA> findByAuction_AuctionIndex(Long auctionIndex);

}
