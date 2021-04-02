package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import top.codewood.service.HelloService;

@ImportResource("classpath:META-INF/dubbo.xml")
@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootDubboConsumer {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootDubboConsumer.class, args);
        HelloService helloService = context.getBean(HelloService.class);
        System.out.println(helloService.sayHi("代码坞"));
    }

}
