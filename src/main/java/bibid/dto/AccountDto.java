package bibid.dto;

import bibid.entity.Account;
import bibid.entity.Address;
import bibid.entity.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccountDto {
    private Long accountIndex;
    private Long memberIndex;
    private String userMoney;
    private List<AccountUseHistoryDto> accountUseHistoryDtoList;

    public Account toEntity(Member member) {
        return Account.builder()
                .accountIndex(this.accountIndex)
                .member(member)
                .userMoney(this.userMoney)
                .accountUseHistoryList(new ArrayList<>())
                .build();
    }
}
