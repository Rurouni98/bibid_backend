package bibid.dto.livestation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveStationResponseListDTO {
    @JsonProperty("content")
    private List<ContentDTO> content;  // 리스트로 정의
    @JsonProperty("total")
    private int total;
}