package top.codewood.config.security.oauth2.provider.mnp;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class WxMnpTokenGranter extends AbstractTokenGranter {

    public static final String GRANT_TYPE = "wx_mnp_jscode";

    private final AuthenticationManager authenticationManager;

    public WxMnpTokenGranter(AuthenticationManager authenticationManager,AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());

        String appid = parameters.get("appid");
        String code = parameters.get("code");
        String encryptedData = parameters.get("encryptedData");
        String iv = parameters.get("iv");

        Authentication authentication = new WxMnpAuthenticationToken(appid, code);
        ((WxMnpAuthenticationToken)authentication).setEncryptedData(encryptedData);
        ((WxMnpAuthenticationToken)authentication).setIv(iv);

        ((AbstractAuthenticationToken)authentication).setDetails(parameters);
        authentication = authenticationManager.authenticate(authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidGrantException("Invalid code.");
        }

        OAuth2Request oAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(oAuth2Request, authentication);

    }
}
