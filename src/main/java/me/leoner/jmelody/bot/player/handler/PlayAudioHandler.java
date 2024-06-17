package me.leoner.jmelody.bot.player.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AllArgsConstructor;
import me.leoner.jmelody.bot.JMelody;
import me.leoner.jmelody.bot.modal.RequestPlay;
import me.leoner.jmelody.bot.player.GuildPlayerManager;
import me.leoner.jmelody.bot.service.EmbedGenerator;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PlayAudioHandler implements AudioLoadResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(PlayAudioHandler.class);

    private RequestPlay request;

    private GuildPlayerManager manager;

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(request);
        manager.queue(track);
        request.getMessage().editOriginalEmbeds(EmbedGenerator.withTrackAdded(track, request.getMember())).queue();

        JMelody.startPlayer(request.getGuild(), request.getMember());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        boolean isSearch = playlist.isSearchResult();

        List<AudioTrack> tracks = playlist.getTracks();
        if (isSearch) {
            tracks = Collections.singletonList(tracks.get(0));
        }

        for (AudioTrack track : tracks) {
            track.setUserData(request);
            manager.queue(track);
        }

        MessageEmbed embed = EmbedGenerator.withPlaylistAdded(playlist, request.getMember());
        if (isSearch) {
            embed = EmbedGenerator.withTrackAdded(tracks.get(0), request.getMember());
        }
        request.getMessage().editOriginalEmbeds(embed).queue();

        JMelody.startPlayer(request.getGuild(), request.getMember());
    }

    @Override
    public void noMatches() {
        logger.error("No music was found with {}", request.getSong());
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        logger.error("An error occurred while loading the track {}: {}", request.getSong(), exception.getMessage());
    }
}
