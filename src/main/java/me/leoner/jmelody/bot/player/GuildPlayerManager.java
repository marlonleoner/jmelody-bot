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
import java.util.Objects;

public class GuildPlayerManager extends AudioEventAdapter {

    protected final Logger logger = LoggerFactory.getLogger(GuildPlayerManager.class);

    private final AudioPlayer player;

    @Getter
    private final AudioHandler handler;

    @Getter
    private final List<AudioTrack> tracks;

    private AudioTrack currentTrack;

    private final NowPlayingService nowPlayingService = new NowPlayingService();

    public GuildPlayerManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.player.setVolume(50);
        this.player.addListener(this);
        this.handler = new AudioHandler(this.player);
        this.tracks = new LinkedList<>();
        this.currentTrack = null;
    }

    private void verifyQueue() throws CommandException {
        if (tracks.isEmpty() && Objects.isNull(currentTrack))
            throw new CommandException("nothing is playing here", true);
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) this.tracks.add(track);

        this.player.setPaused(false);
    }

    public void stop() throws CommandException {
        verifyQueue();

        this.player.stopTrack();
        this.tracks.clear();
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

    private void nextTrack() throws CommandException {
        AudioTrack next = getNextTrack();
        if (Objects.isNull(next) && Objects.isNull(currentTrack))
            throw new CommandException("nothing to play here", true);

        this.player.startTrack(next, false);
        this.player.setPaused(false);
    }

    private AudioTrack getNextTrack() {
        if (tracks.isEmpty()) return null;

        return tracks.remove(0);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        logger.error("An error occurred when starting the track: {}", exception.getMessage());
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        logger.debug("Playing track {}", track.getInfo().title);
        RequestPlay request = (RequestPlay) track.getUserData();
        nowPlayingService.update(request, track);
        this.currentTrack = track;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        try {
            this.currentTrack = null;
            if (endReason.mayStartNext) {
                nextTrack();
            }
        } catch (Exception ex) {
            logger.error("An error occurred onTrackEnd {}: {}", track.getInfo().title, ex.getMessage());
        }
    }
}
