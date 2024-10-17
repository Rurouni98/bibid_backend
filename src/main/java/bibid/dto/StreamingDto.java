package bibid.dto;

import bibid.entity.Auction;
import bibid.entity.Streaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamingDto {
    private Long streamingIndex;
    private Long auctionIndex;
    private List<String> streamUrlList;

    private String channelId;
    private String channelName;
    private String channelStatus;

    private int cdnInstanceNo;
    private String cdnStatus;

    private String publishUrl;
    private String streamKey;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Streaming toEntity(Auction auction) {
        return Streaming.builder()
                .streamingIndex(this.streamingIndex)
                .auction(auction)
                .streamUrlList(this.streamUrlList)
                .cdnInstanceNo(this.cdnInstanceNo)
                .cdnStatus(this.cdnStatus)
                .publishUrl(this.publishUrl)
                .streamKey(this.streamKey)
                .channelId(this.channelId)
                .channelName(this.channelName)
                .channelStatus(this.channelStatus)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }
}
