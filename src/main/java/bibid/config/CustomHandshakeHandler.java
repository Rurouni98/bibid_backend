package bibid.config;

import bibid.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtProvider jwtProvider;

    // 생성자 주입을 통해 JwtProvider를 받아옵니다.
    public CustomHandshakeHandler(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

//    @Override
//    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        // SecurityContext에서 인증 정보를 가져와 Principal로 설정
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            return null;
//        }
//        return authentication;
//    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        Cookie jwtCookie = WebUtils.getCookie(servletRequest, "ACCESS_TOKEN");

        if (jwtCookie != null) {
            String token = jwtCookie.getValue();
            log.info("CustomHandshakeHandler - ACCESS_TOKEN found in cookie: {}", token);

            String username = jwtProvider.validateAndGetSubject(token);
            log.info("CustomHandshakeHandler - Extracted username from JWT: {}", username);

            attributes.put("ACCESS_TOKEN", token);
            log.info("CustomHandshakeHandler - ACCESS_TOKEN added to session attributes");

            return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        } else {
            log.warn("CustomHandshakeHandler - No ACCESS_TOKEN found in cookies");
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}