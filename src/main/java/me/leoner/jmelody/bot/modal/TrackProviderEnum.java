package me.leoner.jmelody.bot.modal;

import lombok.Getter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public enum TrackProviderEnum {

    URL(1),
    LOCAL(2),
    YOUTUBE(3, "YOUTUBE", "ytsearch:", true),
    SOUNDCLOUD(4, "SOUNDCLOUD", "scsearch:", true),
    SPOTIFY(5, "SPOTIFY", "spsearch:", true),
    APPLE_MUSIC(6, "APPLE_MUSIC", "amsearch:", false),
    DEEZER(7, "DEEZER", "dzsearch:", false),
    YANDEX(8, "YANDEX", "ymsearch:", false);

    private final Integer id;

    private final String name;

    private final String prefix;

    private final boolean active;

    private TrackProviderEnum(Integer id) {
        this(id, null, null, false);
    }

    private TrackProviderEnum(Integer id, String name, String prefix, boolean active) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.active = active;
    }

    public boolean isSearch() {
        return Objects.nonNull(getPrefix());
    }

    public static TrackProviderEnum getTrackProvider(String query) {
        return getTrackProvider(query, null);
    }

    public static TrackProviderEnum getTrackProvider(String query, Integer providerId) {
        try {
            new URL(query).toURI();
            return URL;
        } catch (URISyntaxException | MalformedURLException ex) {
            return getProviderById(providerId);
        }
    }

    public static TrackProviderEnum getProviderById(Integer id) {
        for (TrackProviderEnum provider : values()) {
            if (provider.getId().equals(id)) {
                return provider;
            }
        }

        return YOUTUBE;
    }

    public static List<TrackProviderEnum> getActives() {
        return Arrays.stream(values())
                .filter(provider -> provider.active)
                .toList();
    }
}
