package me.leoner.jmelody.serviceold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.rabbitmq.RabbitModule;
import me.leoner.jmelody.redis.RedisModule;

import java.util.Optional;

@AllArgsConstructor
public class ApplicationModules {

    private static ApplicationModules instance;

    @Getter
    private final RedisModule redis;
    @Getter
    private final RabbitModule rabbit;

    public static synchronized void initialize(RedisModule redis, RabbitModule rabbit) {
        if (instance != null) {
            throw new IllegalStateException("ApplicationModules already initialized.");
        }

        instance = new ApplicationModules(redis, rabbit);
    }

    public static ApplicationModules getInstance() {
        return Optional
                .ofNullable(instance)
                .orElseThrow(() -> new IllegalStateException("ApplicationModules has not been initialized."));
    }
}
