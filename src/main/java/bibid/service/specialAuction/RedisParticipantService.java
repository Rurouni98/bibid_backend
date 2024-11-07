package bibid.service.specialAuction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisParticipantService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PARTICIPANTS_KEY_PREFIX = "auction:participants:";
    private final SimpMessagingTemplate messagingTemplate;

    public void sendParticipantCountUpdate(Long auctionIndex) {
        Long participantCount = getParticipantCount(auctionIndex); // 현재 참가자 수 조회
        messagingTemplate.convertAndSend("/topic/participants/count/" + auctionIndex, participantCount);
    }

    // 입장 처리
    public void enterAuction(Long auctionIndex, String userId) {
        String key = PARTICIPANTS_KEY_PREFIX + auctionIndex;
        redisTemplate.opsForSet().add(key, userId);
        log.info("User {} entered auction {} in Redis", userId, auctionIndex);

        // 참가자 수 WebSocket 업데이트 전송
        sendParticipantCountUpdate(auctionIndex);
    }

    // 퇴장 처리
    public void leaveAuction(Long auctionIndex, String userId) {
        String key = PARTICIPANTS_KEY_PREFIX + auctionIndex;
        redisTemplate.opsForSet().remove(key, userId);
        log.info("User {} left auction {} in Redis", userId, auctionIndex);

        // 참가자 수 WebSocket 업데이트 전송
        sendParticipantCountUpdate(auctionIndex);
    }

    // 참가자 수 조회
    public Long getParticipantCount(Long auctionIndex) {
        String key = PARTICIPANTS_KEY_PREFIX + auctionIndex;
        return redisTemplate.opsForSet().size(key);
    }

    // 참가자 목록 조회
    public Set<String> getParticipants(Long auctionIndex) {
        String key = PARTICIPANTS_KEY_PREFIX + auctionIndex;
        return redisTemplate.opsForSet().members(key);
    }
}
