package bibid.controller.auction;

import bibid.dto.AuctionDetailDto;
import bibid.dto.AuctionDto;
import bibid.dto.ResponseDto;
import bibid.entity.CustomUserDetails;
import bibid.repository.MemberRepository;
import bibid.service.auction.AuctionService;
import bibid.service.specialAuction.SpecialAuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {
    private final AuctionService auctionService;
    private final SpecialAuctionService specialService;
    private final MemberRepository memberRepository;

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestPart("auctionDto") AuctionDto auctionDto,
                                  @RequestPart("auctionDetailDto") AuctionDetailDto auctionDetailDto,
                                  @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                  @RequestPart(value = "additionalImages", required = false) MultipartFile[] additionalImages,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  @PageableDefault(page = 0, size = 15) Pageable pageable
    ) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            log.info("post auctionDto: {}", auctionDto);
            Page<AuctionDto> auctionDtoList = auctionService.post(auctionDto, auctionDetailDto, thumbnail, additionalImages, customUserDetails.getMember(), pageable);

            log.info("post auctionDto: {}", auctionDtoList);
            responseDto.setPageItems(auctionDtoList);
            responseDto.setStatusCode(HttpStatus.CREATED.value());
            responseDto.setStatusMessage("created");

            return ResponseEntity.created(new URI("/auction")).body(responseDto);
        } catch(Exception e ) {
            log.error("post error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }


}
