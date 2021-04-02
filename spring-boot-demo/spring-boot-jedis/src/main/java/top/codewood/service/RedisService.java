package top.codewood.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Map;
import java.util.function.Function;

@Service
public class RedisService {

    private JedisPool jedisPool;

    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void set(String key, String value) {
        execute(jedis -> jedis.set(key, value));
    }

    public void set(String key, String value, int seconds) {
        execute(jedis -> jedis.setex(key, seconds, value));
    }

    public void hmset(String key, Map<String, String> value) {
        execute(jedis -> jedis.hmset(key, value));
    }

    public void hmset(String key, Map<String, String> value, int seconds) {
        execute(jedis -> {
            jedis.hmset(key, value);
            jedis.expire(key, seconds);
            return null;
        });
    }

    public String get(String key) {
        return execute(jedis -> jedis.get(key));
    }

    public boolean del(String key) {
        return execute(jedis -> jedis.del(key) > 0);
    }

    public Long incr(String key) {
        return execute(jedis -> jedis.incr(key));
    }

    public Long incr(String key, Integer integer) {
        return execute(jedis -> jedis.incrBy(key, integer));
    }

    public Long decr(String key) {
        return execute(jedis -> jedis.decr(key));
    }

    public Long decr(String key, Integer integer) {
        return execute(jedis -> jedis.decrBy(key, integer));
    }

    public boolean exists(String key) {
        return execute(jedis -> jedis.exists(key));
    }

    /**
     * 无效
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean setnx(String key, String value, int seconds) {
        return execute(jedis -> jedis.set(key, value, SetParams.setParams().nx().ex(seconds)) != null);
    }

    private <T> T execute(Function<Jedis, Object> function) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return (T) function.apply(jedis);
        } finally {
            if (jedis != null && jedis.isConnected()) {
                jedis.close();
            }
        }
    }

}
