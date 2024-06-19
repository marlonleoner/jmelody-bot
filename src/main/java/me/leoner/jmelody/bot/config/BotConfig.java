package me.leoner.jmelody.bot.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.service.LoggerService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotConfig {

    public static void load() {
        LoggerService.info(BotConfig.class, "Starting BotConfig...");

        // Loading and updating application context with all properties
        Config config = ConfigFactory.load();

        ApplicationContext context = ApplicationContext.getContext();
        // Discord
        context.setToken(config.getString("DISCORD_TOKEN"));
        // Redis
        context.setRedisHost(config.getString("REDIS_HOST"));
        context.setRedisPort(config.getInt("REDIS_PORT"));
        context.setRedisUsername(config.getString("REDIS_USER"));
        context.setRedisPassword(config.getString("REDIS_PASS"));
        // Spotify
        context.setSpotifyClientId(config.getString("SPOTIFY_CLIENT_ID"));
        context.setSpotifyClientSecret(config.getString("SPOTIFY_CLIENT_SECRET"));
        context.setSpotifyCountryCode(config.getString("SPOTIFY_COUNTRY_CODE"));

        LoggerService.info(BotConfig.class, "Discord Token: {}", context.getToken());

        LoggerService.info(BotConfig.class, "Properties loaded!");
    }
}
