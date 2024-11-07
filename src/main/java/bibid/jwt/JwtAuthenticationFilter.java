package bibid.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Spring Security에 Filter로 등록해서 요청이 올 때마다 자동으로
// doFilterInternal 메소드가 동작
// JwtProvider의 validateAndGetSubject 메소드를 호출해서
// JWT의 유효성 검사를 진행하고 유효하면 username을 리턴받아
// username에 해당하는 Member를 Spring Security Context에 등록
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

//    private String parseBearerToken(HttpServletRequest request) {
//        /*
//        * request 헤더에 담겨있는 토큰의 형태
//        * header: {
//        *   Authorization: "Bearer 토큰"
//        * }
//        * */
//        String bearerToken = request.getHeader("Authorization");
//
//        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
//            return bearerToken.substring(7);
//        }
//
//        return null;
//    }
//

    private String parseBearerToken(HttpServletRequest request) {
//        // 1. Authorization 헤더에서 Bearer 토큰 추출
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }

        // 2. HttpOnly 쿠키에서 토큰 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) { // 토큰이 저장된 쿠키 이름이 "ACCESS_TOKEN"이라 가정
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 토큰 값이 있으면 토큰 값이 담기고 없으면 null이 담긴다.
            String token = parseBearerToken(request);

            // 토큰의 유효성 검사 및 Security Context에 Member 등록
            if (token != null && !token.equalsIgnoreCase("null")) {
                // JwtProvider의 validateAndGetSubject 메소드로 토큰의 유효성 검사 및
                // username 가져오기
                String username = jwtProvider.validateAndGetSubject(token);
                log.info("Username from token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("Loaded user details: {}", userDetails);

                // Security Context에 등록될 Authentication Token 객체 생성
                AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Authentication Token을 Security Context에 등록
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
        } catch (Exception e) {
            log.error("set security context error: {}", e.getMessage());
        }
        //필터 체인에 등록
        filterChain.doFilter(request, response);
    }
}
