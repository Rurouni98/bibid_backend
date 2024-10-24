package bibid.dto.livestation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordVodDTO {
    private String channelId;
    private String fileName;
    private String uploadPath;
    private String recordType;
}
