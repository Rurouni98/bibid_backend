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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long auctionIndex; // 경매 ID

    private List<ChatDto> chatDtoList;
    private List<ChatRoomManagementDto> chatRoomManagementDtoList;

    public ChatRoom toEntity(Auction auction) {
        return ChatRoom.builder()
                .chatRoomIndex(this.chatRoomIndex)
                .roomName(this.roomName)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .auction(auction)
                .chatList(new ArrayList<>())
                .chatRoomManagementList(new ArrayList<>())
                .build();
    }

}

