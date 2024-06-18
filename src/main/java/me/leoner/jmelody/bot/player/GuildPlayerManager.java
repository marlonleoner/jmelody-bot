package me.leoner.jmelody.bot.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import me.leoner.jmelody.bot.command.CommandException;
import me.leoner.jmelody.bot.modal.RequestPlay;
import me.leoner.jmelody.bot.service.NowPlayingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class GuildPlayerManager extends AudioEventAdapter {

    protected final Logger logger = LoggerFactory.getLogger(GuildPlayerManager.class);

    private final AudioPlayer player;

    @Getter
    private final AudioHandler handler;

    @Getter
    private final List<AudioTrack> tracks;

    private final NowPlayingService nowPlayingService = new NowPlayingService();

    public GuildPlayerManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.player.setVolume(25);
        this.player.addListener(this);
        this.handler = new AudioHandler(this.player);
        this.tracks = new LinkedList<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.tracks.add(track);
        }
        this.player.setPaused(false);
    }

    public void stop() {
        this.player.stopTrack();
        this.tracks.clear();
    }

    public void prev() {
        // TODO: how to get the previous track?
    }

    public void next() throws CommandException {
        this.nextTrack();
    }

    public boolean pause() {
        boolean state = !this.player.isPaused();
        this.player.setPaused(state);
        return state;
    }

    public Integer getVolume() {
        return this.player.getVolume();
    }

    public void setVolume(Integer volume) {
        this.player.setVolume(volume);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        logger.error("An error occurred when starting the track {}: {}", track.getInfo().title, exception.getMessage());
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        logger.debug("Playing track {}", track.getInfo().title);
        RequestPlay request = (RequestPlay) track.getUserData();
        nowPlayingService.update(request, track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        try {
            if (endReason.mayStartNext) {
                nextTrack();
            }
        } catch (Exception ex) {
            logger.error("An error occurred onTrackEnd {}: {}", track.getInfo().title, ex.getMessage());
        }
    }

    private void nextTrack() throws CommandException {
        if (tracks.isEmpty()) {
            this.player.stopTrack();
            throw new CommandException("nothing is playing here", true);
        }

        AudioTrack next = tracks.remove(0);
        this.player.startTrack(next, false);
        this.player.setPaused(false);
    }
}
