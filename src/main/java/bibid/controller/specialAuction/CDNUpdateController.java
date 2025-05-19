package bibid.controller.specialAuction;

import bibid.dto.livestation.LiveStationChannelDTO;
import bibid.entity.Auction;
import bibid.entity.LiveStationChannel;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CDNUpdateController {

    private final SpecialAuctionRepository specialAuctionRepository;

    /**
     * YouTube 라이브 방송용 CDN 정보 전달 (streamKey, watch URL 포함)
     */
    @MessageMapping("/cdn.notify/{auctionIndex}")
    @SendTo("/topic/cdn-updates/{auctionIndex}")
    public LiveStationChannelDTO sendCdnUpdate(@DestinationVariable Long auctionIndex) {
        Auction auction = specialAuctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매를 찾을 수 없습니다."));

        LiveStationChannel channel = auction.getLiveStationChannel();
        if (channel == null) {
            throw new IllegalStateException("경매에 연결된 스트리밍 채널이 없습니다.");
        }

        log.info("CDN 업데이트 요청 - auctionIndex: {}, youtubeWatchUrl: {}", auctionIndex, channel.getYoutubeWatchUrl());

        return channel.toDto();
    }
}
