package me.leoner.jmelody.bot.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class LinkUtils {

    private LinkUtils() {
        // empty constructor
    }

    public static String getSearchTermOrUrl(String song) {
        if (!LinkUtils.isURL(song)) {
            return "ytsearch:" + song;
        }

        return song;
    }

    public static boolean isURL(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }

        return true;
    }
}
