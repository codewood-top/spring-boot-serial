package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logging")
public class LoggingController {

    private Logger logger = LoggerFactory.getLogger(LoggingController.class);

    private String success = "success";

    @RequestMapping("info")
    public String info(String msg) {
        logger.info("msg: {}", msg);
        return success;
    }

    @RequestMapping("warn")
    public String warn(String msg) {
        logger.warn("msg: {}", msg);
        return success;
    }

    @RequestMapping("debug")
    public String debug(String msg) {
        logger.debug("msg: {}", msg);
        return success;
    }

    @RequestMapping("error")
    public String error(String msg) {
        logger.error("msg: {}", msg);
        return success;
    }

}
