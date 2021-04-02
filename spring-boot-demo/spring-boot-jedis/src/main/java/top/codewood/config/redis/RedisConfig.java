package top.codewood.config.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import top.codewood.util.AppExecutor;

@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnProperty("jedis.host")
    public JedisPool jedisPool(@Value("${jedis.host}") String host, @Value("${jedis.port}") int port,
                               @Value("${jedis.password}") String password
    ) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxWaitMillis(5 * 1000);

        JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig(), host, port, 5 * 1000, password);

        /**
         * 无效
         */
        AppExecutor.getService().execute(() -> {
            Jedis jedis = jedisPool.getResource();
            jedis.psubscribe(new KeyExpiredListener(), "*");
        });

        return jedisPool;
    }

}
