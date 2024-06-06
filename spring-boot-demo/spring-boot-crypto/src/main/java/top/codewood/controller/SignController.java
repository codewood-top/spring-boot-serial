package top.codewood.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("sign")
public class SignController {

    @RequestMapping("req")
    public String req(HttpServletRequest request) {
        System.out.println(request.getParameterMap());
        return "success";
    }

}
