package bibid.controller.account;

import bibid.dto.AccountDto;
import bibid.dto.AccountUseHistoryDto;
import bibid.dto.ResponseDto;
import bibid.entity.CustomUserDetails;
import bibid.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

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

    // 구매 요청
    @PostMapping("/buy")
    public ResponseEntity<?> buyAuction(@RequestBody AccountUseHistoryDto accountUseHistoryDto,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<AccountDto> responseDto = new ResponseDto<>();
        Long memberIndex = customUserDetails.getMember().getMemberIndex();

        try {
            AccountDto updatedAccount = accountService.buyAuction(accountUseHistoryDto, memberIndex);
            responseDto.setItem(updatedAccount);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("구매 요청 성공");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("구매 요청 실패: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("구매 요청 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    // 판매 요청
    @PostMapping("/sell")
    public ResponseEntity<?> sellAuction(@RequestBody AccountUseHistoryDto accountUseHistoryDto,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<AccountDto> responseDto = new ResponseDto<>();
        Long memberIndex = customUserDetails.getMember().getMemberIndex();

        try {
            AccountDto updatedAccount = accountService.sellAuction(accountUseHistoryDto, memberIndex);
            responseDto.setItem(updatedAccount);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("판매 요청 성공");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("판매 요청 실패: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("판매 요청 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

}

