package me.leoner.jmelody.redis;

public final class RedisModule {

    private final RedisConfiguration configuration;

    public RedisModule(RedisConfiguration configuration) {
        this.configuration = configuration;
    }

    public void set(String key, String value) {
        try (var jedis = configuration.getResource()) {
            jedis.set(key, value);
        }
    }

    public String get(String key) {
        try (var jedis = configuration.getResource()) {
            return jedis.get(key);
        }
    }

    public boolean exists(String key) {
        try (var jedis = configuration.getResource()) {
            return jedis.exists(key);
        }
    }

    public void delete(String key) {
        try (var jedis = configuration.getResource()) {
            jedis.del(key);
        }
    }
}