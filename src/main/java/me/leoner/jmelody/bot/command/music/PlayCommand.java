package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.player.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PlayCommand extends AbstractCommand {

    public PlayCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply().queue(message -> {
            PlayerManager.getInstance().play(event);
            message.editOriginal("Song loaded!").queue();
        });
    }
}
