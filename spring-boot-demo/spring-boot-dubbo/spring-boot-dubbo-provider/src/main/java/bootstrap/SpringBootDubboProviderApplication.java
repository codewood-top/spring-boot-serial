package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.CountDownLatch;

@ImportResource("classpath:META-INF/dubbo.xml")
@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootDubboProviderApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SpringBootDubboProviderApplication.class, args);
        new CountDownLatch(1).await();
    }

}
