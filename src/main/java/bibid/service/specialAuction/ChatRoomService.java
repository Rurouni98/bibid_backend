package bibid.service.specialAuction;

import bibid.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomService {
    void createChatRoom(Long auctionId);
}
