package bibid.repository.specialAuction;

import bibid.entity.ChatRoom;
import bibid.entity.ChatRoomManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomManagementRepository extends JpaRepository<ChatRoomManagement, Long> {
    Optional<ChatRoomManagement> findByChatRoomAndParticipantIndex(ChatRoom chatRoom, Long aLong);

    Optional<ChatRoomManagement> findByChatRoomAndParticipantNickname(ChatRoom chatRoom, String nickname);
}
