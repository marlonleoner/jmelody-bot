package me.leoner.jmelody.bot.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Objects;

public class RedisClient {

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
            LoggerService.warn(RedisClient.class, "Error setting value to redis: {}", ex.getMessage());
        }
    }

    public <T> T get(String key, Class<T> type) {
        try {
            return mapper.readValue(jedis.get(key), type);
        } catch (Exception ex) {
            return null;
        }
    }

    public <T> List<T> getAll(String key, Class<T> type) {
        return jedis.keys(key)
                .stream()
                .map(k -> get(k, type))
                .toList();
    }

    public List<String> getKeys(String key) {
        return jedis.keys(key)
                .stream()
                .toList();
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
