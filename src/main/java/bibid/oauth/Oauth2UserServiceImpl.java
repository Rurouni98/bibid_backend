package bibid.oauth;

import bibid.entity.CustomUserDetails;
import bibid.repository.MemberRepository;
import bibid.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2UserServiceImpl extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request){
        OAuth2User oAuth2User = super.loadUser(request);

        String nickname = "";
        String providerId = "";

        Oauth2UserInfo oAuth2UserInfo = null;

        if(request.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

            providerId = oAuth2UserInfo.getProviderId();
            nickname = oAuth2UserInfo.getName();
        } else if(request.getClientRegistration().getRegistrationId().equals("naver")){
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());

            providerId = oAuth2UserInfo.getProviderId();
            nickname = oAuth2UserInfo.getName();
        } else if(request.getClientRegistration().getRegistrationId().equals("google")){

        } else {
            return null;
        }

        String provider = oAuth2UserInfo.getProvider();
        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        String password = passwordEncoder.encode(nickname);
        String email = oAuth2UserInfo.getEmail();

        Member member;

        if(memberRepository.findByMemberId(username).isPresent()) {
            member = memberRepository.findByMemberId(username)
                    .orElseThrow(() -> new RuntimeException("member not exist"));
        } else {
            member = Member.builder()
                    .memberId(username)
                    .memberPw(password)
                    .email(email)
                    .nickname(nickname)
                    .build();

            memberRepository.save(member);
        }

        return CustomUserDetails.builder()
                .member(member)
                .attributes(oAuth2User.getAttributes())
                .build();
    }
}
