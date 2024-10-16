package bibid.config;

import bibid.jwt.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;

public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider; // JWT 검증 클래스

    public JwtChannelInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // CONNECT 메시지에서 JWT 토큰 확인
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    // JWT 토큰을 검증하고 유효한 경우 사용자 정보를 설정
                    String username = jwtProvider.validateAndGetSubject(token);
                    accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
                } catch (Exception e) {
                    throw new MessagingException("Invalid JWT Token");
                }
            } else {
                throw new MessagingException("Missing or invalid Authorization header");
            }
        }
        return message;
    }
}
