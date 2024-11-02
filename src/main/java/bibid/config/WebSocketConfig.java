package bibid.config;

import bibid.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;



@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${front.url}")
    private String frontUrl;

    private final JwtProvider jwtProvider;

    // JwtProvider를 생성자 주입받도록 추가
    public WebSocketConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-auctions")
//                .setAllowedOrigins(frontUrl + ":3000")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler(jwtProvider))
                .withSockJS();

        registry.addEndpoint("/ws-notifications")
//                .setAllowedOrigins(frontUrl + ":3000")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler(jwtProvider))
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtProvider));
    }
}