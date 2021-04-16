package top.codewood.config.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ElasticSearchConfig {

    static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${es.host:127.0.0.1}")
    private String host;

    @Value("${es.port:9200}")
    private int port;

    @Value("${es.scheme:http}")
    private String scheme;

    @Bean
    public RestClient elasticSearchRestClient() {
        return RestClient.builder(createHttpHost()).build();
    }

    private HttpHost createHttpHost() {
        return new HttpHost(host, port, scheme);
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(createHttpHost()));
    }

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("host: {}, port: {}, scheme: {}", host, port, scheme);
    }


}
