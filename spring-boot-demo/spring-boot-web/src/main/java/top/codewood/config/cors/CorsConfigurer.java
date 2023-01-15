package top.codewood.config.cors;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class CorsConfigurer {

    public CorsConfiguration buildConfig() {

        CorsConfiguration corsConfig = new CorsConfiguration();

        // 允许凭证
        corsConfig.setAllowCredentials(true);
        // 允许任何请求来源
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 允许任何请求头
        corsConfig.addAllowedHeader(CorsConfiguration.ALL);
        // 允许任何方法
        corsConfig.addAllowedMethod(CorsConfiguration.ALL);

        corsConfig.setMaxAge(3600L);

        return corsConfig;
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CorsFilter(source));

        // 这里是把filter往前放，避免options的请求被cors
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }

}
