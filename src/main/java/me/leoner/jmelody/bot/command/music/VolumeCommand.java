package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.NowPlayingButtonInteractionEnum;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;
import java.util.Objects;

public class VolumeCommand extends AbstractCommand {

    public VolumeCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "volume", "Volume to set the player", false));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        OptionMapping optionVolume = event.getOption("volume");
        if (Objects.isNull(optionVolume)) {
            view(event.deferReply(), guild);
            return;
        }

        Member user = event.getMember();
        Integer volume = optionVolume.getAsInt();
        change(event.deferReply(), guild, user, volume);
    }

    @Override
    public void handleButton(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        Member user = event.getMember();
        NowPlayingButtonInteractionEnum button = NowPlayingButtonInteractionEnum.getByButtonId(event.getButton().getId());
        Integer currentVolume = PlayerManager.getInstance().getVolume(guild);
        Integer volume = this.getVolumeFromButton(button, currentVolume);
        change(event.deferReply(), guild, user, volume);
    }

    private Integer getVolumeFromButton(NowPlayingButtonInteractionEnum button, Integer currentVolume) {
        return switch (button) {
            case NP_MUTED -> 0;
            case NP_VOLUME_UP -> Math.min(currentVolume + 10, 100);
            case NP_VOLUME_DOWN -> Math.max(currentVolume - 10, 0);
            default -> currentVolume;
        };
    }

    private void view(ReplyCallbackAction action, Guild guild) {
        action.queue(message -> {
            Integer currentVolume = PlayerManager.getInstance().getVolume(guild);
            replyEmbed(message, EmbedGenerator.withMessage("The player volume is at `" + currentVolume + "%`"), true);
        });
    }

    private void change(ReplyCallbackAction action, Guild guild, Member user, Integer volume) {
        action.queue(message -> {
            PlayerManager.getInstance().setVolume(guild, volume);
            replyEmbed(message, EmbedGenerator.withMessage(user.getAsMention() + " set the player volume to `" + volume + "%`"), true);
        });
    }
}
