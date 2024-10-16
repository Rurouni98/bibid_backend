package bibid.entity;

import bibid.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "memberSeqGenerator",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "memberSeqGenerator"
    )
    private Long memberIndex;
    private String name;
    @Column(unique = true)
    private String memberId;
    private String memberPw;
    @Column(unique = true)
    private String nickname;
    private String email;
    private String memberPnum;
    private String role;
    private String memberAddress;
    private String addressDetail;
    private String profileUrl;
    private boolean isSeller;

    public MemberDto toDto() {
        return MemberDto.builder()
                .memberIndex(this.memberIndex)
                .name(this.name)
                .memberId(this.memberId)
                .memberPw(this.memberPw)
                .nickname(this.nickname)
                .email(this.email)
                .memberPnum(this.memberPnum)
                .role(this.role)
                .memberAddress(this.memberAddress)
                .addressDetail(this.addressDetail)
                .profileUrl(this.profileUrl)
                .build();
    }









}
