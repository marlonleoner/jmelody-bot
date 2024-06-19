package me.leoner.jmelody.bot.command;

import me.leoner.jmelody.bot.modal.exception.CommandException;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import me.leoner.jmelody.bot.service.LoggerService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CommandManager extends ListenerAdapter {

    private final List<AbstractCommand> commands;

    public CommandManager() {
        this.commands = CommandEnum.getAllCommands();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            LoggerService.info(getClass(), "Setting commands on guild '{}'", guild.getName());
            this.commands.forEach(command -> {
                LoggerService.debug(getClass(), "Setting command '/{}' - {}", command.getName(), command.getDescription());
                CommandCreateAction action = guild.upsertCommand(command.getName(), command.getDescription());
                if (command.hasOptions()) action = action.addOptions(command.getOptions());
                action.queue();
            });
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            AbstractCommand command = this.getCommandByName(event.getName());
            command.handle(event);
        } catch (CommandException ex) {
            LoggerService.error(getClass(), "onSlashCommandInteraction - CommandException: {}", ex.getMessage());
            this.replyException(event.deferReply(), event.getMember(), ex.getMessage(), ex.getEphemeral());
        } catch (Exception ex) {
            LoggerService.error(getClass(), "onSlashCommandInteraction- An unknown error ocurred: {}", ex.getMessage());
            this.replyException(event.deferReply(), event.getMember(), "Hey, something went wrong: " + ex.getMessage(), true);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            String buttonId = event.getButton().getId();
            NowPlayingButtonInteractionEnum button = NowPlayingButtonInteractionEnum.getByButtonId(buttonId);
            if (Objects.isNull(button)) {
                throw new CommandException("Button " + buttonId + " not found", true);
            }

            AbstractCommand command = this.getCommandByType(button);
            command.handleButton(event);
        } catch (CommandException ex) {
            LoggerService.error(getClass(), "onButtonInteraction - CommandException: {}", ex.getMessage());
            this.replyException(event.deferReply(), event.getMember(), ex.getMessage(), ex.getEphemeral());
        } catch (Exception ex) {
            LoggerService.error(getClass(), "onButtonInteraction - An unknown error ocurred: {}", ex.getMessage());
            this.replyException(event.deferReply(), event.getMember(), "Hey, something went wrong: " + ex.getMessage(), true);
        }
    }

    private AbstractCommand getCommandByName(String commandName) throws CommandException {
        Optional<AbstractCommand> commandOptional = this.commands
                .stream()
                .filter(command -> command.getName().equals(commandName))
                .findFirst();

        if (commandOptional.isEmpty()) {
            throw new CommandException("Command `/" + commandName + "` not found", true);
        }

        return commandOptional.get();
    }

    private AbstractCommand getCommandByType(NowPlayingButtonInteractionEnum button) throws CommandException {
        Optional<AbstractCommand> commandOptional = this.commands
                .stream()
                .filter(command -> command.getClass().equals(button.getCommandType()))
                .findFirst();

        if (commandOptional.isEmpty()) {
            throw new CommandException("Command /" + button.getName() + " not found", true);
        }

        return commandOptional.get();
    }

    private void replyException(ReplyCallbackAction action, Member member, String message, Boolean ephemeral) {
        action.setEphemeral(ephemeral).addEmbeds(EmbedGenerator.withErrorMessage(member.getAsMention() + " " + message)).queue();
    }
}
