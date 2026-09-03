package me.leoner.jmelody.listener;

import lombok.extern.slf4j.Slf4j;
import me.leoner.jmelody.command.CommandContext;
import me.leoner.jmelody.command.CommandEnum;
import me.leoner.jmelody.exception.BaseException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        log.info("Slash command interaction has been received: {}", event.getName());

        final var command = CommandEnum.get(event.getName());
        final var context = new CommandContext(event);

        try {
            command.handle(context);
        } catch (BaseException ex) {
            ex.printStackTrace();
        }
    }
}