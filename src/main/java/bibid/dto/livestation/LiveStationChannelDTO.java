package bibid.dto.livestation;

import bibid.entity.LiveStationChannel;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiveStationChannelDTO {

    private Long liveStationChannelIndex;

    private String channelId;
    private String channelStatus;
    private int cdnInstanceNo;
    private String cdnStatusName;

    private String publishUrl;
    private String streamKey;
    private List<String> serviceUrlList;

    private boolean isAvailable;
    private boolean isAllocated;

    public LiveStationChannel toEntity() {
        return LiveStationChannel.builder()
                .liveStationChannelIndex(this.liveStationChannelIndex)
                .channelId(this.channelId)
                .channelStatus(this.channelStatus)
                .cdnInstanceNo(this.cdnInstanceNo)
                .cdnStatusName(this.cdnStatusName)
                .publishUrl(this.publishUrl)
                .streamKey(this.streamKey)
                .isAvailable(this.isAvailable)
                .isAllocated(this.isAllocated)
                .serviceUrlList(new ArrayList<>())
                .build();
    }

}
