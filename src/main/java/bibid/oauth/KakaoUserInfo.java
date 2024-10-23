package bibid.oauth;


import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {

    Map<String, Object> attributes;

    Map<String, Object> properties;

    public KakaoUserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
        this.properties = (Map<String, Object>)attributes.get("kakao_account");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return this.attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return this.properties.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> profile = (Map<String, Object>)this.properties.get("profile");
        return profile.get("nickname").toString();
    }
}
