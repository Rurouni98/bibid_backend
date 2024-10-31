package bibid.dto;

import bibid.entity.Member;
import bibid.entity.ProfileImage;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDto {
    private Long memberIndex;
    private String name;
    private String oauthType;
    private String memberId;
    private String memberPw;
    private String nickname;
    private String email;
    private String memberPnum;
    private String role;
    private String memberAddress;
    private String addressDetail;
    private ProfileImageDto profileImageDto;
    private AccountDto accountDto;
    private Timestamp createTime;
    private String token;
    private String refreshToken;
    private Boolean rememberMe;
    private SellerInfoDto sellerInfoDto;

    public Member toEntity() {
        return Member.builder()
                .memberIndex(this.memberIndex)
                .name(this.name)
                .oauthType(this.oauthType)
                .memberId(this.memberId)
                .memberPw(this.memberPw)
                .nickname(this.nickname)
                .email(this.email)
                .memberPnum(this.memberPnum)
                .role(this.role)
                .memberAddress(this.memberAddress)
                .addressDetail(this.addressDetail)
                .profileImage(null)
                .account(null)
                .createTime(this.createTime)
                .refreshToken(this.refreshToken)
                .rememberMe(this.rememberMe)
                .sellerInfo(null)
                .build();
    }

}