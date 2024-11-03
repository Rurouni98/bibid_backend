package bibid.controller.mypage;

import bibid.dto.*;
import bibid.entity.Account;
import bibid.entity.AuctionInfo;
import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.service.account.AccountService;
import bibid.service.auction.AuctionService;
import bibid.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {
    private final AuctionService auctionService;
    private final MypageService mypageService;
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<?> getMember(){
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            MemberDto findMember = mypageService.findById(1L);

            responseDto.setItem(findMember);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("find temp Member");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e){
            log.error("getMember error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PatchMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(
            @RequestPart("memberDto") MemberDto memberDto,
            @RequestPart(value = "uploadProfiles", required = false) MultipartFile[] uploadProfiles) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            log.info("modify memberDto: {}", memberDto);

            if(uploadProfiles != null){
                log.info("uploadProfile: {}",  (Object[]) uploadProfiles);
            }
            MemberDto modifiedMemberDto = mypageService.modify(memberDto, uploadProfiles);

            responseDto.setItem(modifiedMemberDto);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("mypage updated successfully");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e){
            log.error("modify error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/userInfo/{memberIndex}")
    public ResponseEntity<?> getMemberByMemberIndex(@PathVariable Long memberIndex) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            MemberDto findMember = mypageService.findByMemberIndex(memberIndex);
            log.info("Found member: {}", findMember); // 로그 추가

            responseDto.setItem(findMember);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Found member");

            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            log.error("RuntimeException occurred: {}", e.getMessage()); // 로그 추가
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
        } catch (Exception e) {
            log.error("getMemberByMemberIndex error: ", e); // 이미 존재하는 로그
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("An error occurred");
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<?> getMemberByNickname(@PathVariable String nickname) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            MemberDto findMember = mypageService.findByNickname(nickname);

            responseDto.setItem(findMember);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Found member");

            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
        } catch (Exception e) {
            log.error("getMemberByMemberIndex error: ", e);
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("An error occurred");
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/bidded-auctions")
    public ResponseEntity<?> getMyBiddedAuctions(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Member member = customUserDetails.getMember();
            List<AuctionDto> biddedAuctions = mypageService.findBiddedAuctions(member.getMemberIndex());

            responseDto.setItems(biddedAuctions);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("내가 입찰한 경매 리스트 조회 성공");

            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
        } catch (Exception e) {
            log.error("getMyBiddedAuctions error: ", e);
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestPart("profileImage") MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDto<ProfileImageDto> responseDto = new ResponseDto<>();

        try {
            // 현재 로그인된 사용자의 Member 객체 가져오기
            Member member = customUserDetails.getMember();

            // 프로필 이미지 업로드 서비스 호출
            ProfileImageDto updatedProfileImage = mypageService.uploadOrUpdateProfileImage(profileImage, member);

            responseDto.setItem(updatedProfileImage);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Profile image uploaded successfully");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Profile image upload error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("Failed to upload profile image");
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }


    // 계좌 내역 요청
    @GetMapping("/account")
    public ResponseEntity<?> getAccountHistory(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<Account> responseDto = new ResponseDto<>();

        Long memberIndex = customUserDetails.getMember().getMemberIndex();

        try {
            Member member = customUserDetails.getMember();
            Account account = accountService.findMemberIndex(member.getMemberIndex());

            responseDto.setItem(account);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("내가 입찰한 경매 리스트 조회 성공");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("계좌 사용 내역 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/my-auctions")
    public ResponseEntity<?> getMyAuctions(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            Member member = customUserDetails.getMember();
            List<AuctionDto> myAuctions = mypageService.findMyAuctions(member.getMemberIndex());

            responseDto.setItems(myAuctions);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("내가 등록한 경매 리스트 조회 성공");

            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
        } catch (Exception e) {
            log.error("getMyAuctions error: ", e);
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @DeleteMapping("/my-auctions/{auctionIndex}")
    public ResponseEntity<?> deleteMyAuction(@PathVariable Long auctionIndex,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {
            log.info("deleteMyAuction auctionIndex: {}", auctionIndex);
            auctionService.remove(auctionIndex);

            responseDto.setStatusCode(HttpStatus.NO_CONTENT.value());
            responseDto.setStatusMessage("no content");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("deleteMyAuction error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PatchMapping("/updateProfile/{memberIndex}")
    public ResponseEntity<?> updateProfile(@PathVariable Long memberIndex,
                                           @RequestBody MemberDto memberDto) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            MemberDto updateMemberDto = mypageService.updateProfile(memberIndex, memberDto);

            responseDto.setItem(updateMemberDto);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Profile updated successfully");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("updateProfile error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
}