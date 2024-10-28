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
            strategy = GenerationType.IDENTITY,
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
    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private ProfileImage profileImage;
    @CreationTimestamp
    private Timestamp createTime;
    private String refreshToken;

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
                .profileImage(this.profileImage)
                .createTime(this.createTime)
                .refreshToken(this.refreshToken)
                .build();
    }
}