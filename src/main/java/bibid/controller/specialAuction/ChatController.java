package bibid.controller.specialAuction;

import bibid.dto.ChatDto;
import bibid.entity.*;
import bibid.entity.CustomUserDetails;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.specialAuction.ChatRepository;
import bibid.service.specialAuction.RedisChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final AuctionRepository auctionRepository;
    private final UserDetailsService userDetailsService;
    private final ChatRepository chatRepository;
    private final RedisChatService redisChatService;

    // 참여자 수를 관리하는 Map
    private final Map<Long, Set<String>> participants = new HashMap<>();

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

        chatDto.setSendTime(LocalDateTime.now());
        Chat chat = chatDto.toEntity(auction.getChatRoom(), sender);
        Chat savedChat = chatRepository.save(chat);

        log.info("savedChat : {}", savedChat);

        // Redis에 메시지 저장
        redisChatService.saveChatMessage(auctionIndex, savedChat.toDto());
        log.info("saveChatMessage 호출됨: auctionIndex={}, message={}", auctionIndex, chatDto.getChatMessage());


        return savedChat.toDto();

    }

    @MessageMapping("/chat.enter/{auctionIndex}")
    @SendTo("/topic/participants/enter/{auctionIndex}")
    public ChatDto enter(@DestinationVariable Long auctionIndex, @Payload ChatDto chatDto, Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        // 사용자 이름을 principal에서 가져옴
        String username = principal.getName();
        
        // participants 맵에 사용자를 추가 (중복 체크 없이)

        participants.computeIfAbsent(auctionIndex, k -> new HashSet<>()).add(username);
        log.info("enter_participants: {}", participants);
        int participantCount = participants.get(auctionIndex).size();

        // 입장 메시지 설정
        chatDto.setChatMessage(username + " 님이 입장하셨습니다.");
        chatDto.setParticipantCount(participantCount); // 참가자 수 설정
        chatDto.setSendTime(LocalDateTime.now()); // 현재 시간을 메시지 전송 시간으로 설정

        log.info("enter - 경매 ID: {}, 참여자 수: {}", auctionIndex, participantCount);

        return chatDto;  // 참가자 수와 입장 메시지를 클라이언트로 전송
    }

    @MessageMapping("/chat.leave/{auctionIndex}")
    @SendTo("/topic/participants/leave/{auctionIndex}")
    public ChatDto leave(@DestinationVariable Long auctionIndex, @Payload ChatDto chatDto, Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        // 사용자 이름을 principal에서 가져옴
        String username = principal.getName();

        log.info(participants.toString());

        // participants 맵에서 사용자 제거
        if (participants.containsKey(auctionIndex)) {
            participants.get(auctionIndex).remove(username);
            log.info("leave_participants: {}", participants);
            // 참가자 수가 0명이면 해당 경매의 참가자 맵에서 삭제
            if (participants.get(auctionIndex).isEmpty()) {
                participants.remove(auctionIndex);
            }
        }

        int participantCount = participants.get(auctionIndex) != null ? participants.get(auctionIndex).size() : 0;

        log.info("leave - 경매 ID: {}, 참여자 수: {}", auctionIndex, participantCount);

        // 나가기 메시지 설정
        chatDto.setChatMessage(username + " 님이 나가셨습니다.");
        chatDto.setParticipantCount(participantCount); // 참가자 수 설정
        chatDto.setSendTime(LocalDateTime.now()); // 현재 시간을 메시지 전송 시간으로 설정

        log.info("leave - 경매 ID: {}, 참여자 수: {}", auctionIndex, participantCount);

        return chatDto;  // 나가기 메시지를 클라이언트로 전송
    }
}
