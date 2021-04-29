package top.codewood.config.security.authentication.oauthcode;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import top.codewood.config.security.userdetails.CustomUserDetailsService;

public class OAuthCodeAuthenticationProvider implements AuthenticationProvider {

    private CustomUserDetailsService customUserDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        OAuthCodeAuthenticationToken token = (OAuthCodeAuthenticationToken) authentication;
        UserDetails user = customUserDetailsService.loadUserByOAuthCode((String) token.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        OAuthCodeAuthenticationToken oauthToken = new OAuthCodeAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        oauthToken.setDetails(token.getDetails());
        return oauthToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setCustomUserDetailsService(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }
}
