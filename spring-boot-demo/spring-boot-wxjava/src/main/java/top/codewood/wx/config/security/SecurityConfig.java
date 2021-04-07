package top.codewood.wx.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.config.security.oauth2.provider.mnp.WxMnpAuthenticationProvider;
import top.codewood.wx.config.security.oauth2.provider.mnp.WxMnpTokenGranter;
import top.codewood.wx.config.security.oauth2.provider.mp.WxMpAuthenticationProvider;
import top.codewood.wx.config.security.oauth2.provider.mp.WxMpTokenGranter;
import top.codewood.wx.config.security.userdetails.CustomUserDetailsService;
import top.codewood.wx.mnp.bean.result.WxMnpCode2SessionResult;
import top.codewood.wx.mp.bean.oauth2.WxOAuth2UserInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private WxAppProperties wxAppProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @EnableAuthorizationServer
    public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        ClientDetailsService clientDetailsService = null;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            ClientDetailsServiceBuilder clientDetailsServiceBuilder = new InMemoryClientDetailsServiceBuilder();
            clientDetailsServiceBuilder.withClient("first-client")
                    .secret(passwordEncoder().encode("first-secret"))
                    .scopes("all")
                    .authorizedGrantTypes("authorization_code", "password", "refresh_token", WxMpTokenGranter.GRANT_TYPE, WxMnpTokenGranter.GRANT_TYPE)
                    .accessTokenValiditySeconds(120)
                    .refreshTokenValiditySeconds(600);
            clientDetailsService = clientDetailsServiceBuilder.build();
            clients.withClientDetails(clientDetailsService);

        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager())
                    .userDetailsService(userDetailsService());
            endpoints.accessTokenConverter(jwtAccessTokenConverter())
                    .reuseRefreshTokens(false);
            setTokenGranters(endpoints);

            endpoints.exceptionTranslator(new WebResponseExceptionTranslator<OAuth2Exception>() {
                @Override
                public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                    LOGGER.error("endpoint err: {}", e.getMessage());
                    return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(new OAuth2Exception(e.getMessage()));
                }
            });

        }

        private void setTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {

            List<TokenGranter> tokenGranters = new ArrayList<>();

            // password
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(
                    authenticationManager(),
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()
            ));

            // refresh_token
            tokenGranters.add(new RefreshTokenGranter(
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()
            ));

            // wx_mp_code
            tokenGranters.add(new WxMpTokenGranter(
                    authenticationManager(),
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()
            ));

            // wx_mnp_jscode
            tokenGranters.add(new WxMnpTokenGranter(
                    authenticationManager(),
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()
            ));

            endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));

        }

    }

    @Bean
    public AuthenticationManager authenticationManager() {

        // password
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());


        // wx_mp_code
        WxMpAuthenticationProvider wxMpAuthenticationProvider = new WxMpAuthenticationProvider(wxAppProperties);
        wxMpAuthenticationProvider.setCustomUserDetailsService(userDetailsService());

        // wx_mnp_jscode
        WxMnpAuthenticationProvider wxMnpAuthenticationProvider = new WxMnpAuthenticationProvider(wxAppProperties);
        wxMnpAuthenticationProvider.setCustomUserDetailsService(userDetailsService());

        ProviderManager providerManager = new ProviderManager(Arrays.asList(authenticationProvider, wxMpAuthenticationProvider, wxMnpAuthenticationProvider));
        return providerManager;
    }


    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsServiceImpl();
    }

    public  class CustomUserDetailsServiceImpl extends CustomUserDetailsService {

        @Override
        public UserDetails loadUserByWxOpenid(String openid, Object data) {
            String userPrefix = "wx_";
            if (data != null) {
                if (data instanceof WxOAuth2UserInfo) {
                    userPrefix += "mp_";
                }
                if (data instanceof WxMnpCode2SessionResult) {
                    userPrefix += "mnp_";
                }
            }
            return new User(userPrefix + openid, passwordEncoder().encode("123456"), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return new User(username, passwordEncoder().encode("123456"), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        }
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("jwtSignKey");
        return converter;
    }


}
