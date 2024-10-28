package bibid.dto.livestation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResultDTO {
    @JsonProperty("id")
    int id;

    @JsonProperty("customerId")
    int customerId;

    @JsonProperty("profileName")
    String profileName;
}
