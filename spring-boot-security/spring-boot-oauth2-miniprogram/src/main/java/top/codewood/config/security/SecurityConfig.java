package top.codewood.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.codewood.config.property.WxAppProperties;
import top.codewood.config.security.exception.AppOAuth2Exception;
import top.codewood.config.security.oauth2.provider.mnp.WxMnpAuthenticationProvider;
import top.codewood.config.security.oauth2.provider.mnp.WxMnpTokenGranter;
import top.codewood.config.security.userdetails.CustomUserDetailsService;
import top.codewood.entity.User;
import top.codewood.entity.vo.user.WxUserInfo;
import top.codewood.http.vo.ResultEntity;
import top.codewood.service.UserService;

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

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("first-client")
                    .secret(passwordEncoder().encode("first-secret"))
                    .scopes("all")
                    .authorizedGrantTypes("password", "refresh_token", WxMnpTokenGranter.GRANT_TYPE)
                    .accessTokenValiditySeconds(20)
                    .refreshTokenValiditySeconds(60 * 1);
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
                    AppOAuth2Exception appOAuth2Exception = null;
                    if (e instanceof UsernameNotFoundException) {
                        appOAuth2Exception = new AppOAuth2Exception("用户不存在");
                        appOAuth2Exception.setCode(401404);
                    } else if(e.getMessage().startsWith("Invalid refresh token")) {
                        appOAuth2Exception = new AppOAuth2Exception("invalid token");
                        appOAuth2Exception.setCode(401400);
                    } else {
                        appOAuth2Exception = new AppOAuth2Exception(e.getMessage());
                    }
                    return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(appOAuth2Exception);
                }
            });

        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.authenticationEntryPoint(new AuthenticationEntryPoint() {
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                    LOGGER.error("AuthorizationServerConfig entry err: {}", authException.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(objectMapper.writeValueAsString(ResultEntity.failure(HttpServletResponse.SC_UNAUTHORIZED, "未授权应用！")));
                    response.flushBuffer();
                }
            });
            security.accessDeniedHandler(new AccessDeniedHandler() {
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                    LOGGER.error("AuthorizationServerConfig access denied, err: {}", accessDeniedException.getMessage());
                }
            });
        }

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

        // wx_mnp_jscode
        tokenGranters.add(new WxMnpTokenGranter(
                authenticationManager(),
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()
        ));

        endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));

    }

    @Autowired
    private WxAppProperties wxAppProperties;

    @Bean
    public AuthenticationManager authenticationManager() {

        // password
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        WxMnpAuthenticationProvider wxMnpAuthenticationProvider = new WxMnpAuthenticationProvider(wxAppProperties);
        wxMnpAuthenticationProvider.setUserDetailsService(userDetailsService());

        ProviderManager providerManager = new ProviderManager(Arrays.asList(authenticationProvider, wxMnpAuthenticationProvider));
        return providerManager;
    }

    @Autowired
    private UserService userService;

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService() {
            @Override
            public UserDetails loadUserByWxInfo(WxUserInfo wxUserInfo) {
                wxUserInfo.setDefaultPassword(passwordEncoder().encode("123456"));
                User user = userService.getOrCreate(wxUserInfo);
                return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = Optional.ofNullable(userService.get(username)).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
                return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            }
        };
    }

    @EnableResourceServer
    public class ResourceSecurityConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
            http.authorizeRequests()
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
                                map.put("code", 401405);
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
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(objectMapper.writeValueAsString(map));
                    response.flushBuffer();
                }
            });

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
