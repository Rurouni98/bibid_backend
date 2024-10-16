package bibid.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MypageDto {
    private Long id;
    private String userId;
    private String username;
    private String password;
    private String tel;
    private String phone;
    private String email;
    private String address;
    private String address_detail;
    private String role;
    private String token;
}
