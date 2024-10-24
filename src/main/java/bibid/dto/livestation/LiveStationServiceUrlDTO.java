package bibid.dto.livestation;

import bibid.entity.LiveStationServiceUrl;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiveStationServiceUrlDTO {

    private Long LiveStationServiceUrlIndex;

    private String serviceUrl;

    private Long liveStationChannelIndex;

    public LiveStationServiceUrl toEntity() {
        return LiveStationServiceUrl.builder()
                .LiveStationServiceUrlIndex(this.LiveStationServiceUrlIndex)
                .serviceUrl(this.serviceUrl)
                .LiveStationServiceUrlIndex(this.liveStationChannelIndex)
                .build();
    }

}
