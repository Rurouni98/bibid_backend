package bibid.livestation.dto;

import bibid.livestation.entity.LiveStationChannel;
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
                .serviceUrlList(new ArrayList<>())
                .build();
    }

}
