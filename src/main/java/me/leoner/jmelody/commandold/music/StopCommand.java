package me.leoner.jmelody.commandold.music;

import me.leoner.jmelody.audio.PlayerManager;
import me.leoner.jmelody.button.ButtonInteractionEnum;
import me.leoner.jmelody.commandold.AbstractCommand;
import me.leoner.jmelody.commandold.CommandContext;
import me.leoner.jmelody.exception.BaseException;
import me.leoner.jmelody.manager.GuildPlayerManager;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class StopCommand extends AbstractCommand {

    @Override
    public String getAlias() {
        return "stop";
    }

    @Override
    public String getName() {
        return "Stop Command";
    }

    @Override
    public String getDescription() {
        return "Stops the song currently playing, clears the music queue and disconnects from the voice channel.";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        final GuildPlayerManager manager = PlayerManager.getGuildPlayerManager(context.getGuild());
        manager.stop();
        return "**stopped** the player and all songs have been removed from the queue.";
    }
}
