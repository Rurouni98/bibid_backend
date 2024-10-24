package bibid.oauth2;

import lombok.Data;

@Data //(2)
public class OauthToken { //(1)
    private String token_type;
    private String access_token;
    private String id_token;
    private int expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;
    private String scope;
}
