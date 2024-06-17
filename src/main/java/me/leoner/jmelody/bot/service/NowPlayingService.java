package me.leoner.jmelody.bot.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import kotlin.Pair;
import me.leoner.jmelody.bot.JMelody;
import me.leoner.jmelody.bot.modal.RequestPlay;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NowPlayingService {

    private static NowPlayingService instance;

    private final Map<String, Pair<String, String>> messages;

    public NowPlayingService() {
        this.messages = new HashMap<>();
    }

    public static void update(RequestPlay request, AudioTrack track) {
        request.getTextChannel()
                .sendMessageEmbeds(EmbedGenerator.withNowPlaying(track, request.getMember()))
                .queue(message -> {
                    NowPlayingService instance = getInstance();
                    instance.remove(request.getGuild());
                    instance.add(request.getGuild(), request.getTextChannel().getId(), message.getId());
                });
    }

    private void add(Guild guild, String channelId, String messageId) {
        this.messages.put(guild.getId(), new Pair<>(channelId, messageId));
    }

    private void remove(Guild guild) {
        Pair<String, String> channelMessage = messages.get(guild.getId());
        if (Objects.isNull(channelMessage)) return;

        JMelody.removeMessageFromChannel(channelMessage.getFirst(), channelMessage.getSecond());
    }

    private static NowPlayingService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new NowPlayingService();
        }

        return instance;
    }
}
