package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandException;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand extends AbstractCommand {

    public StopCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws CommandException {
        event.deferReply().queue(message -> {
            PlayerManager.getInstance().stop(event);
            message.editOriginalEmbeds(EmbedGenerator.withMessage("Player stopped")).queue();
        });
    }
}
