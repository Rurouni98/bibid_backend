package bibid.service.account;

import bibid.dto.AccountDto;
import bibid.dto.AccountUseHistoryDto;
import bibid.entity.Account;

public interface AccountService {
    AccountDto chargeAccount(AccountUseHistoryDto accountUseHistoryDto, Long MemberIndex);

    AccountDto exchangeAccount(AccountUseHistoryDto accountUseHistoryDto, Long MemberIndex);

    AccountDto buyAuction(AccountUseHistoryDto accountUseHistoryDto, Long MemberIndex);

    AccountDto sellAuction(AccountUseHistoryDto accountUseHistoryDto, Long MemberIndex);

    Account findMemberIndex(Long memberIndex);
}
