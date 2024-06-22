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

public class BassCommand extends AbstractCommand {

    @Override
    public String getAlias() {
        return "bass";
    }

    @Override
    public String getName() {
        return "Change bass level";
    }

    @Override
    public String getDescription() {
        return "Change bass level";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "level", "Bass level", true));
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        final GuildPlayerManager manager = PlayerManager.getGuildPlayerManager(context.getGuild());
        Integer bass = context.getValueParamByKey("level");
        manager.setBass(bass);
        return "**changed** bass to **`" + bass + "%`**";
    }
}
