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
        return EmbedGenerator.withMessage(":cross: " + message);
    }

    public static MessageEmbed withTrackAdded(AudioTrack track, Member member) {
        AudioTrackInfo infos = track.getInfo();

        EmbedBuilder embed = EmbedGenerator.createBase(true);

        embed.addField("Track Queued", "`#1` • [`" + infos.title + "`](" + infos.uri + ")", true);
        embed.addField("Request by", member.getAsMention(), true);
        embed.addField("Duration", "`" + EmbedGenerator.formatDuration(infos.length) + "`", true);

        return embed.build();
    }

    public static MessageEmbed withPlaylistAdded(AudioPlaylist playlist, Member member) {
        EmbedBuilder embed = EmbedGenerator.createBase(true);

        embed.addField("Playlist Queued", "`" + playlist.getName() + "`", true);
        embed.addField("Request by", member.getAsMention(), true);
        embed.addField("Total Tracks", "`" + playlist.getTracks().size() + " song(s)`", true);

        return embed.build();
    }

}
