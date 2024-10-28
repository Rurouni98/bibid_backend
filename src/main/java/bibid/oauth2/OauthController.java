package bibid.oauth2;

import bibid.dto.ResponseDto;

import bibid.jwt.JwtProvider;
import bibid.service.member.MemberServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import org.springframework.web.client.RestTemplate;

@RestController //(1)
@RequestMapping("/auth")
@Slf4j
public class OauthController {

    @Autowired
    private KakaoServiceImpl kakaoServiceImpl; //(2)

    @Autowired
    private NaverServiceImpl naverServiceImpl;
    private OauthTokenDto oauthToken;
    private JwtProvider jwtProvider;
    @Autowired
    private MemberServiceImpl memberServiceImpl;

    // ㅁ 카카오
    // 프론트에서 인가코드 받아오는 url
    @GetMapping("/kakao/callback") // (3)
    public ResponseEntity<?> getKakaoJwtToken(@RequestParam("code") String code, HttpServletResponse response) { //(4)

        // 넘어온 인가 코드를 통해 access_token 발급 //(5)
        oauthToken = kakaoServiceImpl.getAccessToken(code);

        //(2)
        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = kakaoServiceImpl.saveUserAndGetToken(oauthToken.getAccess_token());

        //(3)
        ResponseDto responseDto = new ResponseDto();

        try {
            log.info ("login KakaoProfileDto: {}", jwtToken.toString());
            Cookie cookie = new Cookie("ACCESS_TOKEN", jwtToken);
            cookie.setHttpOnly(true); // 클라이언트 측 JavaScript에서 쿠키 접근 방지
            cookie.setPath("/"); // 쿠키의 유효 경로 설정
            cookie.setMaxAge(3600); // 쿠키의 만료 시간 설정 (1시간)
            response.addCookie(cookie); // 쿠키 추가

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Sent to Client");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("login error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());

            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

//    @PostMapping("/kakao/reloadToken")
//    public ResponseEntity<?> kakaoLeloadToken(){
//
//
//
//
//    }

    // ㅁ 네이버
    @GetMapping("/naver/callback") // (3)
    public ResponseEntity<?> getNaverJwtToken(@RequestParam("code") String code) { //(4)

        // 넘어온 인가 코드를 통해 access_token 발급 //(5)
        oauthToken = naverServiceImpl.getAccessToken(code);

        //(2)
        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = naverServiceImpl.saveUserAndGetToken(oauthToken.getAccess_token());

        //(3)
        ResponseDto responseDto = new ResponseDto();

        try {
            log.info ("login NaverProfileDto: {}", jwtToken.toString());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(jwtToken);


            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("login error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());

            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/api/token")
    public String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    kakaoServiceImpl.setJwtValue(token);
                    return token;
                }
            }
        }
        System.out.println("토큰값을 가져올 수 없습니다..");

        return null;
    }
}
