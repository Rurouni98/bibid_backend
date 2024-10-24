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
public class AlertOptionsDTO {
    @JsonProperty("alertChangeStatus")
    private boolean alertChangeStatus;

    @JsonProperty("alertVodUploadFail")
    private boolean alertVodUploadFail;

    @JsonProperty("alertReStreamFail")
    private boolean alertReStreamFail;
}
