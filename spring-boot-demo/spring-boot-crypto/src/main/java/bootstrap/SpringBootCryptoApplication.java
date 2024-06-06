package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication(scanBasePackages = "top.codewood")
public class SpringBootCryptoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCryptoApplication.class, args);
    }

}
