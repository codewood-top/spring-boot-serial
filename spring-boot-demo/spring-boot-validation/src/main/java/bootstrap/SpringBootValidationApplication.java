package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootValidationApplication.class, args);
    }

}
