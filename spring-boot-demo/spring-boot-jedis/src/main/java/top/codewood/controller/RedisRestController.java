package top.codewood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.service.RedisService;

@RestController
@RequestMapping("/redis/rest")
public class RedisRestController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("/set")
    public String set(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam(value = "seconds", required = false) Integer seconds) {
        if (seconds != null) {
            redisService.set(key, value, seconds);
        } else {
            redisService.set(key, value);
        }
        return "ok";
    }

    @RequestMapping("/get")
    public String get(@RequestParam("key") String key) {
        return redisService.get(key);
    }

    @RequestMapping("/del")
    public String del(@RequestParam("key") String key) {
        redisService.del(key);
        return "ok";
    }

    @RequestMapping("/exists")
    public boolean exists(@RequestParam("key") String key) {
        return redisService.exists(key);
    }


}
