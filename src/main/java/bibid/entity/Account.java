package bibid.entity;

import bibid.dto.AccountDto;
import bibid.dto.AddressDto;
import bibid.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            strategy = GenerationType.SEQUENCE,
            generator = "accountSeqGenerator"
    )
    private Long accountIndex;

    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;

    private String userMoney;

    // 경매 정보
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AccountUseHistory> accountUseHistoryList;

    public AccountDto toDto() {
        return AccountDto.builder()
                .accountIndex(this.accountIndex)
                .memberIndex(this.member.getMemberIndex())
                .userMoney(this.userMoney)
                .accountUseHistoryDtoList(
                        Optional.ofNullable(accountUseHistoryList).map(list -> list.stream().map(AccountUseHistory::toDto).toList())
                                .orElse(new ArrayList<>()))
                .build();
    }









}
