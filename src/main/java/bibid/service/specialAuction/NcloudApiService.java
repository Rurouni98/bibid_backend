package bibid.service.specialAuction;

import bibid.dto.NcloudApiRequestDTO;

public interface NcloudApiService {
    String callNcloudApi(NcloudApiRequestDTO requestDTO);
}
