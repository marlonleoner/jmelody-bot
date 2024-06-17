package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand extends AbstractCommand {

    public StopCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws CommandException {
        event.reply("handle stop command").queue();
    }
}
