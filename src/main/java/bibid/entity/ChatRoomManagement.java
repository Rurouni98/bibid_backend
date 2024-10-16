package bibid.entity;

import bibid.dto.ChatRoomDto;
import bibid.dto.ChatRoomManagementDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@SequenceGenerator(
        name = "chatRoomManagementSeqGenerator",
        sequenceName = "CHATROOM_MANAGEMENT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomManagement {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chatRoomManagementSeqGenerator"
    )
    private Long chatRoomManagementIndex;

    @ManyToOne
    @JoinColumn(name = "chatRoomIndex") // 외래 키: 스트리밍 ID
    private ChatRoom chatRoom;

    private Long participantIndex;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;

    public ChatRoomManagementDto toDto() {
        return ChatRoomManagementDto.builder()
                .chatRoomManagementIndex(this.chatRoomManagementIndex)
                .chatRoomIndex(Optional.ofNullable(this.chatRoom).map(ChatRoom::getChatRoomIndex).orElse(null))
                .participantIndex(this.participantIndex)
                .joinTime(this.joinTime)
                .leaveTime(this.leaveTime)
                .build();
    }

}
