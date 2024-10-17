package bibid.dto;

import bibid.entity.Member;
import bibid.entity.ProfileImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDto {
    private Long memberIndex;
    private String name;
    private String memberId;
    private String memberPw;
    private String nickname;
    private String email;
    private String memberPnum;
    private String role;
    private String memberAddress;
    private String addressDetail;
    private ProfileImage profileImage;
    private String token;

    public Member toEntity() {
        return Member.builder()
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
                .profileImage(this.profileImage)
                .build();
    }
}