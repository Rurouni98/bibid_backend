package bibid.controller.account;

import bibid.dto.AccountDto;
import bibid.dto.AccountUseHistoryDto;
import bibid.dto.AuctionDto;
import bibid.dto.ResponseDto;
import bibid.entity.*;
import bibid.repository.account.AccountRepository;
import bibid.repository.account.AccountUseHistoryRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final AccountUseHistoryRepository accountUseHistoryRepository;
    private final AuctionRepository auctionRepository;
    private final AccountRepository accountRepository;

    // 충전 요청
    @PostMapping("/charge")
    public ResponseEntity<?> chargeAccount(@RequestBody AccountUseHistoryDto accountUseHistoryDto,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDto<AccountDto> responseDto = new ResponseDto<>();
        Long memberIndex = customUserDetails.getMember().getMemberIndex();

        try {
            AccountDto updatedAccount = accountService.chargeAccount(accountUseHistoryDto, memberIndex);
            responseDto.setItem(updatedAccount);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("충전 요청 성공");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("충전 요청 실패: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("충전 요청 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    // 환전 요청
    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeAccount(@RequestBody AccountUseHistoryDto accountUseHistoryDto,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<AccountDto> responseDto = new ResponseDto<>();
        Long memberIndex = customUserDetails.getMember().getMemberIndex();

        try {
            AccountDto updatedAccount = accountService.exchangeAccount(accountUseHistoryDto, memberIndex);
            responseDto.setItem(updatedAccount);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("환전 요청 성공");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("환전 요청 실패: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("환전 요청 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    // 구매 확정
    @PostMapping("/confirm")
    public ResponseEntity<?> receiveAuctionPayment(@RequestBody AuctionDto auctionDto) {
        ResponseDto<AccountDto> responseDto = new ResponseDto<>();

        // 경매 인덱스를 가져옵니다.
        Long auctionIndex = auctionDto.getAuctionIndex();
        Auction auction = auctionRepository.findById(auctionIndex).orElseThrow(
                () -> new RuntimeException("Auction not exist")
        );

        // 낙찰자(구매자) 정보와 판매자 정보를 가져옵니다.
        Member seller = auction.getMember();

        try {
            // 낙찰 금액을 가져옵니다.
            int winningBidAmount = auction.getAuctionDetail().getWinningBid().intValue();

            // 판매자의 계좌를 업데이트합니다.
            Account sellerAccount = seller.getAccount();
            int currentBalance = Integer.parseInt(sellerAccount.getUserMoney());
            int newBalance = currentBalance + (int) (winningBidAmount * 0.9);
            sellerAccount.setUserMoney(String.valueOf(newBalance));
            accountRepository.save(sellerAccount);

            // 판매자의 계좌에 거래 내역을 추가합니다.
            AccountUseHistoryDto accountUseHistoryDto = AccountUseHistoryDto.builder()
                    .auctionType("일반 경매")
                    .accountIndex(sellerAccount.getAccountIndex())
                    .afterBalance(String.valueOf(newBalance))
                    .beforeBalance(String.valueOf(currentBalance))
                    .createdTime(LocalDateTime.now())
                    .productName(auction.getProductName())
                    .changeAccount(String.valueOf(winningBidAmount * 0.9))
                    .useType("수령")
                    .memberIndex(seller.getMemberIndex())
                    .auctionIndex(auctionIndex)
                    .build();

            AccountUseHistory history = accountUseHistoryDto.toEntity(seller, auction, sellerAccount);
            accountUseHistoryRepository.save(history);
            
            auction.setAuctionStatus("구매 확정");
            auctionRepository.save(auction);

            responseDto.setItem(sellerAccount.toDto());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("대금 수령 성공");

            log.info("대금 수령 완료 - 판매자: {}, 낙찰 금액: {}", seller.getNickname(), winningBidAmount);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("대금 수령 요청 실패: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("대금 수령 요청 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

}

