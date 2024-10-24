package bibid.repository;

import bibid.entity.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {

    SellerInfo findByMember_MemberIndex(Long memberIndex);

}
