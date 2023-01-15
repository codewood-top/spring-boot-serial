package top.codewood.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import top.codewood.config.security.authentication.WebAuthenticationEntryPoint;
import top.codewood.config.security.authentication.WebAuthenticationFailureHandler;
import top.codewood.config.security.authentication.WebAuthenticationSuccessHandler;
import top.codewood.config.security.authentication.oauthcode.OAuthCodeAuthenticationSecurityConfig;
import top.codewood.config.security.userdetails.CustomUserDetailsService;
import top.codewood.service.OAuthService;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    static final String LOGIN_URL = "/login";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .and()
                .userDetailsService(userDetailsService())
                .authorizeRequests()
                .mvcMatchers(LOGIN_URL, "/", "/favicon.ico", "/oauth/rest/*")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
        http.apply(oAuthCodeAuthenticationSecurityConfig());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/***");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private OAuthService oAuthService;

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService() {
            @Override
            public UserDetails loadUserByOAuthCode(String code) {
                String username = oAuthService.codeUsername(code);
                return loadUserByUsername(username);
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return new User(username, passwordEncoder().encode("123456"), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            }
        };
    }

    @Bean
    public WebAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new WebAuthenticationSuccessHandler();
    }

    @Bean
    public WebAuthenticationFailureHandler authenticationFailureHandler() {
        return new WebAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new WebAuthenticationEntryPoint(LOGIN_URL);
    }

    @Bean
    public OAuthCodeAuthenticationSecurityConfig oAuthCodeAuthenticationSecurityConfig() {
        return new OAuthCodeAuthenticationSecurityConfig(userDetailsService(), authenticationSuccessHandler(), authenticationFailureHandler());
    }

}
