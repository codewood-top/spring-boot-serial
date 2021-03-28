package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootOauth2CutomApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOauth2CutomApplication.class, args);
    }

}
