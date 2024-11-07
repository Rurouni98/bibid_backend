package bibid.controller.auctionItemDetail;

import bibid.dto.*;
import bibid.entity.Account;
import bibid.entity.Auction;
import bibid.entity.Member;
import bibid.entity.CustomUserDetails;
import bibid.repository.auction.AuctionRepository;
import bibid.service.account.AccountService;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import bibid.service.qna.QnaServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auctionDetail")
@RequiredArgsConstructor
@Slf4j
public class AuctionItemDetailController {

    private final AuctionItemDetailService auctionItemDetailService;
    private final AccountService accountService;
    private final QnaServiceImpl qnaService;
    private final AuctionRepository auctionRepository;

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
                                         @RequestBody BidRequestDto bidRequestDto,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<BidRequestDto> bidResponseDto = new ResponseDto<>();
        Member member = customUserDetails.getMember();

        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("auction not exist"));

        log.info("Bid request received - auctionIndex: {}, bidder: {}, bid amount: {}",
                auctionIndex, member.getNickname(), bidRequestDto.getUserBiddingPrice());

//        String checkAccount = auctionItemDetailService.biddingItem(auctionIndex, bidRequestDto, member.getMemberIndex());

//        if (checkAccount.equals("fail")) {
//            log.error("Bid validation failed for auctionIndex: {}. Reason: {}", auctionIndex, checkAccount);
//
//            bidResponseDto.setStatusMessage(checkAccount);
//            bidResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//            return ResponseEntity.internalServerError().body(bidRequestDto);
//        }

        log.info("Bid validation passed for auctionIndex: {}. Proceeding to place bid.", auctionIndex);

        try {
            // 입찰 업데이트 로직 실행
            auctionItemDetailService.updateAuctionItemDetail(auctionIndex, bidRequestDto, member);

            // 성공 응답 설정
            bidResponseDto.setItem(bidRequestDto);
            bidResponseDto.setStatusCode(HttpStatus.OK.value());
            bidResponseDto.setStatusMessage("입찰 성공");

            log.info("Bid placed successfully for auctionIndex: {} by bidder: {} with bid amount: {}",
                    auctionIndex, member.getNickname(), bidRequestDto.getUserBiddingPrice());
            return ResponseEntity.ok(bidResponseDto);
        } catch (Exception e) {
            // 오류 처리
            log.error("Bid placement failed for auctionIndex: {}. Error: {}", auctionIndex, e.getMessage(), e);

            bidResponseDto.setStatusMessage("입찰 실패: " + e.getMessage());
            bidResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.internalServerError().body(bidResponseDto);
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
