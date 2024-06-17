package me.leoner.jmelody.bot.command;

import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CommandManager extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private final List<AbstractCommand> commands;

    public CommandManager() {
        this.commands = CommandEnum.getAllCommands();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            LOGGER.info("Setting commands on guild '{}'", guild.getName());
            this.commands.forEach(command -> {
                LOGGER.debug("Setting command '/{}' - {}", command.getName(), command.getDescription());
                CommandCreateAction action = guild.upsertCommand(command.getName(), command.getDescription());
                if (command.hasOptions()) action = action.addOptions(command.getOptions());
                action.queue();
            });
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Optional<AbstractCommand> commandOptional = this.commands
                .stream()
                .filter(command -> command.getName().equals(event.getName()))
                .findFirst();

        if (commandOptional.isEmpty()) {
            this.replyException(event, "Command `/" + event.getName() + "` not found", true);
            return;
        }

        try {
            commandOptional.get().handle(event);
        } catch (CommandException ex) {
            this.replyException(event, ex.getMessage(), ex.getEphemeral());
        } catch (Exception ex) {
            this.replyException(event, "Hey, something went wrong: " + ex.getMessage(), true);
        }
    }

    private void replyException(SlashCommandInteractionEvent event, String message, Boolean ephemeral) {
        event.replyEmbeds(EmbedGenerator.withErrorMessage(event.getMember().getAsMention() + " " + message)).setEphemeral(ephemeral).queue();
    }
}
