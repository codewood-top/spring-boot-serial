package top.codewood.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.codewood.rabbitmq.service.RabbitService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class DelayProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DelayProcessor.class);

    private static final String DELAY_NAME = "delay";

    private RabbitService rabbitService;
    public DelayProcessor(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @Bean
    public Queue delayQueue() {
        return new Queue(DELAY_NAME, true, false, false);
    }

    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_NAME).noargs();
    }

    @RabbitListener(queues = DELAY_NAME)
    public void process(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String msgBody = new String(message.getBody());

        try {
            Integer times = message.getMessageProperties().getHeader("times");
            if (times == null) {
                times = 0;
            }

            String status = null;

            if (times < 5) {
                status = "resend";
                resend(msgBody, times + 1);
            } else {
                status = "done";
                rabbitService.send("order", "order.delay", msgBody, Collections.singletonMap("times", times));
            }
            logger.info("routingKey: {}, msgBody: {}, times: {}, status: {}", routingKey, msgBody, times, status);

        } catch (Exception e) {
            logger.error("err: {}", e.getMessage());
        }

    }

    private void resend(String data, int times) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("times", times);
        rabbitService.sendDelay(DELAY_NAME, DELAY_NAME, data, 1 << times, headers);
    }

}
