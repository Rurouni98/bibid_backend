package bibid.dto;

import bibid.entity.Account;
import bibid.entity.Address;
import bibid.entity.Member;
import lombok.*;

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

    public Account toEntiy(Member member) {
        return Account.builder()
                .accountIndex(this.accountIndex)
                .member(member)
                .userMoney(this.userMoney)
                .build();
    }
}
