package top.codewood.spi.service.impl;

import top.codewood.spi.service.HelloSPIService;

public class HelloSPIServiceImpl implements HelloSPIService {

    {
        System.out.println("HelloSPIServiceImpl loaded.");
    }

    @Override
    public void sayHi() {
        System.out.println("Hi, this is HelloSPIServiceImpl.");
    }

}
