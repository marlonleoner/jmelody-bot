package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class NextCommand extends AbstractCommand {

    public NextCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply().queue(message -> {
            PlayerManager.getInstance().next(event.getGuild());
            message.editOriginalEmbeds(EmbedGenerator.withMessage(event.getMember().getAsMention() + " **skipped** to the next track")).queue();
        });
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }
}