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
public class RecordDTO {
    @JsonProperty("format")
    String format;

    @JsonProperty("type")
    String type;

    @JsonProperty("bucketName")
    String bucketName;

    @JsonProperty("filePath")
    String filePath;

    @JsonProperty("accessControl")
    String accessControl;
}
