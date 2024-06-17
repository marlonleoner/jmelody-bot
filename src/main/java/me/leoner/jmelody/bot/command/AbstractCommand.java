package me.leoner.jmelody.bot.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class AbstractCommand {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final String name;

    private final String description;

    private final List<OptionData> options;

    public AbstractCommand(String name, String description) {
        this(name, description, new ArrayList<>());
    }

    public AbstractCommand(String name, String description, List<OptionData> options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public Boolean hasOptions() {
        return Objects.nonNull(options) && !options.isEmpty();
    }

    protected void addOption(OptionData option) {
        this.options.add(option);
    }

    public abstract void handle(SlashCommandInteractionEvent event) throws CommandException;
}
