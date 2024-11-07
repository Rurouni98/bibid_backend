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
public class OriginDTO {
    @JsonProperty("originDomain")
    private String originDomain;

    @JsonProperty("originPath")
    private String originPath;
}
