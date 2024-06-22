package me.leoner.jmelody.bot.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.config.ApplicationContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmbedFactory {

    private static final ApplicationContext applicationContext = ApplicationContext.getContext();

    private static final int COLOR = 0x00CED1;
    private static final String BASE_MESSAGE_SUCCESS = "`✅` | ";
    private static final String BASE_MESSAGE_FAILURE = "`❌` | ";

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
        EmbedBuilder embed = EmbedFactory.createBase(false);
        embed.setDescription(message);

        return embed.build();
    }

    public static MessageEmbed withErrorMessage(String message) {
        return EmbedFactory.withMessage(BASE_MESSAGE_FAILURE + message);
    }

    public static MessageEmbed withSuccessMessage(String message) {
        return EmbedFactory.withMessage(BASE_MESSAGE_SUCCESS + message);
    }

    public static MessageEmbed withNowPlaying(AudioTrack track, Member member) {
        AudioTrackInfo infos = track.getInfo();

        EmbedBuilder embed = EmbedFactory.createBase(true);
        embed.setAuthor("Now Playing", null, applicationContext.getNowPlayingIcon());
        embed.addField("Track", "[`" + infos.title + "`](" + infos.uri + ")", true);
        embed.addField("Request by", member.getAsMention(), true);
        embed.addField("Duration", "`" + EmbedFactory.formatDuration(infos.length) + "`", true);
        embed.setImage(applicationContext.getNowPlayingImage());

        return embed.build();
    }
}
