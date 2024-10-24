package bibid.dto;

import bibid.entity.Auction;
import bibid.entity.ChatRoom;
import bibid.entity.ChatRoomManagement;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {

    private Long chatRoomIndex;
    private String roomName;
    private Long auctionIndex; // 경매 ID

    private LocalDateTime createdAt;
    private List<ChatDto> chatDtoList;
    private List<ChatRoomManagementDto> chatRoomManagementDtoList;

    public ChatRoom toEntity(Auction auction) {
        return ChatRoom.builder()
                .chatRoomIndex(this.chatRoomIndex)
                .roomName(this.roomName)
                .auction(auction)
                .createdAt(this.createdAt)
                .chatList(new ArrayList<>())
                .chatRoomManagementList(new ArrayList<>())
                .build();
    }

}

