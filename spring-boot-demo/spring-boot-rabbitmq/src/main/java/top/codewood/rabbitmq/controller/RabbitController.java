package top.codewood.rabbitmq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.rabbitmq.service.RabbitService;

@RestController
@RequestMapping("rabbit")
public class RabbitController {

    private static Logger logger = LoggerFactory.getLogger(RabbitController.class);

    private RabbitService rabbitService;
    public RabbitController(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @RequestMapping("send")
    public String send(@RequestParam("exchange") String exchange, @RequestParam("routingKey") String routingKey, @RequestParam("data") String data) {
        rabbitService.send(exchange, routingKey, data);
        return "ok";
    }

    @RequestMapping("sendDelay")
    public String sendDelay(@RequestParam("exchange") String exchange,
                            @RequestParam(value = "routingKey", required = false, defaultValue = "*") String routingKey,
                            @RequestParam("data") String data,
        @RequestParam(value = "seconds", required = false, defaultValue = "0") int seconds) {
        logger.info("send.delay, exchange: {}, routingKey: {}, data: {}, seconds: {}", exchange, routingKey, data, seconds);
        rabbitService.sendDelay(exchange, routingKey, data, seconds);
        return "ok";
    }

}
