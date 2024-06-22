package me.leoner.jmelody.bot.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.exception.BaseException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommand {

    public abstract String getAlias();

    public abstract String getName();

    public abstract String getDescription();

    public abstract List<OptionData> getOptions();

    public boolean hasOptions() {
        List<OptionData> optionsData = this.getOptions();
        return Objects.nonNull(optionsData) && !optionsData.isEmpty();
    }

    public String handle(CommandContext context) throws BaseException {
        return handle(context, null);
    }

    public abstract String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException;

    protected void success(InteractionHook message, MessageEmbed embed) {
        message.editOriginalEmbeds(embed).queue();
    }
}

