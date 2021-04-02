package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootJedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJedisApplication.class, args);
    }

}
