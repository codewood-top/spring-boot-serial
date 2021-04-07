package top.codewood.wx.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.config.property.WxAppProperty;
import top.codewood.wx.mnp.bean.result.WxMnpCode2SessionResult;
import top.codewood.wx.mnp.bean.user.WxMnpUserInfo;
import top.codewood.wx.service.OrderService;
import top.codewood.wx.service.WxMnpService;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/wx/mnp/rest")
@RestController
public class WxMnpRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(WxMnpRestController.class);

    @Autowired
    private WxAppProperties wxAppProperties;

    @Autowired
    private WxMnpService wxMnpService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/access_token")
    public String getAccessToken() {
        return wxMnpService.getAccessToken();
    }

    @RequestMapping("/code2session")
    public Map code2Session(@RequestParam("code") String code) {
        WxMnpCode2SessionResult result = wxMnpService.code2Session(code);
        LOGGER.debug("code2Session result: {}", result);
        Map<String, String> map = new HashMap<>();
        map.put("openid", result.getOpenid());
        map.put("unionId", result.getUnionId());
        return map;
    }

    @RequestMapping("/getuserinfo")
    public WxMnpUserInfo getUserInfo(
            @RequestParam("openid") String openid,
            @RequestParam("encryptedData") String encryptedData,
            @RequestParam("iv") String iv) {
        WxMnpUserInfo wxMnpUserInfo = wxMnpService.getUserInfo(openid, encryptedData, iv);
        LOGGER.debug("userInfo: {}", wxMnpUserInfo);
        return wxMnpUserInfo;
    }

    @RequestMapping("/getdefaultorderpayinfo")
    public Map getDefaultOrderPayInfo() {

        WxAppProperty wxAppProperty = wxAppProperties.getAppPropertyByType(WxConstants.AppType.MINIPROGRAM);
        String orderNumber = orderService.generateOrderNumber();
        Map<String, String> map = new HashMap<>();
        map.put("orderNumber", orderNumber);
        map.put("appid", wxAppProperty.getAppid());
        map.put("mchid", wxAppProperties.getPay().getMchid());
        return map;
    }

}
