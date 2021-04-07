package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootScheduleApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SpringBootScheduleApplication.class, args);
        new CountDownLatch(1).await();
    }

}
