package bibid.controller.auctionItemDetail;

import bibid.dto.*;
import bibid.entity.AuctionInfo;
import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctionDetail")
@RequiredArgsConstructor
@Slf4j
public class AuctionItemDetailController {

    private final AuctionItemDetailService auctionItemDetailService;

    @GetMapping("/category-item-detail/{auctionIndex}")
    public ResponseEntity<?> getItemDetail(@PathVariable("auctionIndex") Long auctionIndex){

        ResponseDto<AuctionResponseDto> responseDto = new ResponseDto<>();

        try {

            log.info("auctionIndex 확인 : {}", auctionIndex);

            AuctionDto auctionItem = auctionItemDetailService.findAuctionItem(auctionIndex);
            MemberDto seller = auctionItemDetailService.findSeller(auctionIndex); // 판매자 정보 가져오기

            List<AuctionInfoDto> auctionBidInfo = auctionItemDetailService.findLastBidder(auctionIndex); // 최근 세명의 입찰 정보

            List<MemberDto> biddingMember = auctionItemDetailService.findLastBidderName(auctionBidInfo);

            List<String> infoExtension = auctionItemDetailService.findAuctionInfoEtc(auctionIndex);
//            System.out.println(infoExtension);// 추가정보 - 현재 경매 최고가, 현재 경매 총 입찰수

            SellerInfoDto sellerDetailInfo = auctionItemDetailService.findSellerInfo(auctionIndex);
//            System.out.println(sellerDetailInfo); // 판매자의 맴버디테일, 셀러인포. 상호명 대표자 등.

            List<String> auctionImages = auctionItemDetailService.findAuctionImagesByAuctionIndex(auctionIndex);
            System.out.println(auctionImages); // 옥션이미지경로, 필수값 , 첫번째 값으로 썸네일저장


            AuctionResponseDto auctionResponse = new AuctionResponseDto();
            auctionResponse.setAuctionItem(auctionItem);
            auctionResponse.setSeller(seller);

            auctionResponse.setAuctionBidInfo(auctionBidInfo);
            auctionResponse.setBiddingMember(biddingMember);

            auctionResponse.setInfoExtension(infoExtension);
            auctionResponse.setSellerDetailInfo(sellerDetailInfo);

            auctionResponse.setAuctionImages(auctionImages);

            responseDto.setItem(auctionResponse);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("complete : read item detail");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e){
            log.error("err : read item detail : {}", e.getMessage());

            responseDto.setStatusMessage(e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.internalServerError().body(responseDto);
        }
    }


    @PostMapping("/category-item-detail/{auctionIndex}")
    public ResponseEntity<?> biddingItem(@PathVariable("auctionIndex") Long auctionIndex,
                                         @RequestBody BidRequestDto bidRequestDto,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<BidRequestDto> bidResponseDto = new ResponseDto<>();

        log.info("Received bid request for auctionIndex: {}, request data: {}", auctionIndex, bidRequestDto);

        try {

            // 입찰 업데이트 로직 실행
            auctionItemDetailService.updateAuctionItemDetail(auctionIndex, bidRequestDto, customUserDetails.getMember());

            // 성공 응답 설정
            bidResponseDto.setItem(bidRequestDto);
            bidResponseDto.setStatusCode(HttpStatus.OK.value());
            bidResponseDto.setStatusMessage("입찰 성공");

            log.info("Bid placed successfully for auctionIndex: {}", auctionIndex);
            return ResponseEntity.ok(bidResponseDto);
        } catch (Exception e) {
            // 오류 처리
            log.error("Bid placement failed for auctionIndex: {}. Error: {}", auctionIndex, e.getMessage());

            bidResponseDto.setStatusMessage("입찰 실패: " + e.getMessage());
            bidResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.internalServerError().body(bidResponseDto);
        }
    }
}
