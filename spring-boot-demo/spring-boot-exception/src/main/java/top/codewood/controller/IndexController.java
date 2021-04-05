package top.codewood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({""})
    public String index() {
        return "index/index";
    }

    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException("这里是个异常");
    }

}