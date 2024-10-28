package bibid.oauth2;

import lombok.Data;

@Data
public class NaverProfileDto {

    public String resultcode;
    public String message;
    public Response response;

    @Data
    public class Response {
        public String id;
        public String name;
        public String nickname;
        public String profile_image;
        public String email;
        public String mobile;
        public String mobile_e164;
    }


}
