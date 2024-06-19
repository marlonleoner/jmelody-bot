package me.leoner.jmelody.bot.command;

import lombok.Getter;
import me.leoner.jmelody.bot.modal.exception.CommandException;
import me.leoner.jmelody.bot.modal.exception.TrackException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class AbstractCommand {

    private final String name;

    private final String description;

    private final List<OptionData> options;

    protected AbstractCommand(String name, String description) {
        this(name, description, new ArrayList<>());
    }

    private AbstractCommand(String name, String description, List<OptionData> options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public boolean hasOptions() {
        List<OptionData> optionsData = this.getOptions();
        return Objects.nonNull(optionsData) && !optionsData.isEmpty();
    }

    protected void replyEmbed(InteractionHook message, MessageEmbed embed, boolean autoDelete) {
        message.editOriginalEmbeds(embed).queue();
        if (autoDelete) message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
    }

    public abstract List<OptionData> getOptions();

    public abstract void handle(SlashCommandInteractionEvent event) throws CommandException;

    public void handleButton(ButtonInteractionEvent event) throws CommandException {
        // empty method
    }
}
