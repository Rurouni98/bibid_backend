package bibid.dto.livestation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStationInfoDTO {
    private String channelId;
    private String channelName;
    private String channelStatus;
    private int cdnInstanceNo;
    private String cdnStatusName;

    private String publishUrl;
    private String streamKey;
}
