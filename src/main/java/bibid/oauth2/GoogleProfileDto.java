package bibid.oauth2;

import lombok.Data;

@Data
public class GoogleProfileDto {

    public String sub;
    public String name;
    public String given_name;
    public String family_name;
    public String picture;
    public String email;
    public String email_verified;

}
