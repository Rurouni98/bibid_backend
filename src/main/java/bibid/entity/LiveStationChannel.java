package bibid.entity;

import bibid.dto.livestation.LiveStationChannelDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@SequenceGenerator(
        name = "liveStationChannelSeqGenerator",
        sequenceName = "LIVE_STATION_CHANNEL_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class LiveStationChannel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "liveStationChannelSeqGenerator"
    )
    private Long liveStationChannelIndex;

    private String channelId;
    private String channelStatus;
    private int cdnInstanceNo;
    private String cdnStatusName;

    private String publishUrl;
    private String streamKey;

    @OneToMany(mappedBy = "liveStationChannel", cascade = CascadeType.ALL)
    private List<LiveStationServiceUrl> serviceUrlList;

    private boolean isAvailable;
    private boolean isAllocated;

    public LiveStationChannelDTO toDto(){
        return LiveStationChannelDTO.builder()
                .liveStationChannelIndex(this.liveStationChannelIndex)
                .channelId(this.channelId)
                .channelStatus(this.channelStatus)
                .cdnInstanceNo(this.cdnInstanceNo)
                .cdnStatusName(this.cdnStatusName)
                .publishUrl(this.publishUrl)
                .streamKey(this.streamKey)
                .isAvailable(this.isAvailable)
                .isAllocated(this.isAllocated)
                .serviceUrlList(this.serviceUrlList != null ?
                        this.serviceUrlList.stream().map(LiveStationServiceUrl::getServiceUrl)
                                .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}