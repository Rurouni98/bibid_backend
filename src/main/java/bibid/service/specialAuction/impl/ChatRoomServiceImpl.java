package bibid.service.specialAuction.impl;

import bibid.entity.Auction;
import bibid.entity.ChatRoom;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.specialAuction.ChatRoomRepository;
import bibid.service.specialAuction.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final AuctionRepository auctionRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void createChatRoom(Long auctionIndex) {
        // 경매 정보 조회
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .auction(auction)
                .roomName("경매 #" + auctionIndex + " 채팅방")
                .startTime(auction.getStartingLocalDateTime().minusMinutes(20))
                .endTime(auction.getEndingLocalDateTime().plusMinutes(20))
                .build();

        auction.setChatRoom(chatRoom);
        auction.setChatRoomCreated(true);
        auctionRepository.save(auction);

        System.out.println("채팅방이 생성되었습니다. 경매 ID: " + auctionIndex);
    }
}
