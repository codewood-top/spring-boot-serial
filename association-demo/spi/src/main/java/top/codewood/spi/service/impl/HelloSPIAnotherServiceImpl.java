package top.codewood.spi.service.impl;

import top.codewood.spi.service.HelloSPIService;

public class HelloSPIAnotherServiceImpl implements HelloSPIService {

    {
        System.out.println("HelloSPIAnotherServiceImpl loaded.");
    }

    @Override
    public void sayHi() {
        System.out.println("Hi, this is HelloSPIAnotherServiceImpl.");
    }

}
