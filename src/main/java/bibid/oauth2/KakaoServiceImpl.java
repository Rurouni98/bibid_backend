package bibid.oauth2;

import bibid.dto.MemberDto;
import bibid.entity.Member;
import bibid.jwt.JwtProvider;
import bibid.repository.member.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.Cookie;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Base64;


@Service
public class KakaoServiceImpl {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtProvider jwtProvider;

    String jwtValue;

    // ㅁ [1번] 코드로 카카오에서 토큰 받기
    public OauthTokenDto getAccessToken(String code) {

        //(2)
        RestTemplate rt = new RestTemplate();

        //(3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "29e81fa9fda262c573f312af9934fa5c");
        params.add("redirect_uri", "http://localhost:3000/auth/kakao/callback");
        params.add("code", code);

        //(5)
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        //(6)
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //(7)
        ObjectMapper objectMapper = new ObjectMapper();
        OauthTokenDto oauthToken = null;
        Member refreshTokenMember = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthTokenDto.class);

            System.out.println(oauthToken);
            refreshTokenMember = Member.builder()
                    .refreshToken(oauthToken.getRefresh_token())
                    .build();
            return oauthToken;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        //(8)
    }

    // ㅁ [2번] 카카오에서 받은 액세스 토큰으로 카카오에서 사용자 정보 받아오기
    public KakaoProfileDto findProfile(String kakaoAccessToken) {

        //(1-2)
        RestTemplate rt = new RestTemplate();

        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(1-5)
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfileDto kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfileDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;

    }

    // ㅁ [3번] 카카오에서 받은 액세스 토큰으로 정보저장 + Jwt 토큰 얻어서 토큰생성
    public String saveUserAndGetToken(String kakaoAccessToken) {

        Member kakaoMember = null;

        KakaoProfileDto profile = findProfile(kakaoAccessToken);

        kakaoMember = memberRepository.findByEmail(profile.getKakao_account().getEmail());


        if (kakaoMember == null) {
            kakaoMember = Member.builder()
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
//                    .refreshToken(profile.getKakao_account().)
                    .role("ROLE_USER")
                    .oauthType("Kakao")
                    .build();

            memberRepository.save(kakaoMember);

        }
        return jwtProvider.createOauthJwt(kakaoMember); //(2)
    }

    // ㅁ [4번] 카카오에서 받은 refresh_token으로 access_token leload 하기
    public OauthTokenDto leLoadToken (Long memberIndex){

//        String refreshToken = findToken(memberIndex);

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "wa3QkzrBALL4WACeB12Z");
//        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> kakaoLeloadRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> kakaoLeloadResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoLeloadRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthTokenDto oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(kakaoLeloadResponse.getBody(), OauthTokenDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return oauthToken;

    }



    // ㅁ [4-1번] DB에서 리프레시토큰 가져오기
//    public String findToken (Long memberIndex) {
//
//
//
//        Member member = memberRepository.findByMemberIndex(memberIndex);
//
//        MemberDto memberDto = member.toDto();
//
//        return memberDto.getRefreshToken();
//
//    }

    public void setJwtValue(String token){
        this.jwtValue = token;
    }

    public String getJwtValue(){
        return jwtValue;
    }

    // ㅁ [5번] OAuth 로그아웃
    public void logout(){
        RestTemplate rt = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", "wa3QkzrBALL4WACeB12Z");
        params.add("logout_redirect_uri", "http://localhost:3000/members/logout");

        HttpEntity<MultiValueMap<String, String>> logout =
                new HttpEntity<>(params);

        ResponseEntity<Void> logoutResponse = rt.exchange(
          "https://kauth.kakao.com/oauth/logout",
                HttpMethod.GET,
                logout,
                Void.class
        );

    }




//    public String convertImageToBase64(String imageUrl) throws Exception {
//        URL url = new URL(imageUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setDoInput(true);
//        connection.connect();
//
//        InputStream inputStream = connection.getInputStream();
//        byte[] imageBytes = inputStream.readAllBytes();
//        inputStream.close();
//
//        return Base64.getEncoder().encodeToString(imageBytes);
//    }

}




