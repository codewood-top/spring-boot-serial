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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.codewood.config.security.exception.AppOAuth2Exception;
import top.codewood.config.security.filter.ClientDetailsAuthenticationFilter;
import top.codewood.http.vo.ResultEntity;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
                    .authorizedGrantTypes("authorization_code", "password", "refresh_token")
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

            endpoints.exceptionTranslator(new WebResponseExceptionTranslator<OAuth2Exception>() {
                @Override
                public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                    LOGGER.error("endpoint err: {}", e.getMessage());
                    return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(new AppOAuth2Exception(e.getMessage()));
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
            security.addTokenEndpointAuthenticationFilter(new ClientDetailsAuthenticationFilter(clientDetailsService, passwordEncoder()));

        }

        @PostConstruct
        public void postConstruct() {
            LOGGER.info("AuthorizationServerConfig postConstruct");
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

    @EnableResourceServer
    public class ResourceSecurityConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
            http.authorizeRequests()
                    .antMatchers("/rest/hello", "/rest/logininfo")
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
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        ProviderManager providerManager = new ProviderManager(Arrays.asList(authenticationProvider));
        return providerManager;
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(User
                .withUsername("test")
                .password(passwordEncoder().encode("testpass"))
                .roles("USER")
                .build()
        );
    }

}
