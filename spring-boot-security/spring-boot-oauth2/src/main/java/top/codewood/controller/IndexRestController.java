package top.codewood.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class IndexRestController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello, spring-boot-oauth2.";
    }

    @RequestMapping("/info")
    public Object info() {
        return "Hello, info";
    }

}
