package top.codewood.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

    {
        System.out.println("HelloController loaded.");
    }

    @RequestMapping("")
    public String index() {
        return "hello/index";
    }

}
