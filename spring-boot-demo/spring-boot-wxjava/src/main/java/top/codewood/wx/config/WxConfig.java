package top.codewood.wx.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.codewood.wx.config.property.WxAppProperties;

@Configuration
@EnableConfigurationProperties({WxAppProperties.class})
public class WxConfig {
}
