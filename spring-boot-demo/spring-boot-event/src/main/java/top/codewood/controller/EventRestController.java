package top.codewood.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.config.event.CustomEvent;
import top.codewood.config.event.TestEvent;

@RestController
@RequestMapping("/event/rest")
public class EventRestController implements ApplicationContextAware {

    private ApplicationContext context;

    @RequestMapping("send")
    public String send(@RequestParam(value = "type", defaultValue = "custom", required = false) String type,
                       @RequestParam("msg") String msg) {

        if ("custom".equals(type)) {
            context.publishEvent(new CustomEvent(this, msg));
        } else if ("test".equals(type)){
            context.publishEvent(new TestEvent(this, msg));
        }

        return "ok";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
