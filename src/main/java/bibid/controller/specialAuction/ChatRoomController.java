package bibid.controller.specialAuction;

import bibid.entity.ChatRoom;
import bibid.entity.ChatRoomManagement;
import bibid.repository.specialAuction.ChatRoomManagementRepository;
import bibid.repository.specialAuction.ChatRoomRepository;
import bibid.service.specialAuction.RedisParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final RedisParticipantService redisParticipantService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomManagementRepository chatRoomManagementRepository;

    // 참가자 입장 메시지 처리
    @MessageMapping("/chatroom/{auctionIndex}/enter")
    @SendTo("/topic/participants/enter/{auctionIndex}")
    public String enterAuction(@DestinationVariable Long auctionIndex, @Payload String userId) {
        log.info("User {} entering auction {}", userId, auctionIndex);

        // Redis에 참가자 추가
        redisParticipantService.enterAuction(auctionIndex, userId);

        // DB에 입장 기록 추가
        ChatRoom chatRoom = chatRoomRepository.findByAuction_AuctionIndex(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));
        ChatRoomManagement chatRoomManagement = ChatRoomManagement.builder()
                .chatRoom(chatRoom)
                .participantIndex(Long.valueOf(userId))
                .joinTime(LocalDateTime.now())
                .build();
        chatRoomManagementRepository.save(chatRoomManagement);

        // 참가자 수 업데이트 및 입장 메시지 브로드캐스트
        redisParticipantService.sendParticipantCountUpdate(auctionIndex);
        return userId + "님이 입장하셨습니다.";
    }

    // 참가자 퇴장 메시지 처리
    @MessageMapping("/chatroom/{auctionIndex}/leave")
    @SendTo("/topic/participants/leave/{auctionIndex}")
    public String leaveAuction(@DestinationVariable Long auctionIndex, @Payload String userId) {
        log.info("User {} leaving auction {}", userId, auctionIndex);

        // Redis에서 참가자 제거
        redisParticipantService.leaveAuction(auctionIndex, userId);

        // DB에서 해당 참가자의 퇴장 시간 업데이트
        ChatRoom chatRoom = chatRoomRepository.findByAuction_AuctionIndex(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));
        ChatRoomManagement chatRoomManagement = chatRoomManagementRepository.findByChatRoomAndParticipantIndex(chatRoom, Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("Participant not found in chat room"));
        chatRoomManagement.setLeaveTime(LocalDateTime.now());
        chatRoomManagementRepository.save(chatRoomManagement);

        // 참가자 수 업데이트 및 퇴장 메시지 브로드캐스트
        redisParticipantService.sendParticipantCountUpdate(auctionIndex);
        return userId + "님이 나가셨습니다.";
    }
}

