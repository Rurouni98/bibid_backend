package bibid.service;

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
            message.setSubject("이메일 인증");
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
                    "<p>성공적인 경매를 위한 BIBID 서비스에 가입하신 것을 환영합니다.</p>" +
                    "<p>아래의 인증코드를 입력하시면 가입이 정상적으로 완료됩니다.</p>" +
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