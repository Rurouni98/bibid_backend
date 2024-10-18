package bibid.controller.naverApi;

import bibid.dto.NcloudApiRequestDTO;
import bibid.service.specialAuction.NcloudApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ncloud")
@Slf4j
@RequiredArgsConstructor
public class useNaverApiController {

    private final NcloudApiService ncloudApiService;

    @PostMapping("/check")
    public ResponseEntity<?> checkNcloudApi(@RequestBody NcloudApiRequestDTO requestDTO) {
        log.info("Dto : {}", requestDTO);
        String response = ncloudApiService.callNcloudApi(requestDTO);
        return ResponseEntity.ok(response);
    }

}
