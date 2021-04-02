package top.codewood.config.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.codewood.config.rabbitmq.bean.UserAccountTopic;

@Component
public class UserAccountProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(UserAccountProcessor.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = UserAccountTopic.NAME, durable = "true"),
            exchange = @Exchange(value = UserAccountTopic.NAME, type = ExchangeTypes.TOPIC),
            key = "#"

    ))
    public void process(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey(),
                msg = new String(message.getBody());
        LOGGER.debug("routingKey: {}, msg: {}", routingKey, msg);
    }

}