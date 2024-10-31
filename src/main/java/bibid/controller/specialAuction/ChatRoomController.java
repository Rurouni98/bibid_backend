package bibid.controller.specialAuction;

import bibid.entity.ChatRoom;
import bibid.entity.ChatRoomManagement;
import bibid.entity.Member;
import bibid.repository.member.MemberRepository;
import bibid.repository.specialAuction.ChatRoomManagementRepository;
import bibid.repository.specialAuction.ChatRoomRepository;
import bibid.service.specialAuction.RedisParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final RedisParticipantService redisParticipantService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomManagementRepository chatRoomManagementRepository;
    private final MemberRepository memberRepository;

    // 참가자 입장 메시지 처리
    @MessageMapping("/chatroom/{auctionIndex}/enter")
    public void enterAuction(@DestinationVariable Long auctionIndex, @Payload String nickname) {
        log.info("User {} attempting to enter auction {}", nickname, auctionIndex);

        // Redis에 참가자 추가
        redisParticipantService.enterAuction(auctionIndex, nickname);
        log.info("Added {} to Redis for auction {}", nickname, auctionIndex);

        // DB에 입장 기록 추가
        try {
            ChatRoom chatRoom = chatRoomRepository.findByAuction_AuctionIndex(auctionIndex)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));
            log.info("Found chat room for auctionIndex {}: {}", auctionIndex, chatRoom);

            log.info("Attempting to find member with nickname: {}", nickname);
            Member participant = memberRepository.findByNickname(nickname);

            if (participant == null) {
                log.warn("Member with nickname {} was not found in the database.", nickname);
                throw new IllegalArgumentException("Participant not found");
            }

            log.info("Found participant with nickname {}: {}", nickname, participant);

            ChatRoomManagement chatRoomManagement = ChatRoomManagement.builder()
                    .chatRoom(chatRoom)
                    .participantNickname(nickname)
                    .participantIndex(participant.getMemberIndex())
                    .eventTime(LocalDateTime.now())
                    .type("ENTER")
                    .build();

            chatRoomManagementRepository.save(chatRoomManagement);
            log.info("Saved ChatRoomManagement entry for user {} in auction {}: {}", nickname, auctionIndex, chatRoomManagement);
        } catch (Exception e) {
            log.error("Error while processing entry for auction {}: {}", auctionIndex, e.getMessage());
        }

        // 참가자 수 업데이트 및 입장 메시지 브로드캐스트
        redisParticipantService.sendParticipantCountUpdate(auctionIndex);
        log.info("Broadcasted participant count update for auction {}", auctionIndex);
    }

    // 참가자 퇴장 메시지 처리
    @MessageMapping("/chatroom/{auctionIndex}/leave")
    public void leaveAuction(@DestinationVariable Long auctionIndex, @Payload String nickname) {
        log.info("User {} attempting to leave auction {}", nickname, auctionIndex);

        // Redis에서 참가자 제거
        redisParticipantService.leaveAuction(auctionIndex, nickname);
        log.info("Removed {} from Redis for auction {}", nickname, auctionIndex);

        // DB에 퇴장 기록 추가
        try {
            ChatRoom chatRoom = chatRoomRepository.findByAuction_AuctionIndex(auctionIndex)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));
            log.info("Found chat room for auctionIndex {}: {}", auctionIndex, chatRoom);

            Member participant = memberRepository.findByNickname(nickname);

            ChatRoomManagement chatRoomManagement = ChatRoomManagement.builder()
                    .chatRoom(chatRoom)
                    .participantNickname(nickname)
                    .participantIndex(participant.getMemberIndex())
                    .eventTime(LocalDateTime.now())
                    .type("LEAVE")
                    .build();

            chatRoomManagementRepository.save(chatRoomManagement);
            log.info("Saved ChatRoomManagement entry for user {} in auction {}: {}", nickname, auctionIndex, chatRoomManagement);
        } catch (Exception e) {
            log.error("Error while processing entry for auction {}: {}", auctionIndex, e.getMessage());
        }

        // 참가자 수 업데이트 및 퇴장 메시지 브로드캐스트
        redisParticipantService.sendParticipantCountUpdate(auctionIndex);
        log.info("Broadcasted participant count update for auction {}", auctionIndex);
    }
}
