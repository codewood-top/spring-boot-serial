package top.codewood.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
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
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import top.codewood.config.security.oauth2.provider.sms.DefaultSmsCodeService;
import top.codewood.config.security.oauth2.provider.sms.SmsAuthenticationProvider;
import top.codewood.config.security.oauth2.provider.sms.SmsCodeService;
import top.codewood.config.security.oauth2.provider.sms.SmsTokenGranter;
import top.codewood.config.security.userdetails.CustomUserDetailsService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Configuration
public class SecurityConfig {

    static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    private ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
    }

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
                    .authorizedGrantTypes("authorization_code", "password", "refresh_token", "sms")
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

            // sms
            tokenGranters.add(new SmsTokenGranter(
                    authenticationManager(),
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()
            ));

            endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));

        }

    }

    @Bean
    public SmsCodeService smsCodeService() {
        return new DefaultSmsCodeService();
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        // password
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        // sms
        SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
        smsAuthenticationProvider.setHideUserNotFoundExceptions(false);
        smsAuthenticationProvider.setUserDetailsService(userDetailsService());
        smsAuthenticationProvider.setSmsCodeService(smsCodeService());

        ProviderManager providerManager = new ProviderManager(Arrays.asList(authenticationProvider, smsAuthenticationProvider));
        return providerManager;
    }


    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsServiceImpl();
    }

    public  class CustomUserDetailsServiceImpl extends CustomUserDetailsService {

        @Override
        public UserDetails loadUserByPhone(String phone) {
            return new User(phone, passwordEncoder().encode("123456"), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
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
