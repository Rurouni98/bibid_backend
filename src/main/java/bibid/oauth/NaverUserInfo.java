package bibid.oauth;

import java.util.Map;

public class NaverUserInfo implements Oauth2UserInfo {

    Map<String, Object> attributes;

    Map<String, Object> properties;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.properties = (Map<String, Object>) attributes.get("naver_account");
    }

    @Override
    public String getProvider() {
        return "naver";
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
        Map<String, Object> profile = (Map<String, Object>) this.properties.get("profile");
        return profile.get("nickname").toString();
    }


//        private Map<String, Object> attributes;
//
//        @Override
//        public String getProvider() {
//            return "naver";
//        }
//
//        @Override
//        public String getProviderId() {
//            return (String) ((Map) attributes.get("response")).get("id");
//        }
//
//        @Override
//        public String getEmail() {
//            return (String) ((Map) attributes.get("response")).get("email");
//        }
//
//        @Override
//        public String getName() {
//            return (String) ((Map) attributes.get("response")).get("name");
//        }
}

