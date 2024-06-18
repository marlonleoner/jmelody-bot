package me.leoner.jmelody.bot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private static RedisClient client;

    private final ObjectMapper mapper;

    private final Jedis jedis;

    protected RedisClient() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try (JedisPool pool = new JedisPool(System.getProperty("REDIS_HOST"), Integer.parseInt(System.getProperty("REDIS_PORT")))) {
            this.jedis = pool.getResource();
        }
    }

    public void set(String key, Object value) {
        try {
            this.jedis.set(key, mapper.writeValueAsString(value));
        } catch (Exception ex) {
            logger.warn("Error setting value to redis: {}", ex.getMessage());
        }
    }

    public <T> T get(String key, Class<T> type) {
        try {
            return mapper.readValue(jedis.get(key), type);
        } catch (Exception ex) {
            return null;
        }
    }

    public static RedisClient getClient() {
        if (Objects.isNull(client)) {
            client = new RedisClient();
        }

        return client;
    }
}
