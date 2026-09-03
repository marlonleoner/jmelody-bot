package me.leoner.jmelody.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
@Getter
public class EnvironmentConfiguration {

    private String token;
    private String redisHost;
    private Integer redisPort;
    private String redisUsername;
    private String redisPassword;
    private String spotifyClientId;
    private String spotifyClientSecret;
    private String spotifyCountryCode;
    private String rabbitHost;
    private Integer rabbitPort;
    private String rabbitVirtualHost;
    private String rabbitUsername;
    private String rabbitPassword;
    private String nowPlayingIcon;
    private String nowPlayingImage;

    public static EnvironmentConfiguration load() {
        return new EnvironmentConfiguration(
                get("DISCORD_TOKEN"),
                get("REDIS_HOST"),
                Integer.parseInt(get("REDIS_PORT")),
                get("REDIS_USER"),
                get("REDIS_PASS"),
                get("SPOTIFY_CLIENT_ID"),
                get("SPOTIFY_CLIENT_SECRET"),
                get("SPOTIFY_COUNTRY_CODE"),
                get("RABBIT_HOST"),
                Integer.parseInt(get("RABBIT_PORT")),
                get("RABBIT_VIRTUAL_HOST"),
                get("RABBIT_USERNAME"),
                get("RABBIT_PASSWORD"),
                get("NOW_PLAYING_ICON"),
                get("NOW_PLAYING_IMAGE")
        );
    }

    private static String get(String key) {
        return Optional
                .ofNullable(System.getenv(key))
                .orElseThrow(() -> new IllegalStateException("Required environment variable not configured: " + key));
    }
}

