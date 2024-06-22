package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.audio.PlayerManager;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.BaseException;
import me.leoner.jmelody.bot.manager.GuildPlayerManager;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class VolumeCommand extends AbstractCommand {

    private static final Integer DELTA_VOLUME = 10;
    private static final String VOLUME_PARAM = "volume";

    @Override
    public String getAlias() {
        return "volume";
    }

    @Override
    public String getName() {
        return "Music Volume Command";
    }

    @Override
    public String getDescription() {
        return "Changes the volume of the music, by default the music will be playing at 100% volume.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, VOLUME_PARAM, "Volume to set the player", false));
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        final GuildPlayerManager manager = PlayerManager.getGuildPlayerManager(context.getGuild());
        Integer currentVolume = manager.getVolume();
        Integer volume = getVolumeParam(context, button, currentVolume);
        if (Objects.isNull(volume)) {
            return "the player volume is at `" + currentVolume + "%`";
        }

        manager.setVolume(volume);
        return " set the player volume to `" + volume + "%`";
    }

    private Integer getVolumeParam(CommandContext context, ButtonInteractionEnum button, Integer currentVolume) {
        if (Objects.isNull(button)) {
            return context.getValueParamByKey(VOLUME_PARAM);
        }

        return getVolumeFromButton(button, currentVolume);
    }

    private Integer getVolumeFromButton(ButtonInteractionEnum button, Integer currentVolume) {
        return switch (button) {
            case NP_MUTED -> 0;
            case NP_VOLUME_UP -> Math.min(currentVolume + DELTA_VOLUME, 100);
            case NP_VOLUME_DOWN -> Math.max(currentVolume - DELTA_VOLUME, 0);
            default -> currentVolume;
        };
    }
}
