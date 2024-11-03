package bibid.oauth2;

import bibid.dto.ResponseDto;
import bibid.entity.Account;
import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.entity.SellerInfo;
import bibid.jwt.JwtProvider;
import bibid.repository.member.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor

public class NaverServiceImpl {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Value("${front.url}")
    private String frontUrl;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    // ㅁ [1번] 코드로 카카오에서 토큰 받기
    public OauthTokenDto getAccessToken(String code) {



        //(1) RestfulAPI 준비
        RestTemplate rt = new RestTemplate();

        //(2) 어떤 형식으로 보내줄 건지
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(3) 어떤 내용을 보내줄 건지
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("redirect_uri", frontUrl + ":3000/auth/naver/callback");
        params.add("code", code);

        //(4) 어디에 담아줄 건지
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(params, headers);

        //(5) 무엇을 주고 받을건지
        ResponseEntity<String> accessTokenResponse = null;
        try {
            accessTokenResponse = rt.exchange(
                    "https://nid.naver.com/oauth2.0/token",
                    HttpMethod.POST,
                    naverTokenRequest,
                    String.class
            );
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }

        //(6) Json 방식을 java로 받을 때 어떻게 받을건지
        ObjectMapper objectMapper = new ObjectMapper();
        OauthTokenDto oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthTokenDto.class);
            findProfile(oauthToken.getAccess_token());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oauthToken; //(8)
    }

    //     ㅁ [2번] 카카오에서 받은 액세스 토큰으로 카카오에서 사용자 정보 받아오기
    public NaverProfileDto findProfile(String naverAccessToken) {

        //(1-2)
        RestTemplate rt = new RestTemplate();

        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + naverAccessToken); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(1-5)
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest =
                new HttpEntity<>(headers);

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> naverProfileResponse = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );

        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        NaverProfileDto naverProfile = null;
        try {
            naverProfile = objectMapper.readValue(naverProfileResponse.getBody(), NaverProfileDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return naverProfile;
    }

    // ㅁ [3번] 카카오에서 받은 액세스 토큰으로 정보저장 + Jwt 토큰 얻어서 토큰생성
    public String saveUserAndGetToken(String naverAccessToken) {

        Member naverMember = null;

        NaverProfileDto profile = findProfile(naverAccessToken);

        naverMember = memberRepository.findByEmail(profile.getResponse().getEmail());

        if (naverMember == null) {
            naverMember = Member.builder()
                    .memberId(profile.getResponse().getNickname())
                    .nickname(profile.getResponse().getNickname())
                    .email(profile.getResponse().getEmail())
                    .memberPnum(profile.getResponse().getMobile())
                    .name(profile.getResponse().getName())
                    .role("ROLE_USER")
                    .oauthType("Naver")
                    .build();

            // SellerInfo와 Account를 생성하여 Member에 설정
            SellerInfo sellerInfo = SellerInfo.builder()
                    .member(naverMember)
                    .build();
            naverMember.setSellerInfo(sellerInfo);

            Account account = Account.builder()
                    .member(naverMember)
                    .userMoney("1000000")
                    .build();
            naverMember.setAccount(account);
            
            memberRepository.save(naverMember);
        }

        return jwtProvider.createOauthJwt(naverMember); //(2)
    }

    // ㅁ [4-1번] DB
    public Map<String, String> getMember(String jwtTokenValue) {

        Map<String, String> item = new HashMap<>();
        Member member = null;

        try {
            String memberId = jwtProvider.validateAndGetSubject(jwtTokenValue);

            member = memberRepository.findByNickname(memberId);

            item.put("memberIndex", String.valueOf(member.getMemberIndex()));
            item.put("type", member.getOauthType());
            item.put("addressDetail", "***");
            item.put("email", member.getEmail());
            item.put("memberAddress", "***");
            item.put("memberId", member.getMemberId());
            item.put("nickname", member.getNickname());
            item.put("name", member.getName());
            item.put("memberPnum", member.getMemberPnum());

            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ㅁ [4-1번] DB에서 리프레시토큰 가져오기
    public ResponseEntity<?> getTokenAndType(String jwtTokenValue, Principal principal) {

        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();
        Map<String, String> item = new HashMap<>();
        Member member = null;
        try {
            if (principal == null) {
                throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
            }

            String username = principal.getName();

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            Member memberId = userDetails.getMember();

            String findMemberNickname = memberId.getNickname();

            member = memberRepository.findByNickname(findMemberNickname);

            String token = member.getRefreshToken();
            String type = member.getOauthType();

            item.put("token", token);
            item.put("type", type);

            responseDto.setItem(item);

            return ResponseEntity.ok(responseDto);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    // ㅁ 프로필 이미지 base 64 변환 (수정중)
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
//


}




