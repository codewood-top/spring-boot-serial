package top.codewood.config.security.oauth2.provider.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmsTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "sms";

    private final AuthenticationManager authenticationManager;

    public SmsTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());

        String phone = parameters.get("phone");
        String code = parameters.get("code");

        Authentication auth = new SmsAuthenticationToken(phone, code);
        ((AbstractAuthenticationToken)auth).setDetails(parameters);
        auth = authenticationManager.authenticate(auth);

        if (auth == null || !auth.isAuthenticated()) {
            throw new InvalidGrantException("无效的验证码！");
        }

        OAuth2Request oAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(oAuth2Request, auth);
    }
}
