package bibid.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 권한 부여하는 테이블
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {
    private Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();

        auths.add(
                new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return member.getRole();
                    }
                }
        );

        return auths;
    }

    @Override
    public String getPassword() {
        return this.member.getMemberPw();
    }

    @Override
    public String getUsername() {
        return this.member.getMemberId();
    }
}
