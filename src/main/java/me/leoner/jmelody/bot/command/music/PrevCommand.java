package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class PrevCommand extends AbstractCommand {

    public PrevCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply().queue(message -> {
            PlayerManager.getInstance().prev(event.getGuild());
            message.editOriginalEmbeds(EmbedGenerator.withMessage(event.getMember().getAsMention() + " **skipped** to the previous track")).queue();
        });
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }
}
