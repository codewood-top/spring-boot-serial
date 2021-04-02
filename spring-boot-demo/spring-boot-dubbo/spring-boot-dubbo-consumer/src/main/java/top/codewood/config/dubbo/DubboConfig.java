package top.codewood.config.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfig {

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181");
        registryConfig.setProtocol("zookeeper");
        return registryConfig;
    }


    @Bean
    public ApplicationConfig applicationConfig() {
        return new ApplicationConfig("SpringBootDubboConsumer");
    }

    @Bean
    public ConsumerConfig consumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(10000);
        consumerConfig.setRetries(0);
        return consumerConfig;
    }

}
