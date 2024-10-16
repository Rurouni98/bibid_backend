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
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;

    public ChatRoomManagement toEntity(ChatRoom chatRoom) {
        return ChatRoomManagement.builder()
                .chatRoomManagementIndex(this.chatRoomManagementIndex)
                .chatRoom(chatRoom)
                .participantIndex(this.participantIndex)
                .joinTime(this.joinTime)
                .leaveTime(this.leaveTime)
                .build();
    }
}
