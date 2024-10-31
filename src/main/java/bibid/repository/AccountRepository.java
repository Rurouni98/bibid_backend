package bibid.repository;

import bibid.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByMemberIndex(Long memberIndex);
}
