package top.codewood.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "weixin.config.properties")
public class WxAppProperties {

    private final Map<String, WxAppProperty> appPropertyMap;

    {
        appPropertyMap = new HashMap<>();
    }

    private WxAppProperty[] apps;

    public WxAppProperty[] getApps() {
        return apps;
    }

    public void setApps(WxAppProperty[] apps) {
        this.apps = apps;
    }

    public WxAppProperty getAppProperty(String appid) {
        WxAppProperty wxAppProperty = appPropertyMap.get(appid);
        if (wxAppProperty == null) throw new RuntimeException(String.format("appid: %s 尚未配置！",appid));
        return wxAppProperty;
    }

    public WxAppProperty getAppPropertyByType(String type) {
        for (WxAppProperty app : apps) {
            if (type.equals(app.getType())) return app;
        }
        throw new RuntimeException(String.format("未配置类型 %s 的参数！", type));
    }

    @PostConstruct
    public void postConstruct() {
        if (apps != null) {
            for (WxAppProperty app : apps) {
                app.check();
                if (appPropertyMap.containsKey(app.getAppid())) {
                    throw new RuntimeException(String.format("appid: %s 有重复配置！", app.getAppid()));
                }
                appPropertyMap.put(app.getAppid(), app);
            }
        }
    }


}
