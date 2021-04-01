package bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "top.codewood")
@EntityScan(basePackages = "top.codewood")
@EnableJpaRepositories(basePackages = "top.codewood")
public class SpringBootJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJpaApplication.class, args);
    }

}
