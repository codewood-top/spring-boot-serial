package top.codewood.wx.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.common.bean.WxAccessToken;
import top.codewood.wx.common.bean.error.WxErrorException;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.config.property.WxAppProperty;
import top.codewood.wx.mp.bean.WxMpJsapiTicket;
import top.codewood.wx.mp.bean.menu.WxMenu;
import top.codewood.wx.service.WxMpApi;
import top.codewood.wx.service.WxMpMenuApi;
import top.codewood.wx.service.WxMpService;
import top.codewood.wx.util.Strings;

import java.time.LocalDateTime;

@Service("wxMpService")
public class WxMpServiceImpl implements WxMpService {

    static final Logger LOGGER = LoggerFactory.getLogger(WxMpServiceImpl.class);

    private WxAccessToken2 wxAccessToken = null;
    private WxJsapiTicket2 wxJsapiTicket = null;

    @Autowired
    private WxAppProperties wxAppProperties;

    @Override
    public String getAccessToken() {
        if (wxAccessToken == null || wxAccessToken.expiredTime.isBefore(LocalDateTime.now())) {
            synchronized (this) {
                if (wxAccessToken != null && wxAccessToken.expiredTime.isAfter(LocalDateTime.now())) {
                    return wxAccessToken.accessToken;
                }
                updateAccessToken();
            }
        }
        return wxAccessToken != null ? wxAccessToken.accessToken : Strings.EMPTY;
    }

    @Override
    public void createMenu(WxMenu wxMenu) {
        Assert.notNull(wxMenu, "菜单不能为空");
        String respStr = WxMpMenuApi.getInstance().create(getAccessToken(), wxMenu);
        LOGGER.debug("resp: {}", respStr);

    }

    @Override
    public WxMenu queryMenu() {
        return WxMpMenuApi.getInstance().query(getAccessToken());
    }

    @Override
    public String jsSignature(String nonceStr, long timestamp, String url) {
        return DigestUtils.sha1Hex(String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s", getJsapiTicket(getAccessToken()),
                nonceStr, timestamp, url));
    }

    private String getJsapiTicket(String accessToken) {
        if (wxJsapiTicket == null || wxJsapiTicket.expiredTime.isBefore(LocalDateTime.now())) {
            synchronized (this) {
                if (wxJsapiTicket != null && wxJsapiTicket.expiredTime.isAfter(LocalDateTime.now())) {
                    return wxJsapiTicket.ticket;
                }
                WxMpJsapiTicket jsapiTicket = WxMpApi.getJsapiTicket(accessToken);
                LOGGER.debug("正在更新jsapi_ticket: {}", jsapiTicket);
                WxJsapiTicket2 wxJsapiTicket2 = new WxJsapiTicket2();
                wxJsapiTicket2.ticket = jsapiTicket.getTicket();
                wxJsapiTicket2.expiresIn = jsapiTicket.getExpiresIn();
                wxJsapiTicket2.expiredTime = LocalDateTime.now().plusSeconds(wxJsapiTicket2.expiresIn - 200);
                wxJsapiTicket = wxJsapiTicket2;
            }
        }
        return wxJsapiTicket != null ? wxJsapiTicket.ticket : Strings.EMPTY;
    }

    private void updateAccessToken() {
        WxAppProperty wxAppProperty = wxAppProperties.getAppPropertyByType(WxConstants.AppType.MP);
        LOGGER.debug("正在请求更新 access_token");
        try {
            WxAccessToken accessToken = WxMpApi.getAccessToken(wxAppProperty.getAppid(), wxAppProperty.getSecret());
            WxAccessToken2 wxAccessToken2 = new WxAccessToken2();
            wxAccessToken2.accessToken = accessToken.getAccessToken();
            wxAccessToken2.expiresIn = accessToken.getExpiresIn();
            wxAccessToken2.expiredTime = LocalDateTime.now().plusSeconds(wxAccessToken2.expiresIn - 200);
            wxAccessToken = wxAccessToken2;
        } catch (WxErrorException e) {
            LOGGER.error("请求更新 access_token 失败：{}", e.getMessage());
        }

    }

    class WxAccessToken2 {

        String accessToken;
        Integer expiresIn;
        LocalDateTime expiredTime;

    }

    class WxJsapiTicket2 {
        String ticket;
        Integer expiresIn;
        LocalDateTime expiredTime;
    }

}
