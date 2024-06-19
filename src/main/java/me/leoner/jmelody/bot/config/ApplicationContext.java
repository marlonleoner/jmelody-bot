package me.leoner.jmelody.bot.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Data
public class ApplicationContext {

    private static ApplicationContext context;

    private String token;

    private String redisHost;

    private Integer redisPort;

    private String redisUsername;

    private String redisPassword;

    private String spotifyClientId;

    private String spotifyClientSecret;

    private String spotifyCountryCode;

    public static ApplicationContext getContext() {
        if (Objects.isNull(context)) {
            context = new ApplicationContext();
        }

        return context;
    }
}
