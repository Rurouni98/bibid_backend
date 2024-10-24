package bibid.dto.livestation;

import lombok.Data;

import java.util.Map;

@Data
public class RecordContentDTO {
    private Map<String, RecordInfoDTO> recordList;
}
