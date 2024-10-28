package bibid.oauth2;

import bibid.dto.ResponseDto;
import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.jwt.JwtProvider;
import bibid.repository.member.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoServiceImpl {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    // ㅁ [1번] 코드로 카카오에서 토큰 받기
    public OauthTokenDto getAccessToken(String code) {
        log.info("getAccessToken called with code: {}", code);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "29e81fa9fda262c573f312af9934fa5c");
        params.add("redirect_uri", "http://localhost:3000/auth/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> accessTokenResponse = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );
            log.info("Received access token response: {}", accessTokenResponse.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            OauthTokenDto oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthTokenDto.class);
            log.info("Parsed oauthToken: {}", oauthToken);

            Member refreshTokenMember = Member.builder()
                    .refreshToken(oauthToken.getRefresh_token())
                    .build();

            return oauthToken;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse access token response", e);
            return null;
        }
    }

    // ㅁ [2번] 카카오에서 받은 액세스 토큰으로 카카오에서 사용자 정보 받아오기
    public KakaoProfileDto findProfile(String kakaoAccessToken) {
        log.info("findProfile called with kakaoAccessToken: {}", kakaoAccessToken);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoProfileRequest,
                    String.class
            );
            log.info("Received kakao profile response: {}", kakaoProfileResponse.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfileDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse kakao profile response", e);
            return null;
        }
    }

    // ㅁ [3번] 카카오에서 받은 액세스 토큰으로 정보저장 + Jwt 토큰 얻어서 토큰생성
    public String saveUserAndGetToken(String kakaoAccessToken) {
        log.info("saveUserAndGetToken called with kakaoAccessToken: {}", kakaoAccessToken);

        KakaoProfileDto profile = findProfile(kakaoAccessToken);
        if (profile == null) {
            log.warn("Profile not found for given access token.");
            return null;
        }

        Member kakaoMember = memberRepository.findByEmail(profile.getKakao_account().getEmail());

        if (kakaoMember == null) {
            kakaoMember = Member.builder()
                    .memberId(profile.getKakao_account().getProfile().getNickname())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
                    .role("ROLE_USER")
                    .oauthType("Kakao")
                    .build();
            memberRepository.save(kakaoMember);
            log.info("New member saved: {}", kakaoMember);
        }

        String jwtToken = jwtProvider.createOauthJwt(kakaoMember);
        log.info("Generated JWT token: {}", jwtToken);

        return jwtToken;
    }

    // ㅁ [4-1번] DB에서 리프레시토큰 가져오기
    public ResponseEntity<?> getTokenAndType(String jwtTokenValue, Principal principal) {
        log.info("getTokenAndType called with jwtTokenValue: {}", jwtTokenValue);

        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();
        Map<String, String> item = new HashMap<>();

        try {
            if (principal == null) {
                log.warn("Principal is null");
                throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
            }

            String username = principal.getName();
            log.info("Authenticated username: {}", username);

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
            Member memberId = userDetails.getMember();

            String findMemberNickname = memberId.getNickname();
            log.info("Found member nickname: {}", findMemberNickname);

            Member member = memberRepository.findByNickname(findMemberNickname);
            log.info("Found member by nickname: {}", member);

            String token = member.getRefreshToken();
            String type = member.getOauthType();

            item.put("token", token);
            item.put("type", type);

            responseDto.setItem(item);

            return ResponseEntity.ok(responseDto);

        } catch (RuntimeException e) {
            log.error("Error occurred while retrieving token and type", e);
            throw new RuntimeException(e);
        }
    }
}
