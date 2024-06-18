package me.leoner.jmelody.bot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

public class RedisClient {

    private static RedisClient client;

    private final ObjectMapper mapper;

    private final Jedis jedis;

    protected RedisClient() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        this.jedis = new JedisPool(System.getProperty("REDIS_HOST"), Integer.parseInt(System.getProperty("REDIS_PORT"))).getResource();
    }

    public void set(String key, Object value) {
        try {
            this.jedis.set(key, mapper.writeValueAsString(value));
        } catch (Exception ex) {
            ex.printStackTrace();
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
