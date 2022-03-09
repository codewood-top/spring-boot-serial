package top.codewood.wx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.config.property.WxAppProperty;
import top.codewood.wx.mp.bean.oauth2.WxOAuth2AccessToken;
import top.codewood.wx.mp.bean.oauth2.WxOAuth2UserInfo;
import top.codewood.wx.service.WxMpOAuth2Api;
import top.codewood.wx.util.Strings;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/wx/mp")
public class WxMpController {

    final Logger logger = LoggerFactory.getLogger(WxMpController.class);

    private WxAppProperties wxAppProperties;

    public WxMpController(WxAppProperties wxAppProperties) {
        this.wxAppProperties = wxAppProperties;
    }

    @GetMapping("/js_sdk")
    public String jsSdk(@RequestParam(value = "debug", required = false, defaultValue = "false") boolean debug, Model model) {
        model.addAttribute("debug", debug);
        model.addAttribute("appid", wxAppProperties.getAppPropertyByType(WxConstants.AppType.MP).getAppid());
        return "mp/js_sdk";
    }

    /**
     *
     * @param scope base / userinfo
     * @param code
     * @param state
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/authorize/{scope}")
    public String authorize(
            @PathVariable("scope") String scope,
            @RequestParam(required = false, name = "code") String code,
            @RequestParam(required = false, name = "state", defaultValue = "") String state,
            Model model,
            HttpServletRequest request) {
        String scopeVal = null;
        if ("base".equals(scope)) {
            scopeVal = WxConstants.MpAuthorizeScope.BASE;
        } else if ("userinfo".equals(scope)) {
            scopeVal = WxConstants.MpAuthorizeScope.USER_INFO;
        } else {
            throw new RuntimeException("invalid scope: " + scope);
        }

        WxAppProperty wxAppProperty = wxAppProperties.getAppPropertyByType(WxConstants.AppType.MP);

        String currentReqUrl = getCurrentUrl(request);
        logger.info("current req url: {}, code: {}", currentReqUrl, code);
        if (StringUtils.hasText(code)) {
            WxOAuth2AccessToken oAuth2AccessToken = WxMpOAuth2Api.getInstance().getOAuth2AccessToken(wxAppProperty.getAppid(), wxAppProperty.getSecret(), code);

            String openid = oAuth2AccessToken.getOpenid();
            WxOAuth2UserInfo wxOAuth2UserInfo = null;
            if (WxConstants.MpAuthorizeScope.USER_INFO.equals(oAuth2AccessToken.getScope())) {
                wxOAuth2UserInfo = WxMpOAuth2Api.getInstance().getUserInfo(oAuth2AccessToken.getAccessToken(), openid);
                logger.info("wx.oauth2.userinfo: {}", wxOAuth2UserInfo);
            }

            model.addAttribute("openid", openid);
            model.addAttribute("accessToken", oAuth2AccessToken.getAccessToken());
            model.addAttribute("userInfo", wxOAuth2UserInfo);

        } else {
            String redirectUrl = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    wxAppProperty.getAppid(),
                    Strings.urlEncode(currentReqUrl),
                    scopeVal,
                    state
            );
            return "redirect:" + redirectUrl;
        }
        model.addAttribute("scope", scope);
        model.addAttribute("appid", wxAppProperty.getAppid());
        return "mp/authorize";
    }

    @RequestMapping("/authorize_code/{scope}")
    public String authorize_code(@PathVariable("scope") String scope,
                                 @RequestParam(required = false, name = "code") String code,
                                 @RequestParam(required = false, name = "state", defaultValue = "") String state,
                                 Model model,
                                 HttpServletRequest request) {

        String scopeVal = null;
        if ("base".equals(scope)) {
            scopeVal = WxConstants.MpAuthorizeScope.BASE;
        } else if ("userinfo".equals(scope)) {
            scopeVal = WxConstants.MpAuthorizeScope.USER_INFO;
        } else {
            throw new RuntimeException("invalid scope: " + scope);
        }

        WxAppProperty wxAppProperty = wxAppProperties.getAppPropertyByType(WxConstants.AppType.MP);

        String currentReqUrl = getCurrentUrl(request);
        logger.info("current req url: {}, code: {}", currentReqUrl, code);
        if (StringUtils.hasText(code)) {
            model.addAttribute("appid", wxAppProperty.getAppid());
            model.addAttribute("code", code);
            model.addAttribute("scope", scope);
            return "mp/authorizeCode";
        } else {
            String redirectUrl = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    wxAppProperty.getAppid(),
                    Strings.urlEncode(currentReqUrl),
                    scopeVal,
                    state
            );
            return "redirect:" + redirectUrl;
        }
    }

    private String getCurrentUrl(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme())
                .append("://");

        String host = request.getHeader("Host");
        if (StringUtils.hasText(host)) {
            sb.append(host);
        } else {
            sb.append(request.getServerName());
        }

        sb.append(request.getServletPath());

        if (StringUtils.hasText(request.getQueryString())) {
            sb.append("?").append(request.getQueryString());
        }
        return sb.toString();
    }

}
