package bibid.controller.specialAuction;

import bibid.dto.livestation.LiveStationServiceUrlDTO;
import bibid.entity.LiveStationChannel;
import bibid.entity.LiveStationServiceUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CDNUpdateController {

    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/cdn.notify/{auctionIndex}")
    @SendTo("/topic/cdn-updates/{auctionIndex}")
    public List<LiveStationServiceUrlDTO> sendCdnUpdate(String auctionIndex, LiveStationChannel channel) {
        return channel.getServiceUrlList().stream()
                .map(LiveStationServiceUrl::toDto)
                .toList();
    }

}
