package bibid.repository.member;

import bibid.entity.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {

    SellerInfo findByMember_MemberIndex(Long memberIndex);

}
