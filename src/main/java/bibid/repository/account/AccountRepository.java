package bibid.repository.account;

import bibid.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMember_MemberIndex(Long memberIndex);
}
