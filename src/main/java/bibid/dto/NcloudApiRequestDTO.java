package bibid.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NcloudApiRequestDTO {
    private String secretKey;
    private String accessKey;
    private String apiUrl;
    private String method;
    private String requestBody;
}
