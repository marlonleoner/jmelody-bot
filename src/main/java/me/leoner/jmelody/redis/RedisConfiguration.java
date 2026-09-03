package me.leoner.jmelody.redis;

import lombok.extern.slf4j.Slf4j;
import me.leoner.jmelody.config.EnvironmentConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class RedisConfiguration implements AutoCloseable {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private JedisPool pool;

    public RedisConfiguration(EnvironmentConfiguration environment) {
        this.host = environment.getRedisHost();
        this.port = environment.getRedisPort();
        this.username = environment.getRedisUsername();
        this.password = environment.getRedisPassword();

        log.info("Redis configuration loaded.");
    }

    public void connect() {
        var poolConfig = new JedisPoolConfig();

        this.pool = new JedisPool(poolConfig, host, port, 2_000, password);

        try (var jedis = pool.getResource()) {
            jedis.ping();
        }

        log.info("Redis connection established.");
    }

    public Jedis getResource() {
        if (pool == null) {
            throw new IllegalStateException("Redis connection has not been started.");
        }

        return pool.getResource();
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.close();
        }

        log.info("Redis connection closed.");
    }
}