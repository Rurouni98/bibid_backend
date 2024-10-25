package bibid.dto.livestation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GlobalEdgeResponseDTO {
    @JsonProperty("code")
    String code;

    @JsonProperty("message")
    String message;

    @JsonProperty("result")
    List<ResultDTO> result;
}
