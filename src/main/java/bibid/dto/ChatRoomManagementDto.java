package bibid.dto;

import bibid.entity.ChatRoom;
import bibid.entity.ChatRoomManagement;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomManagementDto {
    private Long chatRoomManagementIndex;

    private Long chatRoomIndex;

    private Long participantIndex;
    private String participantNickname;
    private LocalDateTime eventTime;
    private String type;  // LEAVE, ENTER

    public ChatRoomManagement toEntity(ChatRoom chatRoom) {
        return ChatRoomManagement.builder()
                .chatRoomManagementIndex(this.chatRoomManagementIndex)
                .chatRoom(chatRoom)
                .participantIndex(this.participantIndex)
                .participantNickname(this.participantNickname)
                .eventTime(this.eventTime)
                .type(this.type)
                .build();
    }
}
