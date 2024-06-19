package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandException;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Collections;
import java.util.List;

public class PauseCommand extends AbstractCommand {

    public PauseCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws CommandException {
        execute(event.deferReply(), event.getGuild(), event.getMember());
    }

    @Override
    public void handleButton(ButtonInteractionEvent event) throws CommandException {
        execute(event.deferReply(), event.getGuild(), event.getMember());
    }

    private void execute(ReplyCallbackAction action, Guild guild, Member member) {
        action.queue(message -> {
            String state = PlayerManager.getInstance().pause(guild) ? "paused" : "resumed";
            replyEmbed(message, EmbedGenerator.withMessage(member.getAsMention() + " **" + state + "** the player"), true);
        });
    }
}