package me.leoner.jmelody.bot.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private static RedisClient client;

    private final ObjectMapper mapper;

    @Setter(AccessLevel.PRIVATE)
    private Jedis jedis;

    protected RedisClient() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
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

    public static void load(Jedis jedis) {
        RedisClient c = getClient();
        c.setJedis(jedis);
    }

    public static RedisClient getClient() {
        if (Objects.isNull(client)) {
            client = new RedisClient();
        }

        return client;
    }
}
