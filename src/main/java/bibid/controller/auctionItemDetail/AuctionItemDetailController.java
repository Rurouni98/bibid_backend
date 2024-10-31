package bibid.controller.auctionItemDetail;

import bibid.dto.*;
import bibid.entity.Auction;
import bibid.entity.Member;
import bibid.service.auction.AuctionService;
import bibid.service.auction.impl.AuctionServiceImpl;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import bibid.service.qna.QnaServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auctionDetail")
@RequiredArgsConstructor
@Slf4j
public class AuctionItemDetailController {


    private final AuctionItemDetailService auctionItemDetailService;
    private final QnaServiceImpl qnaService;



    @GetMapping("/category-item-detail/{auctionIndex}")
    public ResponseEntity<?> getItemDetail(@PathVariable("auctionIndex") Long auctionIndex){

        ResponseDto<AuctionResponseDto> responseDto = new ResponseDto<>();
        String checkingMessage = auctionItemDetailService.auctionChecking(auctionIndex);

        if (!checkingMessage.equals("접속성공. 옥션번호 : " + auctionIndex)){
            responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDto.setStatusMessage(checkingMessage); // 유효하지 않은 접근 메시지 추가
            return ResponseEntity.badRequest().body(responseDto); // 400 BAD REQUEST 응답
        }

        try {

            log.info("auctionIndex 확인 : {}", auctionIndex);

            AuctionDto auctionItem = auctionItemDetailService.findAuctionItem(auctionIndex);
            auctionItemDetailService.plusAuctionView(auctionIndex);
            MemberDto seller = auctionItemDetailService.findSeller(auctionIndex); // 판매자 정보 가져오기

            List<AuctionInfoDto> auctionBidInfo = auctionItemDetailService.findLastBidder(auctionIndex); // 최근 세명의 입찰 정보

            List<MemberDto> biddingMember = auctionItemDetailService.findLastBidderName(auctionBidInfo);

            List<String> infoExtension = auctionItemDetailService.findAuctionInfoEtc(auctionIndex);
//            System.out.println(infoExtension);// 추가정보 - 현재 경매 최고가, 현재 경매 총 입찰수

            SellerInfoDto sellerDetailInfo = auctionItemDetailService.findSellerInfo(auctionIndex);
//            System.out.println(sellerDetailInfo); // 판매자의 맴버디테일, 셀러인포. 상호명 대표자 등.

            List<String> auctionImages = auctionItemDetailService.findAuctionImagesByAuctionIndex(auctionIndex);
            System.out.println(auctionImages); // 옥션이미지경로, 필수값 , 첫번째 값으로 썸네일저장

            List<QnADto> qnAList = qnaService.findQnaListByAuctionIndex(auctionIndex);
            System.out.println(qnAList);


            AuctionResponseDto auctionResponse = new AuctionResponseDto();
            auctionResponse.setAuctionItem(auctionItem);
            auctionResponse.setSeller(seller);

            auctionResponse.setAuctionBidInfo(auctionBidInfo);
            auctionResponse.setBiddingMember(biddingMember);

            auctionResponse.setInfoExtension(infoExtension);
            auctionResponse.setSellerDetailInfo(sellerDetailInfo);

            auctionResponse.setAuctionImages(auctionImages);
            auctionResponse.setQnAList(qnAList);

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
                                         @RequestBody BidRequestDto bidRequestDto){
        ResponseDto<BidRequestDto> bidResponseDto = new ResponseDto<>();

        System.out.println("post api 호출");
        System.out.println("auctionIndex::"+auctionIndex);
        System.out.println("reQuestDto : " + bidRequestDto.toString());
// 맴버 받아오기 추가하기 맴버인덱스랑 맴버닉네임추가해주기
        Member member = new Member();
        MemberDto memberDto = member.toDto();
        memberDto.setMemberIndex(1L);
        // 맴버 받아와서 아래 updateAuctionItemDetail 메서드의
        // auctionInfo.setBidder 부분에서 memberIndex 로 맴버 조회해 비더 넣어주기
        // repository 메서드 새로만들기
        String checkAccount = auctionItemDetailService.biddingItem(auctionIndex, bidRequestDto, memberDto);
        if (checkAccount.equals("fail")){
            log.error("err : post Auction bidding failed : {}", checkAccount);

            bidResponseDto.setStatusMessage(checkAccount);
            bidResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.internalServerError().body(bidRequestDto);
        }

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

    @PostMapping("/category-item-detail/{auctionIndex}/inquiry")
    public ResponseEntity<?> postInquiry(@PathVariable("auctionIndex") Long auctionIndex,
                                         @RequestBody QnADto qnADto){
        System.out.println("문의등록요청::::::::::::::::::::::");
        qnADto.setAuctionIndex(auctionIndex);
        System.out.println(qnADto);
        // 1L 자리에 맴버 받아오기 추가해서 넣기 질문등록자
        qnADto.setMemberIndex(1L);
        ResponseDto<?> responseDto = new ResponseDto<>();

        try {
            qnaService.postQnA(qnADto);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }


}
