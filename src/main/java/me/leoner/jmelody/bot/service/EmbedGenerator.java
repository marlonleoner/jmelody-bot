package me.leoner.jmelody.bot.service;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;

public class EmbedGenerator {

    private static final int COLOR = 0xd4e300;

    private EmbedGenerator() {
        // empty constructor
    }

    private static String formatDuration(long duration) {
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private static EmbedBuilder createBase(boolean timestamp) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(COLOR);
        if (timestamp) {
            embed.setTimestamp(Instant.now());
        }

        return embed;
    }

    public static MessageEmbed withMessage(String message) {
        EmbedBuilder embed = EmbedGenerator.createBase(false);
        embed.setDescription(message);

        return embed.build();
    }

    public static MessageEmbed withErrorMessage(String message) {
        return EmbedGenerator.withMessage("❎ | " + message);
    }

    public static MessageEmbed withTrackAdded(AudioTrack track, Member member) {
        AudioTrackInfo infos = track.getInfo();

        EmbedBuilder embed = EmbedGenerator.createBase(false);
        embed.setDescription("✅ | " + member.getAsMention() + " **added [`" + infos.title + "`](" + infos.uri + ") to the queue**");

        return embed.build();
    }

    public static MessageEmbed withPlaylistAdded(AudioPlaylist playlist, Member member) {
        EmbedBuilder embed = EmbedGenerator.createBase(false);
        embed.setDescription("✅ | " + member.getAsMention() + " **added `" + playlist.getName() + " (" + playlist.getTracks().size() + "songs)` to the queue**");

        return embed.build();
    }

    public static MessageEmbed withNowPlaying(AudioTrack track, Member member) {
        AudioTrackInfo infos = track.getInfo();

        EmbedBuilder embed = EmbedGenerator.createBase(true);
        embed.setAuthor("Now Playing", null, System.getProperty("NOW_PLAYING_ICON"));
        embed.addField("Track", "[`" + infos.title + "`](" + infos.uri + ")", true);
        embed.addField("Request by", member.getAsMention(), true);
        embed.addField("Duration", "`" + EmbedGenerator.formatDuration(infos.length) + "`", true);
        embed.setImage(System.getProperty("NOW_PLAYING_IMAGE"));

        return embed.build();
    }
}
