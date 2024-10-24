package bibid.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController //(1)
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService; //(2)
    private OauthToken oauthToken;

    // 프론트에서 인가코드 받아오는 url
    @GetMapping("/kakao/callback") // (3)
    public User getToken(@RequestParam("code") String code) { //(4)

        // 넘어온 인가 코드를 통해 access_token 발급 //(5)
        oauthToken = userService.getAccessToken(code);

        //(2)
        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = userService.saveUserAndGetToken(oauthToken.getAccess_token());

        //(3)
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        //(4)
        return ResponseEntity.ok().headers(headers).body("success");
    }





}
