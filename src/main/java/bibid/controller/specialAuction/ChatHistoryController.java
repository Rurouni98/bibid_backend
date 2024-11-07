package bibid.controller.specialAuction;

import bibid.dto.ChatDto;
import bibid.service.specialAuction.RedisChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryController {

    private final RedisChatService redisChatService;

    @GetMapping("/lastMinute/{auctionIndex}")
    public List<ChatDto> getLastMinuteChatMessages(@PathVariable Long auctionIndex) {
        log.info("getLastMinuteChatMessages 호출 - auctionIndex: {}", auctionIndex);

        // Redis에서 1분 내 채팅 메시지 리스트 가져오기
        List<ChatDto> lastMinuteMessages = redisChatService.getLastMinuteMessages(auctionIndex);

        if (lastMinuteMessages.isEmpty()) {
            log.info("최근 1분 내 메시지가 없음. auctionIndex: {}", auctionIndex);
        } else {
            log.info("최근 1분 내 메시지 개수: {}", lastMinuteMessages.size());
            log.info("최근 1분 내 메시지 리스트: {}", lastMinuteMessages);
        }

        return lastMinuteMessages;
    }
}
