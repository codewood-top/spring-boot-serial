package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "top.codewood")
@EnableJpaRepositories(basePackages = "top.codewood")
@EntityScan(basePackages = "top.codewood")
public class SpringBootOAuth2MiniprogramApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOAuth2MiniprogramApplication.class, args);
    }

}
