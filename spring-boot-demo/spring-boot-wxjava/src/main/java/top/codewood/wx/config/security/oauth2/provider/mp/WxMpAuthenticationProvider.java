package top.codewood.wx.config.security.oauth2.provider.mp;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.config.property.WxAppProperty;
import top.codewood.wx.config.security.userdetails.CustomUserDetailsService;
import top.codewood.wx.mp.bean.oauth2.WxOAuth2AccessToken;
import top.codewood.wx.mp.bean.oauth2.WxOAuth2UserInfo;
import top.codewood.wx.service.WxMpOAuth2Api;

public class WxMpAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    private WxAppProperties wxAppProperties;

    private CustomUserDetailsService customUserDetailsService;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public WxMpAuthenticationProvider(WxAppProperties wxAppProperties) {
        this.wxAppProperties = wxAppProperties;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String appid = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();

        WxAppProperty wxAppProperty = wxAppProperties.getAppProperty(appid);
        if (wxAppProperty == null) throw new AuthenticationServiceException("未配置，appid: " + appid);

        WxOAuth2AccessToken oAuth2AccessToken = WxMpOAuth2Api.getInstance().getOAuth2AccessToken(wxAppProperty.getAppid(), wxAppProperty.getSecret(), code);

        String openid = oAuth2AccessToken.getOpenid();
        WxOAuth2UserInfo wxOAuth2UserInfo = null;
        if (WxConstants.MpAuthorizeScope.USER_INFO.equals(oAuth2AccessToken.getScope())) {
            wxOAuth2UserInfo = WxMpOAuth2Api.getInstance().getUserInfo(oAuth2AccessToken.getAccessToken(), openid);
        }

        UserDetails user = customUserDetailsService.loadUserByWxOpenid(openid, wxOAuth2UserInfo);

        check(user);

        WxMpAuthenticationToken token = new WxMpAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMpAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setCustomUserDetailsService(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
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
