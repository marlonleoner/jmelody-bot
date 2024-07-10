package me.leoner.jmelody.bot.command;

import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.exception.BaseException;
import me.leoner.jmelody.bot.exception.CommandException;
import me.leoner.jmelody.bot.service.LoggerService;
import me.leoner.jmelody.bot.service.MessageFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CommandManager extends ListenerAdapter {

    private final List<AbstractCommand> commands;

    public CommandManager() {
        this.commands = CommandEnum.getAllCommands();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();
        LoggerService.info(getClass(), "Guild '{}' ready, setting commands...", guild.getName());
        this.commands.forEach(command -> {
            LoggerService.debug(getClass(), "Setting command '/{}' - {}", command.getName(), command.getDescription());
            CommandCreateAction action = guild.upsertCommand(command.getAlias(), command.getDescription());
            if (command.hasOptions()) action = action.addOptions(command.getOptions());
            action.queue();
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue(message -> {
            CommandContext context = createCommandContext(event, message);
            try {
                AbstractCommand command = this.getCommandByAlias(event.getName());
                if (Objects.isNull(command)) {
                    throw new CommandException("Command /" + event.getName() + " not found!");
                }

                context.setParams(getParams(event, command));
                String result = command.handle(context);
                commandSuccess(context).accept(result);
            } catch (BaseException ex) {
                commandFailure(context).accept(ex);
            }
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        event.deferReply().queue(message -> {
            CommandContext context = createCommandContext(event, message);
            try {
                String buttonId = event.getButton().getId();
                ButtonInteractionEnum button = ButtonInteractionEnum.getByButtonId(buttonId);
                if (Objects.isNull(button)) {
                    throw new CommandException("Button " + buttonId + " not found", true);
                }

                AbstractCommand command = this.getCommandByType(button);
                if (Objects.isNull(command)) {
                    throw new CommandException("Command /" + button.getName() + " not found!");
                }

                String result = command.handle(context, button);
                commandSuccess(context).accept(result);
            } catch (BaseException ex) {
                commandFailure(context).accept(ex);
            }
        });
    }

    private CommandContext createCommandContext(SlashCommandInteractionEvent event, InteractionHook message) {
        return new CommandContext(event, message);
    }

    private CommandContext createCommandContext(ButtonInteractionEvent event, InteractionHook message) {
        return new CommandContext(event, message);
    }

    private Consumer<String> commandSuccess(CommandContext context) {
        return (String message) -> MessageFactory.success(context, message).queue();
    }

    private Consumer<BaseException> commandFailure(CommandContext context) {
        return throwable -> MessageFactory.failure(context, throwable).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    private Map<String, Object> getParams(SlashCommandInteractionEvent event, AbstractCommand command) {
        Map<String, Object> params = new HashMap<>();
        for (OptionData option : command.getOptions()) {
            Object value = getOptionValueFromEvent(event, option);
            if (Objects.nonNull(value)) {
                params.put(option.getName(), value);
            }
        }

        return params;
    }

    private Object getOptionValueFromEvent(SlashCommandInteractionEvent event, OptionData option) {
        OptionMapping mapping = event.getOption(option.getName());
        if (Objects.isNull(mapping)) return null;

        return switch (option.getType()) {
            case INTEGER -> mapping.getAsInt();
            case BOOLEAN -> mapping.getAsBoolean();
            default -> mapping.getAsString();
        };
    }

    private AbstractCommand getCommandByAlias(String commandAlias) {
        return this.commands.stream()
                .filter(c -> c.getAlias().equals(commandAlias))
                .findFirst()
                .orElse(null);
    }

    private AbstractCommand getCommandByType(ButtonInteractionEnum button) {
        return this.commands.stream()
                .filter(command -> command.getClass().equals(button.getCommandType()))
                .findFirst()
                .orElse(null);
    }
}
