package bibid.entity;

import bibid.dto.ChatDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "chatSeqGenerator",
        sequenceName = "CHAT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chatSeqGenerator"
    )
    private Long ChatIndex;

    @ManyToOne
    @JoinColumn(name = "chatRoomIndex") // 외래 키: 스트리밍 ID
    private ChatRoom chatRoom;

    private String chatMessage;

    @ManyToOne
    @JoinColumn(name = "memberIndex") // 외래 키: 스트리밍 ID
    private Member sender;
    private LocalDateTime sendTime;
    private ChatMessageType messageType;

    public ChatDto toDto(){
        return ChatDto.builder()
                .chatIndex(this.ChatIndex)
                .chatRoomIndex(this.chatRoom.getChatRoomIndex())
                .chatMessage(this.chatMessage)
                .senderIndex(this.sender.getMemberIndex())
                .sendTime(this.sendTime)
                .senderNickname(this.sender.getNickname())
                .messageType(this.messageType)
                .build();
    }
}
