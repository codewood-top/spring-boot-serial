package top.codewood.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.codewood.rabbitmq.config.bean.OrderTopic;

@Component
public class OrderProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessor.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = OrderTopic.NAME, durable = "true"),
            exchange = @Exchange(value = OrderTopic.NAME, type = ExchangeTypes.TOPIC),
            key = "#"

    ))
    public void process(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey(),
                msg = new String(message.getBody());

        int times = message.getMessageProperties().getHeader("times");

        LOGGER.debug("routingKey: {}, msg: {}, times: {}", routingKey, msg, times);


    }


}
