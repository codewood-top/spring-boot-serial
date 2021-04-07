package top.codewood.wx.config.security.oauth2.provider.mnp;

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
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.config.property.WxAppProperty;
import top.codewood.wx.config.security.userdetails.CustomUserDetailsService;
import top.codewood.wx.mnp.api.WxMnpApi;
import top.codewood.wx.mnp.bean.result.WxMnpCode2SessionResult;

public class WxMnpAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    static final Logger LOGGER = LoggerFactory.getLogger(WxMnpAuthenticationProvider.class);

    private WxAppProperties wxAppProperties;

    private CustomUserDetailsService customUserDetailsService;

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
        String appid = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();

        WxAppProperty wxAppProperty = wxAppProperties.getAppProperty(appid);
        if (wxAppProperty == null) throw new AuthenticationServiceException("未配置，appid: " + appid);

        WxMnpCode2SessionResult code2SessionResult = WxMnpApi.getInstance().code2Session(wxAppProperty.getAppid(), wxAppProperty.getSecret(), code);

        LOGGER.debug("code2 session result: {}", code2SessionResult);

        UserDetails user = customUserDetailsService.loadUserByWxOpenid(code2SessionResult.getOpenid(), code2SessionResult);
        check(user);

        WxMnpAuthenticationToken token = new WxMnpAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMnpAuthenticationToken.class.isAssignableFrom(authentication);
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
