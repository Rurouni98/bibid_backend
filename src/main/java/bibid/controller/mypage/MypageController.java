package bibid.controller.mypage;

import bibid.dto.MemberDto;
import bibid.dto.ResponseDto;
import bibid.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {
    private final MypageService mypageService;

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
                log.info("uploadProfile: {}", uploadProfiles);
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
}