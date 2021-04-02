package top.codewood.service.impl;

import org.springframework.stereotype.Service;
import top.codewood.service.HelloService;

@Service("helloService")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }

}
