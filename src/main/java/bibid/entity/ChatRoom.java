package bibid.entity;

import bibid.dto.ChatRoomDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@SequenceGenerator(
        name = "chatroomSeqGenerator",
        sequenceName = "CHATROOM_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chatroomSeqGenerator")
    private Long chatRoomIndex;

    // 경매 ID와 채팅방을 연결
    @OneToOne
    @JoinColumn(name = "auctionIndex")
    private Auction auction;

    private LocalDateTime createdAt;

    // 채팅방의 이름이나 정보
    private String roomName;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Chat> chatList;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatRoomManagement> chatRoomManagementList;

    public ChatRoomDto toDto(){
        return ChatRoomDto.builder()
                .chatRoomIndex(this.getChatRoomIndex())
                .roomName(this.roomName)
                .auctionIndex(this.auction.getAuctionIndex())
                .createdAt(this.createdAt)
                .chatDtoList(Optional.ofNullable(chatList).map(list -> list.stream().map(Chat::toDto).toList())
                        .orElse(new ArrayList<>()))
                .chatRoomManagementDtoList(Optional.ofNullable(chatRoomManagementList).map(list -> list.stream().map(ChatRoomManagement::toDto).toList())
                        .orElse(new ArrayList<>()))
                .build();
    }

}