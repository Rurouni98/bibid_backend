package bibid.controller.member;


import bibid.dto.MemberDto;
import bibid.dto.ResponseDto;
import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.jwt.JwtProvider;
import bibid.oauth2.KakaoServiceImpl;
import bibid.repository.member.MemberRepository;
import bibid.service.member.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final KakaoServiceImpl kakaoService;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Value("${cookie.secure}")
    private String cookieSecure;

    private Map<String, String> verificationCodes = new HashMap<>();

    @PostMapping("/memberId-check")
    public ResponseEntity<?> memberIdCheck(@RequestBody MemberDto memberDto) {
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();

        try {
            log.info("memberId: {}", memberDto.getMemberId());
            Map<String, String> map = memberService.memberIdCheck(memberDto.getMemberId());

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(map);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("memberId-check error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/nickname-check")
    public ResponseEntity<?> nicknameCheck(@RequestBody MemberDto memberDto) {
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();

        try {
            log.info("nickname: {}", memberDto.getNickname());
            Map<String, String> map = memberService.nicknameCheck(memberDto.getNickname());

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(map);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("nickname-check error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberDto memberDto) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            log.info("join memberDto: {}", memberDto.toString());
            MemberDto joinedMemberDto = memberService.join(memberDto);

            responseDto.setStatusCode(HttpStatus.CREATED.value());
            responseDto.setStatusMessage("created");
            responseDto.setItem(joinedMemberDto);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("join error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto, HttpServletResponse response) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            log.info("Received login request: {}", memberDto.toString());
            MemberDto loginMember = memberService.login(memberDto);
            log.info("Login data: {}", loginMember);

            String jwtToken = jwtProvider.createJwt(loginMember.toEntity());
            Boolean rememberMe = loginMember.getRememberMe();
            log.info("Remember Me flag: {}", rememberMe);

            // 쿠키 설정
            StringBuilder cookieHeader = new StringBuilder("ACCESS_TOKEN=" + jwtToken + "; Path=/; HttpOnly; ");

            if (rememberMe) {
                int maxAge = 7 * 24 * 60 * 60; // 7일
                log.info("Setting Max-Age for cookie: {}", maxAge);
                cookieHeader.append("Max-Age=").append(maxAge).append("; ");
            }

            cookieHeader.append("Secure; SameSite=None");

            log.info("Cookie header: {}", cookieHeader.toString());
            response.addHeader("Set-Cookie", cookieHeader.toString());

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(loginMember);

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {

        Member member = new Member();

        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();

        try {

            Map<String, String> logoutMsgMap = new HashMap<>();

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(null);
            SecurityContextHolder.setContext(securityContext);

            logoutMsgMap.put("logoutMsg", "logout success");

            responseDto.setStatusCode(200);
            responseDto.setStatusMessage("ok");
            responseDto.setItem(logoutMsgMap);

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("ACCESS_TOKEN".equals(cookie.getName())) {
                        response.addHeader("Set-Cookie",
                                "ACCESS_TOKEN=null; Max-Age=0; Path=/; Secure; HttpOnly; SameSite=None");
                    }
                }
            }

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("logout error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/fetchMember")
    public ResponseEntity<?> fetchMember(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        if (customUserDetails == null) {
            responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            responseDto.setStatusMessage("로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }

        try {
            Member member = memberRepository.findById(customUserDetails.getMember().getMemberIndex())
                    .orElseThrow(() -> new RuntimeException("member not exist"));

            responseDto.setItem(member.toDto());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("멤버 정보 조회 성공");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("멤버 정보를 가져오는 데 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }



}
