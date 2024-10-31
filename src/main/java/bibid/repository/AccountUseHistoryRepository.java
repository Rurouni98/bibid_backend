package bibid.repository;

import bibid.entity.AccountUseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountUseHistoryRepository extends JpaRepository<AccountUseHistory, Long> {

    void findByMember_MemberIndex(Long memberIndex);
    
}
