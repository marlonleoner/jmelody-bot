package me.leoner.jmelody.bot.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
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

    public GuildPlayerManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.player.setVolume(10);
        this.player.addListener(this);
        this.handler = new AudioHandler(this.player);
        this.tracks = new LinkedList<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.tracks.add(track);
        }
    }

    public void stop() {
        this.player.stopTrack();
        this.tracks.clear();
    }

    public void prev() {
        // TODO: how to get the previous track?
    }

    public void next() {
        this.nextTrack();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        logger.error("An error occurred when starting the track {}: {}", track.getInfo().title, exception.getMessage());
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        logger.debug("Playing track {}", track.getInfo().title);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    private void nextTrack() {
        if (tracks.isEmpty()) {
            this.player.stopTrack();
            return;
        }

        AudioTrack next = tracks.remove(0);
        this.player.startTrack(next, false);
    }
}
