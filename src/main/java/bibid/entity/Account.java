package bibid.entity;

import bibid.dto.AccountDto;
import bibid.dto.AddressDto;
import bibid.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "accountSeqGenerator",
        sequenceName = "ACCOUNT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "accountSeqGenerator"
    )
    private Long accountIndex;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String userMoney;

    public AccountDto toDto() {
        return AccountDto.builder()
                .accountIndex(this.accountIndex)
                .memberIndex(this.member.getMemberIndex())
                .userMoney(this.userMoney)
                .build();
    }









}
