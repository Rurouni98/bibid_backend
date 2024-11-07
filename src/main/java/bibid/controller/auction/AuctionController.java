package bibid.controller.auction;

import bibid.dto.AuctionDetailDto;
import bibid.dto.AuctionDto;
import bibid.dto.ResponseDto;
import bibid.entity.CustomUserDetails;
import bibid.repository.member.MemberRepository;
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
                                  @PageableDefault(page = 0, size = 5) Pageable pageable
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

    @GetMapping("/all") // 모든 상품 호출
    public ResponseEntity<?> getAuctions(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Page<AuctionDto> auctionDtoList = auctionService.findAll(pageable);

            log.info("getAuctions auctionDtoList: {}", auctionDtoList);

            responseDto.setPageItems(auctionDtoList);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            log.error("getAuctions error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/top") //베스트 상품 6개 호출
    public ResponseEntity<?> getTopAuctions(@PageableDefault(page = 0, size = 6) Pageable pageable) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Page<AuctionDto> topAuctions = auctionService.findTopByViewCount(pageable);
            responseDto.setPageItems(topAuctions);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/best/{category}") // 카테고리별 상품을 viewcnt 높은 순으로 호출
    public ResponseEntity<?> getAuctionsByCategory(@PathVariable String category,
                                                   @PageableDefault(page = 0, size = 5) Pageable pageable) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Page<AuctionDto> auctionDtoList = auctionService.findByCategory(category, pageable); // 카테고리로 상품 찾기
            responseDto.setPageItems(auctionDtoList);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/category/{category}") // 카테고리별 상품 호출을 위한 엔드포인트 수정
    public ResponseEntity<?> getAuctionsByCategory2(@PathVariable String category,
                                                    @PageableDefault( page = 0, size = 5) Pageable pageable) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Page<AuctionDto> auctionDtoList = auctionService.findByCategory2(category, pageable);
            responseDto.setPageItems(auctionDtoList);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/conveyor") // 마감시간이 가까운 상품 10개 호출
    public ResponseEntity<?> getConveyor(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Page<AuctionDto> auctionConveyor = auctionService.findConveyor(pageable);
            responseDto.setPageItems(auctionConveyor);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping
    public ResponseEntity<?> getBoards(@RequestParam("searchCondition") String searchCondition,
                                       @RequestParam("searchKeyword") String searchKeyword,
                                       @PageableDefault(page = 0, size = 100) Pageable pageable) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        // 유효성 검사
        if (searchCondition == null || searchCondition.isEmpty() || searchKeyword == null) {
            responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDto.setStatusMessage("Invalid search condition or keyword");
            return ResponseEntity.badRequest().body(responseDto);
        }

        try {
            Page<AuctionDto> auctionDtoList = auctionService.searchFind(searchCondition, searchKeyword, pageable);

            log.info("getBoards auctionDtoList: {}", auctionDtoList);

            responseDto.setPageItems(auctionDtoList);
            responseDto.setItem(AuctionDto.builder()
                    .searchCondition(searchCondition)
                    .searchKeyword(searchKeyword)
                    .build());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            log.error("Error occurred: {}", e.getMessage()); // 오류 메시지 로깅
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
}
