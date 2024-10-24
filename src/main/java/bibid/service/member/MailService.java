package bibid.service.member;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {


    private final JavaMailSender MailSender;

    private static int number;

    // 랜덤으로 숫자 생성
    public static void createNumber() {
        number = (int) (Math.random() * (90000)) + 100000;
    }

    public MimeMessage CreateMail(String email) {
        createNumber();

        MimeMessage message = MailSender.createMimeMessage();

            try{message.setFrom(email);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[BIBID] 요청하신 통합회원 인증번호를 안내해드립니다.");
            String body = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; }" +
                    "h1 { color: #333; }" +
                    "h3 { color: #555; }" +
                    ".footer { margin-top: 20px; font-size: 12px; color: #888; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h3>이메일 인증코드</h3>" +
                    "<p>BIBID에 입력한 이메일 주소가 올바른지 확인하기 위한 인증번호입니다.\n</p>" +
                    "<p>아래의 인증번호를 복사하여 이메일 인증을 완료해주세요.</p>" +
                    "<h1>" + number + "</h1>" +
                    "<p>감사합니다.</p>" +
                    "<div class='footer'>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public int sendMail(String mail) {
        MimeMessage message = CreateMail(mail);
        MailSender.send(message);

        return number;
    }
}