package top.codewood.spi;

import top.codewood.spi.service.HelloSPIService;

import java.util.Iterator;
import java.util.ServiceLoader;

public class HelloSPIApplication {

    public static void main(String[] args) {

        ServiceLoader<HelloSPIService> helloSPIServices = ServiceLoader.load(HelloSPIService.class);
        Iterator<HelloSPIService> iterator = helloSPIServices.iterator();
        while (iterator.hasNext()) {
            HelloSPIService helloSPIService = iterator.next();
            helloSPIService.sayHi();
        }

    }

}
