package bibid.controller.member;

import bibid.dto.EmailDto;
import bibid.dto.MailCheckRequest;
import bibid.dto.ResponseDto;
import bibid.service.member.MailService;
import bibid.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;
    private int number; // 이메일 인증 숫자를 저장하는 변수

    // 인증 이메일 전송
    @PostMapping("/mailSend")
    public HashMap<String, Object> mailSend(@RequestBody EmailDto emailRequest) {
        HashMap<String, Object> map = new HashMap<>();

        try {
            number = mailService.sendMail(emailRequest.getEmail());
            String num = String.valueOf(number);

            map.put("success", Boolean.TRUE);
            map.put("number", num);
        } catch (Exception e) {
            map.put("success", Boolean.FALSE);
            map.put("error", e.getMessage());

        }
        return map;
    }

    // 인증번호 일치여부 확인
    @PostMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestBody MailCheckRequest requst) {

        boolean isMatch = requst.getVerificationCode().equals(String.valueOf(number));

        return ResponseEntity.ok(isMatch);
    }

    @PostMapping("/findByEmail")
    public ResponseEntity<?> findByEmail(@RequestBody Map<String, String> request) {
        ResponseDto<String> responseDto = new ResponseDto<>();

        String email = request.get("email");

        try {
            String findByEmail = memberService.findByEmail(email.trim());

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(findByEmail);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("findByEmail error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/modifyPasswd")
    public ResponseEntity<?> modifyPasswd(@RequestParam String newPasswd) {
        ResponseDto<String> responseDto = new ResponseDto<>();

        try {
            String modifyPasswd = memberService.modifyPasswd(newPasswd);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(modifyPasswd);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("findByEmail error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }}
