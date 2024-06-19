package me.leoner.jmelody.bot.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.leoner.jmelody.bot.JMelody;
import me.leoner.jmelody.bot.command.NowPlayingButtonInteractionEnum;
import me.leoner.jmelody.bot.modal.RequestPlay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Objects;

public class NowPlayingService {

    private final RedisClient redis;

    public NowPlayingService() {
        redis = RedisClient.getClient();
    }

    public void update(RequestPlay request, AudioTrack track) {
        request.getTextChannel()
                .sendMessageEmbeds(EmbedGenerator.withNowPlaying(track, request.getMember()))
                .addActionRow(getActions(1))
                .addActionRow(getActions(2))
                .queue(message -> updateMessages(request.getGuild().getId(), request.getTextChannel().getId(), message.getId()));
    }

    private void updateMessages(String guildId, String channelId, String messageId) {
        String baseKey = "NOW_PLAYING:".concat(guildId);
        removeOldMessage(baseKey);
        String value = channelId.concat(":").concat(messageId);
        updateValue(baseKey, value);
    }

    private void removeOldMessage(String baseKey) {
        String channelMessage = redis.get(baseKey, String.class);
        if (Objects.isNull(channelMessage) || channelMessage.isEmpty()) return;

        String[] data = channelMessage.split(":");
        JMelody.removeMessageFromChannel(data[0], data[1]);
    }

    private void updateValue(String baseKey, String value) {
        redis.set(baseKey, value);
    }

    private List<Button> getActions(Integer line) {
        List<NowPlayingButtonInteractionEnum> buttons = NowPlayingButtonInteractionEnum.getByLine(line);

        return buttons.stream()
                .map(button -> Button.secondary(button.getName(), Emoji.fromFormatted(button.getEmote())))
                .toList();
    }
}
