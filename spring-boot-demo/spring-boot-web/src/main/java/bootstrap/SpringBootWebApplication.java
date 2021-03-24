package bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class SpringBootWebApplication {

    static final Logger LOGGER = LoggerFactory.getLogger(SpringBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

    @GetMapping("/index")
    public String index() {
        LOGGER.info("visiting page index");
        return "index.html";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        LOGGER.info("通过/hello访问hello.html");
        return "hello.html";
    }

    @ResponseBody
    @RequestMapping("/rest/hello")
    public String helloRest() {
        LOGGER.info("visiting rest api hello");
        return "Hello, spring-boot-serial.";
    }

}
