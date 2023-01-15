package top.codewood.wx.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.annotation.PostConstruct;
import java.net.URL;

@Configuration
@Profile("local")
public class WebMvcConfig implements WebMvcConfigurer {

    final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    private ApplicationContext applicationContext;
    private SpringTemplateEngine springTemplateEngine;

    public WebMvcConfig(ApplicationContext applicationContext, SpringTemplateEngine springTemplateEngine) {
        this.applicationContext = applicationContext;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.debug("start update static path");
        String path = WebMvcConfig.class.getClassLoader().getResource("").getPath();
        path = path.replaceAll("/target/classes/", "/src/main/resources/static/");
        String cssPath = "file:" + path + "css/";
        String jsPath = "file:" + path + "js/";
        registry.addResourceHandler("/css/**").addResourceLocations(cssPath);
        registry.addResourceHandler("/js/**").addResourceLocations(jsPath);
    }

    @PostConstruct
    public void postConstruct() {
        springTemplateEngine.setTemplateResolver(templateResolver());
    }

    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver resourceTemplateResolver = new SpringResourceTemplateResolver();
        resourceTemplateResolver.setApplicationContext(applicationContext);

        URL resource = this.getClass().getClassLoader().getResource("templates/");
        String devResource = resource.getFile().replaceAll("target/classes", "src/main/resources");
        resourceTemplateResolver.setPrefix("file:" + devResource);
        resourceTemplateResolver.setCacheable(false);
        resourceTemplateResolver.setSuffix(".html");
        resourceTemplateResolver.setTemplateMode(TemplateMode.HTML);
        return resourceTemplateResolver;
    }

}
