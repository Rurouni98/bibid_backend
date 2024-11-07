package bibid.service.specialAuction;

import bibid.dto.ChatDto;
import bibid.entity.ChatMessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // 메시지 저장 메서드 (채팅방 메시지 Redis에 저장)
    public void saveChatMessage(Long auctionIndex, ChatDto chatDto) {
        if (chatDto.getMessageType() != ChatMessageType.MESSAGE) {
            log.info("메시지 타입이 일반 메시지가 아님: {}", chatDto.getMessageType());
            return;
        }

        String redisKey = "chat:auctionRoom:" + auctionIndex;
        String message;

        try {
            message = objectMapper.writeValueAsString(chatDto);
            log.info("메시지를 JSON으로 변환 성공: {}", message);
        } catch (Exception e) {
            log.error("메시지 변환 오류", e);
            return;
        }

        // Redis List에 메시지 추가 및 TTL 설정
        redisTemplate.opsForList().rightPush(redisKey, message);
        redisTemplate.expire(redisKey, Duration.ofMinutes(1));
        log.info("Redis에 저장된 메시지: {} - TTL 1분 설정 완료", message);
    }

    // 1분 내 메시지 가져오기 메서드
    public List<ChatDto> getLastMinuteMessages(Long auctionIndex) {
        String redisKey = "chat:auctionRoom:" + auctionIndex;
        log.info("1분 내 메시지 가져오기 - Redis 키: {}", redisKey);

        // Redis List에서 모든 채팅 메시지 가져오기
        List<String> lastMinuteMessages = redisTemplate.opsForList().range(redisKey, 0, -1);

        if (lastMinuteMessages == null || lastMinuteMessages.isEmpty()) {
            log.info("Redis에서 가져온 메시지 없음 또는 빈 리스트");
            return Collections.emptyList();  // 데이터가 없을 경우 빈 리스트 반환
        }

        log.info("Redis에서 가져온 메시지 목록: {}", lastMinuteMessages);

        // JSON 문자열을 ChatDto 객체로 변환
        return lastMinuteMessages.stream()
                .map(message -> {
                    try {
                        ChatDto chatDto = objectMapper.readValue(message, ChatDto.class);
                        log.info("JSON 문자열을 ChatDto로 변환 성공: {}", chatDto);
                        return chatDto;
                    } catch (Exception e) {
                        log.error("메시지 변환 오류", e);
                        return null;
                    }
                })
                .filter(chatDto -> chatDto != null) // null 값 필터링
                .collect(Collectors.toList());
    }
}
