package me.leoner.jmelody.bot.modal;

import lombok.Getter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public enum TrackProvider {

    URL(1),
    LOCAL(2),
    YOUTUBE(3, "ytsearch:", true),
    SOUNDCLOUD(4, "scsearch:", true),
    SPOTIFY(5, "spsearch:", true),
    APPLE_MUSIC(6, "amsearch:", false),
    DEEZER(7, "dzsearch:", false),
    YANDEX(8, "ymsearch:", false);

    private final Integer id;

    @Getter
    private final String prefix;

    private final boolean active;

    private TrackProvider(Integer id) {
        this(id, null, false);
    }

    private TrackProvider(Integer id, String prefix, boolean active) {
        this.id = id;
        this.prefix = prefix;
        this.active = active;
    }

    public static TrackProvider getTrackProvider(String query) {
        try {
            new URL(query).toURI();
            return URL;
        } catch (URISyntaxException | MalformedURLException ex) {
            for (TrackProvider provider : values()) {
                if (Objects.nonNull(provider.prefix) && query.contains(provider.prefix)) {
                    return provider;
                }
            }
        }

        return YOUTUBE;
    }

    public boolean isSearch() {
        return Objects.nonNull(getPrefix());
    }
}
