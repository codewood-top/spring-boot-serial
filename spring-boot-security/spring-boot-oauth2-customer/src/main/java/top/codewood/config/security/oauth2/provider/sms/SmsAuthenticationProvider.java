package top.codewood.config.security.oauth2.provider.sms;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import top.codewood.config.security.userdetails.CustomUserDetailsService;

public class SmsAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    static final Logger LOGGER = LoggerFactory.getLogger(SmsAuthenticationProvider.class);

    private CustomUserDetailsService userDetailsService;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private SmsCodeService smsCodeService;

    /**
     * 如果隐藏UserNotfoundException, 则抛 BadCredentialException
     */
    private boolean hideUserNotFoundExceptions = true;


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String phone = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();

        // 这里要验证验证码
        smsCodeService().consumeCode(phone, code);

        UserDetails user = null;
        // TODO 这里要做异常捕捉
        try {
            user = userDetailsService.loadUserByPhone(phone);
        } catch (UsernameNotFoundException e) {
            if (this.hideUserNotFoundExceptions) {
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            throw e;
        }
        check(user);
        SmsAuthenticationToken token = new SmsAuthenticationToken(phone, code, user.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 监测是否有效的验证码
     * @param phone
     * @param code
     */
    private void check(String phone, String code) {
        LOGGER.debug("phone: {}, code: {}", phone, code);
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

    public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    public SmsCodeService smsCodeService() {
        if (this.smsCodeService == null) {
            this.smsCodeService = new DefaultSmsCodeService();
        }
        return this.smsCodeService;
    }

    public void setSmsCodeService(SmsCodeService smsCodeService) {
        this.smsCodeService = smsCodeService;
    }

}
