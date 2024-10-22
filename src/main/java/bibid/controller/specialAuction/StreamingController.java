package bibid.controller.specialAuction;

import bibid.dto.ResponseDto;
import bibid.dto.StreamingDto;
import bibid.service.specialAuction.SpecialAuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
@Slf4j
public class StreamingController {

    private final SpecialAuctionService specialAuctionService;

    @GetMapping
    public ResponseEntity<?> findStreamingByAuctionIndex(
            @RequestParam("auctionIndex") Long auctionIndex) {

        ResponseDto<StreamingDto> responseDto = new ResponseDto<>();

        try {

            StreamingDto streamingDto = specialAuctionService.findStreamingByAuctionIndex(auctionIndex);

            responseDto.setItem(streamingDto);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("getAuctions error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
}
