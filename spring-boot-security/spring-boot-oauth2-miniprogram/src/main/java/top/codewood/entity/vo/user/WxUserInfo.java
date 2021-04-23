package top.codewood.entity.vo.user;

import java.io.Serializable;

public class WxUserInfo implements Serializable {

    private String appid;
    private String openid;
    private String unionid;
    private String nickname;
    private String gender;
    private String province;
    private String city;
    private String avatarUrl;
    private String defaultPassword;
    private String sessionKey;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public String toString() {
        return "WxUserInfo{" +
                "appid='" + appid + '\'' +
                ", openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", defaultPassword='" + defaultPassword + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                '}';
    }

    public static class Builder {
        private String appid;
        private String openid;
        private String unionid;
        private String nickname;
        private String gender;
        private String province;
        private String city;
        private String avatarUrl;
        private String defaultPassword;
        private String sessionKey;

        public Builder appid(String appid) {
            this.appid = appid;
            return this;
        }

        public Builder openid(String openid) {
            this.openid = openid;
            return this;
        }

        public Builder unionid(String unionid) {
            this.unionid = unionid;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder province(String province) {
            this.province = province;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder defaultPassword(String defaultPassword) {
            this.defaultPassword = defaultPassword;
            return this;
        }

        public Builder sessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
            return this;
        }

        public WxUserInfo build() {
            WxUserInfo wxUserInfo = new WxUserInfo();
            wxUserInfo.setAppid(this.appid);
            wxUserInfo.setOpenid(this.openid);
            wxUserInfo.setUnionid(this.unionid);
            wxUserInfo.setNickname(this.nickname);
            wxUserInfo.setGender(this.gender);
            wxUserInfo.setProvince(this.province);
            wxUserInfo.setCity(this.city);
            wxUserInfo.setAvatarUrl(this.avatarUrl);
            wxUserInfo.setDefaultPassword(this.defaultPassword);
            wxUserInfo.setSessionKey(this.sessionKey);
            return wxUserInfo;
        }

    }

}
