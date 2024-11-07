package bibid.service.mypage;

import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(username).orElseThrow(
                () -> new RuntimeException("member not exist")
        );

        return CustomUserDetails.builder()
                .member(member)
                .build();
    }
}
