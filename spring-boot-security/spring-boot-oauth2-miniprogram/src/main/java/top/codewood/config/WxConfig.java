package top.codewood.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.codewood.config.property.WxAppProperties;

@Configuration
@EnableConfigurationProperties({WxAppProperties.class})
public class WxConfig {
}
