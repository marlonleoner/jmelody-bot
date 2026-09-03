package me.leoner.jmelody.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AccessLevel;
import lombok.Getter;
import me.leoner.jmelody.bot.JMelodyOld;
import me.leoner.jmelody.button.ButtonInteractionEnum;
import me.leoner.jmelody.button.CategoryButtonInteractionEnum;
import me.leoner.jmelody.commandold.CommandContext;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;

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
                .addComponents(ActionRow.of(service.getActions(1)))
                .addComponents(ActionRow.of(service.getActions(2)))
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
        JMelodyOld.removeMessageFromChannel(data[0], data[1]);
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
