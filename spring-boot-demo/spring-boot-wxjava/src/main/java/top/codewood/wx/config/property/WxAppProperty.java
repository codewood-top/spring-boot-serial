package top.codewood.wx.config.property;

import top.codewood.wx.common.api.WxConstants;

public class WxAppProperty {

    private String appid;
    private String secret;
    /**
     * 应用类型 公众号 | 小程序 | app
     * @see top.codewood.wx.common.api.WxConstants.AppType
     */
    private String type;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void check() {
        if (appid == null || appid.trim() == "") {
            throw new RuntimeException("appid 不能为null！");
        }
        if (secret == null || secret.trim() == "") {
            throw new RuntimeException("secret 不能为null！");
        }
        if (type == null || type.trim() == "") {
            throw new RuntimeException("type 不能为null, 需指定 { mp, miniprogram, app } 中的一个");
        }
        WxConstants.AppType.checkType(type);
    }

}
