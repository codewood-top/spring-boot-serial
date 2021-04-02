package top.codewood.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

public class KeyExpiredListener extends JedisPubSub {

    static final Logger LOGGER = LoggerFactory.getLogger(KeyExpiredListener.class);

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.info("onPSubscribe, pattern: {}, subscribedChannels: {}", pattern, subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        LOGGER.info("onPMessage, pattern: {}, channel: {}, message: {}", pattern, channel, message);
    }

    @Override
    public void onMessage(String channel, String message) {
        LOGGER.info("onMessage, pattern: {}, channel: {}, message: {}", channel, message);
    }
}
