package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandException;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import me.leoner.jmelody.bot.modal.RequestPlay;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.utils.EventUtils;
import me.leoner.jmelody.bot.utils.LinkUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PlayCommand extends AbstractCommand {

    public PlayCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws CommandException {
        RequestPlay request = this.createRequest(event);

        event.deferReply().queue(message -> {
            PlayerManager.getInstance().play(request);
            message.editOriginalEmbeds(EmbedGenerator.withMessage("Song added")).queue();
        });
    }

    private RequestPlay createRequest(SlashCommandInteractionEvent event) throws CommandException {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        AudioChannelUnion voiceChannel = EventUtils.getVoiceChannelFromUser(member);
        String song = LinkUtils.getSearchTermOrUrl(EventUtils.getParam(event, "song"));

        RequestPlay request = new RequestPlay();
        request.setGuild(guild);
        request.setTextChannel(event.getChannel());
        request.setVoiceChannel(voiceChannel);
        request.setMember(member);
        request.setSong(song);

        return request;
    }
}
