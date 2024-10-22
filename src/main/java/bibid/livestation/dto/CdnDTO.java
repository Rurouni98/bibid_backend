package bibid.livestation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CdnDTO {
    @JsonProperty("createCdn")
    Boolean createCdn;

    @JsonProperty("cdnType")
    String cdnType;

    @JsonProperty("profileId")
    String profileId;

    @JsonProperty("regionType")
    String regionType;

    @JsonProperty("instanceNo")
    private int instanceNo;

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("statusName")
    private String statusName;

    @JsonProperty("cdnDomain")
    private String cdnDomain;
}
