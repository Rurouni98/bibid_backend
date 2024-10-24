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
public class LiveStationResponseDTO {
    @JsonProperty("content")
    private ContentDTO content;
    @JsonProperty("backupStreamKey")
    private String backupStreamKey;
    @JsonProperty("isStreamFailOver")
    private boolean isStreamFailOver;
}
