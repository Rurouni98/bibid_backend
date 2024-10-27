package bibid.config;

import bibid.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;

@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider; // JWT 검증 클래스

    public JwtChannelInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        // CONNECT 메시지에서 JWT 토큰 확인
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String authHeader = accessor.getFirstNativeHeader("Authorization");
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                try {
//                    // JWT 토큰을 검증하고 유효한 경우 사용자 정보를 설정
//                    String username = jwtProvider.validateAndGetSubject(token);
//                    accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
//                } catch (Exception e) {
//                    throw new MessagingException("Invalid JWT Token");
//                }
//            } else {
//                throw new MessagingException("Missing or invalid Authorization header");
//            }
//        }
//        return message;
//    }

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        // CONNECT 명령에서 JWT 쿠키 확인
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            HttpServletRequest servletRequest = (HttpServletRequest) accessor.getHeader("simpHttpServletRequest");
//            Cookie jwtCookie = WebUtils.getCookie(servletRequest, "ACCESS_TOKEN");
//
//            if (jwtCookie != null) {
//                String token = jwtCookie.getValue();
//                if (StringUtils.hasText(token)) {
//                    try {
//                        String username = jwtProvider.validateAndGetSubject(token);
//                        accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
//                    } catch (Exception e) {
//                        throw new MessagingException("Invalid JWT Token", e);
//                    }
//                }
//            } else {
//                throw new MessagingException("Missing JWT Token in cookies");
//            }
//        }
//        return message;
//    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = (String) accessor.getSessionAttributes().get("ACCESS_TOKEN");

            if (token != null && !token.isEmpty()) {
                log.info("JwtChannelInterceptor - Found ACCESS_TOKEN in session attributes: {}", token);

                try {
                    String username = jwtProvider.validateAndGetSubject(token);
                    log.info("JwtChannelInterceptor - Extracted username from JWT: {}", username);

                    accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
                    log.info("JwtChannelInterceptor - User set in WebSocket session: {}", username);
                } catch (Exception e) {
                    log.error("JwtChannelInterceptor - Invalid JWT Token", e);
                    throw new MessagingException("Invalid JWT Token", e);
                }
            } else {
                log.warn("JwtChannelInterceptor - Missing JWT Token in WebSocket session attributes");
                throw new MessagingException("Missing JWT Token in WebSocket session attributes");
            }
        }
        return message;
    }
}
