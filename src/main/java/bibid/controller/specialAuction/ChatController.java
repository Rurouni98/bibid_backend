package bibid.controller.specialAuction;

import bibid.dto.ChatDto;
import bibid.entity.*;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.repository.specialAuction.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final AuctionRepository auctionRepository;
    private final UserDetailsService userDetailsService;
    private final ChatRepository chatRepository;

    @MessageMapping("/chat.sendMessage/{auctionIndex}")
    @SendTo("/topic/public/{auctionIndex}")
    public ChatDto sendMessage(@DestinationVariable Long auctionIndex, @Payload ChatDto chatDto, Principal principal) {

        // principal이 사용자 이름(String)일 수 있음
        if (principal == null) {
            throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        // 사용자 이름을 principal에서 가져옴
        String username = principal.getName();

        // UserDetailsService를 사용하여 사용자 정보 로드
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        Member sender = userDetails.getMember();

        // 경매 정보 조회
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));

        chatDto.setSendTime(LocalDateTime.now()); // 현재 시간을 메시지 전송 시간으로 설정

        Chat chat = chatDto.toEntity(auction.getChatRoom(), sender);

        Chat savedChat = chatRepository.save(chat);

        return savedChat.toDto();  // 클라이언트로 메시지를 그대로 전송

    }

    @MessageMapping("/chat.enter/{auctionIndex}")
    @SendTo("/topic/public/{auctionIndex}")
    public ChatDto enter(ChatDto chatDto, SimpMessageHeaderAccessor headerAccessor) {
        chatDto.setChatMessage(chatDto.getSenderIndex() + " 님이 입장하셨습니다.");
        return chatDto;  // 입장 메시지 전송
    }

}
