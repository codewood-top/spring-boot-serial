package top.codewood.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
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
                    .accessTokenValiditySeconds(600)
                    .refreshTokenValiditySeconds(7200);
            clientDetailsService = clientDetailsServiceBuilder.build();
            clients.withClientDetails(clientDetailsService);

        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager())
                    .userDetailsService(userDetailsService());

            setTokenGranters(endpoints);

            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            enhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
            endpoints.accessTokenConverter(jwtAccessTokenConverter())
                    .tokenEnhancer(enhancerChain)
                    .reuseRefreshTokens(false);

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

    @EnableResourceServer
    public class ResourceSecurityConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
            http.authorizeRequests()
                    .antMatchers("/sms/code")
                    .permitAll()
                    .anyRequest()
                    .authenticated();
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.accessDeniedHandler(new AccessDeniedHandler() {
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                    LOGGER.error("access denied, err: {}", accessDeniedException.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
                }
            });
            resources.authenticationEntryPoint(new AuthenticationEntryPoint() {
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", HttpServletResponse.SC_FORBIDDEN);
                    if (authException instanceof InsufficientAuthenticationException) {
                        if (authException.getCause() instanceof InvalidTokenException) {
                            if (authException.getMessage().startsWith("Access token expired")) {
                                LOGGER.error("token expired.");
                                map.put("message", "token已失效");
                            } else {
                                LOGGER.error("invalid token, err: {}", authException.getMessage());
                                map.put("message", "无效token");
                            }
                        } else {
                            LOGGER.error("authentication entry point err: {}", authException.getMessage());
                            map.put("message", "登录后访问");
                        }
                    }
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(objectMapper.writeValueAsString(map));
                    response.flushBuffer();
                }
            });
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
            return buildUser(phone);
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return buildUser(username);
        }

        private UserDetails buildUser(String username) {
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
        converter.setAccessTokenConverter(accessTokenConverter());
        converter.setSigningKey("jwtSignKey");
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new TokenEnhancer() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                final Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("user_id", 1L);
                ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);
                return accessToken;
            }
        };
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        return new DefaultAccessTokenConverter() {
            @Override
            public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
                OAuth2Authentication authentication = super.extractAuthentication(claims);
                authentication.setDetails(claims);
                return authentication;
            }
        };
    }

}
