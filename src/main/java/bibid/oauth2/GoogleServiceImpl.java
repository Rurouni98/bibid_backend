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
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor

public class GoogleServiceImpl {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    // ㅁ [1번] 코드로 카카오에서 토큰 받기
    public String saveUserAndGetToken(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // Bearer 토큰 형식으로 설정

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleProfileDto googleProfileDto;
        try {
            googleProfileDto = objectMapper.readValue(response.getBody(), GoogleProfileDto.class);

            Member googleMember = saveOrUpdateMember(googleProfileDto);

            String jwtToken = jwtProvider.createOauthJwt(googleMember);

            System.out.println("jwtToken service:" + jwtToken);
            return jwtToken;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Member saveOrUpdateMember(GoogleProfileDto googleProfileDto) {
        Member googleMember = null;

        googleMember = memberRepository.findByEmail(googleProfileDto.getEmail());

        if (googleMember == null) {
            googleMember = Member.builder()
                    .memberId(googleProfileDto.getName())
                    .email(googleProfileDto.getEmail())
                    .name(googleProfileDto.getFamily_name())
                    .nickname(googleProfileDto.getName())
                    .role("ROLE_USER")
                    .oauthType("Google")
                    .build();

            // SellerInfo와 Account를 생성하여 Member에 설정
            SellerInfo sellerInfo = SellerInfo.builder()
                    .member(googleMember)
                    .build();
            googleMember.setSellerInfo(sellerInfo);

            Account account = Account.builder()
                    .member(googleMember)
                    .userMoney("1000000")
                    .build();
            googleMember.setAccount(account);

            memberRepository.save(googleMember);
        }

        return googleMember;
    }

    public Map<String, String> getMember(String jwtTokenValue) {

        Map<String, String> item = new HashMap<>();

        try {
            // JWT에서 memberId를 추출
            String memberId = jwtProvider.validateAndGetSubject(jwtTokenValue);

            // Member 조회
            Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

            // Optional<Member>에서 Member 객체 가져오기
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get(); // Member 객체

                // 회원 정보를 item에 추가
                item.put("memberIndex", String.valueOf(member.getMemberIndex())); // Assuming getId() returns the member's index
                item.put("type", member.getOauthType());
                item.put("addressDetail", "***"); // 실제 주소 세부정보로 변경 필요
                item.put("email", member.getEmail());
                item.put("memberAddress", "***"); // 실제 주소로 변경 필요
                item.put("memberId", member.getMemberId());
                item.put("nickname", member.getNickname());
                item.put("name", member.getName());
                item.put("memberPnum", "***");

                return item;
            } else {
                throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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







