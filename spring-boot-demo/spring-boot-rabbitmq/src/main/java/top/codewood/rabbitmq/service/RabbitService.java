package top.codewood.rabbitmq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitService {

    private RabbitTemplate rabbitTemplate;

    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String exchange, String routingKey, String data) {
        rabbitTemplate.convertAndSend(exchange, routingKey, data);
    }

    public void sendDelay(String exchange, String routingKey, String data, int delaySeconds) {
        sendDelay(exchange, routingKey, data, delaySeconds, null);
    }

    public void sendDelay(String exchange, String routingKey, String data, int delaySeconds, Map<String, Object> headers) {
        rabbitTemplate.convertAndSend(exchange, routingKey, data, message -> {
            message.getMessageProperties().setHeader("x-delay", delaySeconds * 1000);

            if (headers != null) {
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    message.getMessageProperties().setHeader(entry.getKey(), entry.getValue());
                }
            }

            return message;
        });
    }




}
