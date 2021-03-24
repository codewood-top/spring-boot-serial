package top.codewood.web.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.codewood.web.config.security.authentication.WebAuthenticationFailureHandler;
import top.codewood.web.config.security.authentication.WebAuthenticationSuccessHandler;
import top.codewood.web.config.security.userdetails.UserDetailsServiceImpl;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

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
                .mvcMatchers("/", "/login")
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/***");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(passwordEncoder());
    }

    @Bean
    public WebAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new WebAuthenticationSuccessHandler();
    }

    @Bean
    public WebAuthenticationFailureHandler authenticationFailureHandler() {
        return new WebAuthenticationFailureHandler();
    }

}
