package top.codewood.config.security.oauth2.provider.mnp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import top.codewood.config.property.WxAppProperties;
import top.codewood.config.property.WxAppProperty;
import top.codewood.config.security.userdetails.CustomUserDetailsService;
import top.codewood.entity.vo.user.WxUserInfo;
import top.codewood.wx.mnp.api.WxMnpApi;
import top.codewood.wx.mnp.api.WxMnpUserApi;
import top.codewood.wx.mnp.bean.result.WxMnpCode2SessionResult;
import top.codewood.wx.mnp.bean.user.WxMnpUserInfo;

public class WxMnpAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    static final Logger LOGGER = LoggerFactory.getLogger(WxMnpAuthenticationProvider.class);

    private WxAppProperties wxAppProperties;

    private CustomUserDetailsService userDetailsService;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public WxMnpAuthenticationProvider(WxAppProperties wxAppProperties) {
        this.wxAppProperties = wxAppProperties;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxMnpAuthenticationToken mnpAuthenticationToken = (WxMnpAuthenticationToken) authentication;
        String appid = (String) mnpAuthenticationToken.getPrincipal();
        String code = (String) mnpAuthenticationToken.getCredentials();

        WxAppProperty wxAppProperty = wxAppProperties.getAppProperty(appid);
        if (wxAppProperty == null) throw new AuthenticationServiceException("未配置appid: " + appid);

        WxMnpCode2SessionResult code2SessionResult = WxMnpApi.getInstance().code2Session(wxAppProperty.getAppid(), wxAppProperty.getSecret(), code);
        LOGGER.debug("code2 session result: {}", code2SessionResult);

        WxUserInfo.Builder wxUserInfoBuilder = new WxUserInfo.Builder()
                .appid(appid)
                .openid(code2SessionResult.getOpenid())
                .unionid(code2SessionResult.getUnionId())
                .sessionKey(code2SessionResult.getSessionKey());

        WxMnpUserInfo wxMnpUserInfo = null;
        if (StringUtils.hasText(mnpAuthenticationToken.getEncryptedData()) && StringUtils.hasText(mnpAuthenticationToken.getIv())) {
            wxMnpUserInfo = WxMnpUserApi.getInstance().getUserInfo(code2SessionResult.getSessionKey(), mnpAuthenticationToken.getEncryptedData(), mnpAuthenticationToken.getIv());
            wxUserInfoBuilder.nickname(wxMnpUserInfo.getNickname());
            wxUserInfoBuilder.gender(wxMnpUserInfo.getGender());
            wxUserInfoBuilder.province(wxMnpUserInfo.getProvince());
            wxUserInfoBuilder.city(wxMnpUserInfo.getCity());
            wxUserInfoBuilder.avatarUrl(wxMnpUserInfo.getAvatarUrl());
        }

        UserDetails user = userDetailsService.loadUserByWxInfo(wxUserInfoBuilder.build());
        check(user);

        WxMnpAuthenticationToken token = new WxMnpAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMnpAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {

            throw new LockedException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }
        if (!user.isEnabled()) {

            throw new DisabledException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }
}
