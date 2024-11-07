package bibid.dto.livestation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveStationRequestDTO {
     @JsonProperty("channelName")
     String channelName;

     @JsonProperty("cdn")
     CdnDTO cdn;

     @JsonProperty("qualitySetId")
     int qualitySetId;

     @JsonProperty("useDvr")
     boolean useDvr;

     @JsonProperty("immediateOnAir")
     boolean immediateOnAir;

     @JsonProperty("timemachineMin")
     int timemachineMin;

     @JsonProperty("record")
     RecordDTO record;

     @JsonProperty("isStreamFailOver")
     boolean isStreamFailOver;

}

