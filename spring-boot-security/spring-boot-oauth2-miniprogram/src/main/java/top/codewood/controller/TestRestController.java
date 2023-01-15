package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/rest")
public class TestRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(TestRestController.class);

    @RequestMapping("t1")
    public String t1() {
        return "t1-" + System.currentTimeMillis();
    }

    @RequestMapping("t2")
    public String t2() {
        return "t2-" + System.currentTimeMillis();
    }

}
