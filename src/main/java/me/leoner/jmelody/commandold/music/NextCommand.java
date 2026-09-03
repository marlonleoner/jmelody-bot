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

public class NextCommand extends AbstractCommand {

    @Override
    public String getAlias() {
        return "next";
    }

    @Override
    public String getName() {
        return "Skip Music Command";
    }

    @Override
    public String getDescription() {
        return "Skips to the next song in the music queue.";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        final GuildPlayerManager manager = PlayerManager.getGuildPlayerManager(context.getGuild());
        manager.next();
        return "**skipped** to the next track";
    }
}