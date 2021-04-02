package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootRabbitMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRabbitMQApplication.class, args);
    }

}
