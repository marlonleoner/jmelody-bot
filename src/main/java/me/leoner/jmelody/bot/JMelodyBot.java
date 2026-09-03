package me.leoner.jmelody.bot;

import lombok.extern.slf4j.Slf4j;
import me.leoner.jmelody.command.CommandRegister;
import me.leoner.jmelody.config.EnvironmentConfiguration;
import me.leoner.jmelody.listener.CommandListener;
import me.leoner.jmelody.rabbitmq.RabbitConfiguration;
import me.leoner.jmelody.rabbitmq.RabbitModule;
import me.leoner.jmelody.redis.RedisConfiguration;
import me.leoner.jmelody.redis.RedisModule;
import me.leoner.jmelody.service.ApplicationModules;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

@Slf4j
public class JMelodyBot {

    private final EnvironmentConfiguration environment;
    private final RedisConfiguration redisConfiguration;
    private final RedisModule redisModule;
    private final RabbitConfiguration rabbitConfiguration;
    private final RabbitModule rabbitModule;
    private final JDABuilder builder;

    public JMelodyBot() {
        this.environment = EnvironmentConfiguration.load();

        this.redisConfiguration = new RedisConfiguration(environment);
        this.redisModule = new RedisModule(this.redisConfiguration);

        this.rabbitConfiguration = new RabbitConfiguration(environment);
        this.rabbitModule = new RabbitModule(this.rabbitConfiguration);

        this.builder = JDABuilder
                .createDefault(environment.getToken())
                .setActivity(Activity.listening("some music"))
                .addEventListeners(new CommandListener());
    }

    public void run() {
        try {
            this.redisConfiguration.connect();

            this.rabbitConfiguration.connect();
            this.rabbitModule.start();

            ApplicationModules.initialize(
                    redisModule,
                    rabbitModule
            );

            JDA jda = builder
                    .build()
                    .awaitReady();

            CommandRegister.register(jda);

            log.info("Connected as <{}: {} />", jda.getSelfUser().getName(), jda.getSelfUser().getId());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
