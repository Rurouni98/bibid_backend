package bibid.controller.auctionItemDetail;

import bibid.dto.*;
import bibid.service.auction.AuctionService;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctionDetail")
@RequiredArgsConstructor
@Slf4j
public class AuctionItemDetailController {

    @Autowired
    private final AuctionItemDetailService auctionItemDetailService;


    @GetMapping("/category-item-detail/{auctionIndex}")
    public ResponseEntity<?> getItemDetail(@PathVariable("auctionIndex") Long auctionIndex){

        ResponseDto<AuctionResponseDto> responseDto = new ResponseDto<>();

        try {
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


    @PostMapping("/api/category-item-detail/{auctionIndex}")
    public ResponseEntity<?> biddingItem(@PathVariable("auctionIndex") Long auctionIndex,
                                         @RequestBody BidRequestDto bidRequestDto){
        ResponseDto<BidRequestDto> bidResponseDto = new ResponseDto<>();

        System.out.println("post api 호출");
        System.out.println("auctionIndex::"+auctionIndex);
        System.out.println("reQuestDto : " + bidRequestDto.toString());
// 맴버 받아오기 추가하기
        try {
            auctionItemDetailService.updateAuctionItemDetail(auctionIndex, bidRequestDto);

            bidResponseDto.setItem(bidRequestDto);
            System.out.println("ResponseDto : " + bidResponseDto);
            bidResponseDto.setStatusCode(HttpStatus.OK.value());
            bidResponseDto.setStatusMessage("OK");
            return ResponseEntity.ok("success");
        } catch (Exception e){
            log.error("err : post Auction bidding failed : {}", e.getMessage());

            bidResponseDto.setStatusMessage(e.getMessage());
            bidResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.internalServerError().body(bidRequestDto);
        }
    }


}
