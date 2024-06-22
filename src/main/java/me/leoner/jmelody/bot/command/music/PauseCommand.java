package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.audio.PlayerManager;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.BaseException;
import me.leoner.jmelody.bot.manager.GuildPlayerManager;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class PauseCommand extends AbstractCommand {

    @Override
    public String getAlias() {
        return "pause";
    }

    @Override
    public String getName() {
        return "Pause Music Command";
    }

    @Override
    public String getDescription() {
        return "Pauses the music currently playing";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        final GuildPlayerManager manager = PlayerManager.getGuildPlayerManager(context.getGuild());
        String result = manager.pause() ? "paused" : "resumed";
        return "**" + result + " the player**";
    }
}
