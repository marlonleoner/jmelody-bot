package me.leoner.jmelody.bot.manager;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import me.leoner.jmelody.bot.audio.AudioHandler;
import me.leoner.jmelody.bot.audio.TrackManager;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.NothingPlayingException;

import java.util.Objects;

public class GuildPlayerManager {

    private static final float[] BASS_BOOST = {0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f};

    /**
     * The player, this is used to communicate to the audio
     * player when using either Lavalink, or Lavaplayer.
     */
    private final AudioPlayer player;

    /**
     * Equalizer
     */
    private final EqualizerFactory equalizer;

    /**
     *
     */
    @Getter
    private final AudioHandler audioHandler;

    /**
     *
     */
    private final TrackManager trackManager;

    public GuildPlayerManager(AudioPlayerManager manager) {
        this.trackManager = new TrackManager(this);

        this.equalizer = new EqualizerFactory();
        this.setBass(10);

        this.player = manager.createPlayer();
        this.player.setVolume(20);
        this.player.addListener(trackManager);
        this.player.setFilterFactory(this.equalizer);

        this.audioHandler = new AudioHandler(this.player);
    }

    public void setBass(Integer bass) {
        float value = Math.max(Math.min(bass, 100), 0) / 100.0f;
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, BASS_BOOST[i] * value);
        }
    }

    public void queue(AudioTrack track, CommandContext context) {
        track.setUserData(context);
        if (Objects.nonNull(player.getPlayingTrack())) {
            trackManager.queue(track);
            return;
        }

        player.playTrack(track);
    }

    public void stop() throws NothingPlayingException {
        if (Objects.isNull(player.getPlayingTrack())) {
            throw new NothingPlayingException();
        }

        player.stopTrack();
        trackManager.clear();

        nextTrack();
    }

    public void next() throws NothingPlayingException {
        if (Objects.isNull(player.getPlayingTrack())) {
            throw new NothingPlayingException();
        }

        if (trackManager.isEmpty()) {
            player.stopTrack();
            return;
        }

        nextTrack();
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

    public void nextTrack() {
        player.playTrack(trackManager.next());
    }
}
