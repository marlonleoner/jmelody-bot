package me.leoner.jmelody.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.service.LoggerService;
import me.leoner.jmelody.service.RedisService;
import redis.clients.jedis.JedisPool;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConfig {

    public static void load() {
        ApplicationContext context = ApplicationContext.getContext();

        try (JedisPool pool = new JedisPool(
                context.getRedisHost(),
                context.getRedisPort(),
                context.getRedisUsername(),
                context.getRedisPassword()
        )) {
            RedisService.load(pool.getResource());
            LoggerService.info(BotConfig.class, "Redis loaded!");
        }
    }
}
