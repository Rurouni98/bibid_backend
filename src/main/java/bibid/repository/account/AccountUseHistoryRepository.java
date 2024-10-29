package bibid.repository.account;

import bibid.entity.AccountUseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountUseHistoryRepository extends JpaRepository<AccountUseHistory, Long> {
}
