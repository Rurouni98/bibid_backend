package bibid.oauth2;

import bibid.dto.ResponseDto;
import bibid.jwt.JwtProvider;
import bibid.service.member.MemberServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController //(1)
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final KakaoServiceImpl kakaoServiceImpl; //(2)
    private final NaverServiceImpl naverServiceImpl;
    private final GoogleServiceImpl googleServiceImpl;
    private final JwtProvider jwtProvider;
    private final MemberServiceImpl memberServiceImpl;
    private OauthTokenDto oauthToken;

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
        // 프론트에 넘겨 줄 회원정보 조회
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();
        Map<String, String> memberInfo = kakaoServiceImpl.getMember(jwtToken);

        //(3)
        try {
            log.info("login KakaoProfileDto: {}", jwtToken.toString());
            Cookie cookie = new Cookie("ACCESS_TOKEN", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Sent to Client");
            responseDto.setItem(memberInfo);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("login error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());

            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    // ㅁ 네이버
    @GetMapping("/naver/callback") // (3)
    public ResponseEntity<?> getNaverJwtToken(@RequestParam("code") String code, HttpServletResponse response) {

        // 넘어온 인가 코드를 통해 access_token 발급
        oauthToken = naverServiceImpl.getAccessToken(code);
        System.out.println("oauthToken" + oauthToken);

        //(2)
        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = naverServiceImpl.saveUserAndGetToken(oauthToken.getAccess_token());

        //(3)
        // 프론트에 넘겨 줄 회원정보 조회
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();
        Map<String, String> memberInfo = naverServiceImpl.getMember(jwtToken);

        try {
            log.info("login NaverProfileDto: {}", jwtToken.toString());
            Cookie cookie = new Cookie("ACCESS_TOKEN", jwtToken);
            cookie.setHttpOnly(true); // 클라이언트 측 JavaScript에서 쿠키 접근 방지
            cookie.setPath("/"); // 쿠키의 유효 경로 설정
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie); // 쿠키 추가

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Sent to Client");
            responseDto.setItem(memberInfo);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("login error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());

            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    // ㅁ 구글
    @PostMapping("/google/callback")
    public ResponseEntity<?> getGoogleJwtToken(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String accessToken = body.get("access_token");

        String jwtToken = googleServiceImpl.saveUserAndGetToken(accessToken);

        // 프론트에 넘겨 줄 회원정보 조회
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();
        Map<String, String> memberInfo = googleServiceImpl.getMember(jwtToken);

        try {
            log.info("login NaverProfileDto: {}", jwtToken.toString());
            Cookie cookie = new Cookie("ACCESS_TOKEN", jwtToken);
            cookie.setHttpOnly(true); // 클라이언트 측 JavaScript에서 쿠키 접근 방지
            cookie.setPath("/"); // 쿠키의 유효 경로 설정
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie); // 쿠키 추가

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Access token received successfully.");
            responseDto.setItem(memberInfo); // 사용자 정보를 응답 DTO에 추가

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/checkLogin")
    public ResponseEntity<?> checkLogin(HttpServletRequest request, Principal principal) {

        ResponseDto<Boolean> responseDto = new ResponseDto<>();

        Cookie[] cookies = request.getCookies();
        try {
            boolean hasAccessToken = false;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("ACCESS_TOKEN".equals(cookie.getName())) {
                        hasAccessToken = true;

                        if (principal != null) {
                            responseDto.setStatusMessage("ok");
                            responseDto.setStatusCode(200);
                            responseDto.setItem(hasAccessToken);
                        } else {
                            responseDto.setStatusMessage("not logged in");
                            responseDto.setStatusCode(401);
                            responseDto.setItem(hasAccessToken);
                        }
                        return ResponseEntity.ok(responseDto);
                    }
                }
            }

            if (!hasAccessToken) {
                responseDto.setStatusMessage("no cookie");
                responseDto.setStatusCode(401);
                responseDto.setItem(hasAccessToken);
                return ResponseEntity.ok(responseDto);
            }

        } catch (Exception e) {
            responseDto.setStatusMessage("error");
            responseDto.setStatusCode(500);
            return ResponseEntity.internalServerError().body(responseDto);
        }

        responseDto.setStatusMessage("unExpected Error");
        responseDto.setStatusCode(500);
        return ResponseEntity.internalServerError().body(responseDto);
    }
}