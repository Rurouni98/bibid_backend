package bibid.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MailCheckRequest {
    String verificationCode;
}
