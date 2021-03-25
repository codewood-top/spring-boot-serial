package top.codewood.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.codewood.config.security.jwt.authentication.JwtAccessDeniedHandler;
import top.codewood.config.security.jwt.authentication.JwtAuthenticationEntryPoint;
import top.codewood.config.security.jwt.filter.JwtAuthenticationFilter;
import top.codewood.config.security.jwt.token.JwtTokenStore;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    static final String JWT_SECRET = "c4c205097dfe4ac9a44f4f7a7fe11c1e757112e77de84eb89f8a8ace52595144";


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/rest/authorize/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenStore()), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(JWT_SECRET);
    }

}
