package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.modal.exception.CommandException;
import me.leoner.jmelody.bot.modal.RequestPlay;
import me.leoner.jmelody.bot.player.PlayerManager;
import me.leoner.jmelody.bot.utils.EventUtils;
import me.leoner.jmelody.bot.utils.LinkUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PlayCommand extends AbstractCommand {

    public PlayCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws CommandException {
        RequestPlay request = this.createRequest(event);

        event.deferReply().queue(message -> {
            request.setMessage(message);
            PlayerManager.getInstance().play(request);
        });
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "song", "Name or URL of track to play", true),
                new OptionData(OptionType.STRING, "source", "Platform to search song", false)
        );
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
