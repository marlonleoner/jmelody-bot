package me.leoner.jmelody.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.service.LoggerService;

import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotConfig {

    public static void load() {
        LoggerService.info(BotConfig.class, "Starting BotConfig...");

        ApplicationContext context = ApplicationContext.getContext();
        // Discord
        context.setToken(EnvLoader.get("DISCORD_TOKEN"));
        // Redis
        context.setRedisHost(EnvLoader.get("REDIS_HOST"));
        context.setRedisPort(Integer.valueOf(EnvLoader.get("REDIS_PORT")));
        context.setRedisUsername(EnvLoader.get("REDIS_USER"));
        context.setRedisPassword(EnvLoader.get("REDIS_PASS"));
        // Spotify
        context.setSpotifyClientId(EnvLoader.get("SPOTIFY_CLIENT_ID"));
        context.setSpotifyClientSecret(EnvLoader.get("SPOTIFY_CLIENT_SECRET"));
        context.setSpotifyCountryCode(EnvLoader.get("SPOTIFY_COUNTRY_CODE"));
        // Scheduler
        context.setScheduler(Executors.newSingleThreadScheduledExecutor());
        // Now Playing
        context.setNowPlayingIcon(EnvLoader.get("NOW_PLAYING_ICON"));
        context.setNowPlayingImage(EnvLoader.get("NOW_PLAYING_IMAGE"));

        LoggerService.info(BotConfig.class, "Properties loaded!");
    }
}
