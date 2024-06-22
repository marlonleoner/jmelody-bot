package me.leoner.jmelody.bot.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AccessLevel;
import lombok.Getter;
import me.leoner.jmelody.bot.JMelody;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.button.CategoryButtonInteractionEnum;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.modal.TrackRequest;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Objects;

public class NowPlayingService {

    @Getter(AccessLevel.PRIVATE)
    private static final NowPlayingService instance = new NowPlayingService();

    private final RedisService redis;

    private NowPlayingService() {
        redis = RedisService.getClient();
    }

    public static void update(AudioTrack track) {
        NowPlayingService service = getInstance();

        CommandContext context = track.getUserData(CommandContext.class);
        context.getTextChannel()
                .sendMessageEmbeds(EmbedFactory.withNowPlaying(track, context.getMember()))
                .addActionRow(service.getActions(1))
                .addActionRow(service.getActions(2))
                .queue(message -> service.updateMessages(context.getGuild().getId(), context.getTextChannel().getId(), message.getId()));
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
        List<ButtonInteractionEnum> buttons = ButtonInteractionEnum.getByCategoryAndLine(CategoryButtonInteractionEnum.NOW_PLAYING, line);

        return buttons.stream()
                .map(button -> Button.secondary(button.getName(), Emoji.fromFormatted(button.getEmote())))
                .toList();
    }
}
