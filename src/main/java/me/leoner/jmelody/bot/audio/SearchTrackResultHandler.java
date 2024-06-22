package me.leoner.jmelody.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import me.leoner.jmelody.bot.modal.TrackRequestContext;
import me.leoner.jmelody.bot.service.LoggerService;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchTrackResultHandler implements AudioLoadResultHandler {

    private final TrackRequestContext context;

    private AudioPlaylist playlist;

    public SearchTrackResultHandler(TrackRequestContext context) {
        this.context = context;
        this.playlist = null;
    }

    public AudioPlaylist search() throws TimeoutException {
        try {
            PlayerManager.getDefaultAudioManager()
                    .getPlayerManager()
                    .loadItem(context.getFullQuery(), this)
                    .get(5000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException ex) {
            Thread.currentThread().interrupt();
        }

        return playlist;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        LoggerService.info(getClass(), "trackLoaded: {}", track.getInfo().title);
        this.playlist = new BasicAudioPlaylist(track.getInfo().title, Collections.singletonList(track), null, true);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        LoggerService.info(getClass(), "playlistLoaded: {} - {}", playlist.getName(), playlist.getTracks().size());
        this.playlist = playlist;
    }

    @Override
    public void noMatches() {
        LoggerService.warn(getClass(), "noMatches");
        playlist = new BasicAudioPlaylist("No matches", Collections.emptyList(), null, true);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        LoggerService.error(getClass(), "loadFailed: {}", exception.getMessage());
    }
}
