package top.codewood.config.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomEventListener {

    static final Logger LOGGER = LoggerFactory.getLogger(CustomEventListener.class);

    @EventListener(TestEvent.class)
    public void listenTestEvent(TestEvent testEvent) {
        LOGGER.info("test event msg: {}", testEvent.getMsg());
    }

    @EventListener(CustomEvent.class)
    public void listenCustomEvent(CustomEvent customEvent) {
        LOGGER.info("custom event msg: {}", customEvent.getMsg());
    }

}
