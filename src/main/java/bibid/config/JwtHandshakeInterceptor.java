package bibid.config;

import bibid.jwt.JwtProvider;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider; // JWT 검증 클래스

    public JwtHandshakeInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        String authHeader = request.getHeaders().getFirst("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);  // 'Bearer ' 제거 후 토큰 추출
//            String username = jwtProvider.validateAndGetSubject(token);  // 토큰 검증 및 subject 추출
//
//            if (username != null) {
//                attributes.put("username", username);  // 검증된 username을 WebSocket 세션에 저장
//                return true;  // 유효한 토큰이면 WebSocket 연결 허용
//            }
//        }
//        return false; // 토큰이 없거나 유효하지 않으면 연결 차단
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 이후 로직 (필요 시)
    }

}
