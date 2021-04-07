package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootWxJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWxJavaApplication.class, args);
    }

}
