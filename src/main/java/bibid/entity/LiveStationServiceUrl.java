package bibid.entity;

import bibid.dto.livestation.LiveStationServiceUrlDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@SequenceGenerator(
        name = "LiveStationServiceUrlSeqGenerator",
        sequenceName = "LIVE_STATION_SERVICE_URL_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class LiveStationServiceUrl {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "LiveStationServiceUrlSeqGenerator"
    )
    private Long LiveStationServiceUrlIndex;

    @Column(name = "service_url", nullable = false)
    private String serviceUrl;

    @ManyToOne
    @JoinColumn(name = "liveStationChannelIndex")
    private LiveStationChannel liveStationChannel;

    public LiveStationServiceUrlDTO toDto() {
        return LiveStationServiceUrlDTO.builder()
                .liveStationChannelIndex(this.LiveStationServiceUrlIndex)
                .serviceUrl(this.serviceUrl)
                .LiveStationServiceUrlIndex(this.LiveStationServiceUrlIndex)
                .build();
    }

}
