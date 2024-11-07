package bibid.entity;

import bibid.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

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
            strategy = GenerationType.SEQUENCE,
            generator = "memberSeqGenerator"
    )
    private Long memberIndex;

    private String name;
    private String oauthType = "Normal";

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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Account account;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private SellerInfo sellerInfo;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private ProfileImage profileImage;

    @CreationTimestamp
    private Timestamp createTime;

    private String refreshToken;
    private Boolean rememberMe;

    public MemberDto toDto() {
        return MemberDto.builder()
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
                .profileImageDto(this.profileImage != null ? this.profileImage.toDto() : null)
                .accountDto(this.account != null ? this.account.toDto() : null) // null 체크 추가
                .createTime(this.createTime)
                .refreshToken(this.refreshToken)
                .rememberMe(this.rememberMe)
                .sellerInfoDto(this.sellerInfo != null ? this.sellerInfo.toDto() : null) // null 체크 추가
                .build();
    }
}