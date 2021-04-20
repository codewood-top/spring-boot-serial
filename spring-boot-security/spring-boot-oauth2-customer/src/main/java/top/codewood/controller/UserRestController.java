package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private TokenStore tokenStore;

    @RequestMapping("/token_addition_info")
    public String additionInfo() {
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("authentication: {}, addition: {}", authentication, getExtraInfoByAccessToken(authentication));
        return "ok";
    }

    @RequestMapping("/token_details")
    public String tokenDetails() {
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("authentication: {}, addition: {}", authentication, getExtraInfo(authentication));
        return "ok";
    }

    private Map<String, Object> getExtraInfoByAccessToken(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation();
    }
    private Map<String, Object> getExtraInfo(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        return (Map<String, Object>) details.getDecodedDetails();
    }

}
