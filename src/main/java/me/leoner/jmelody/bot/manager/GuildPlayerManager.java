package me.leoner.jmelody.bot.manager;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import me.leoner.jmelody.bot.audio.AudioHandler;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.NothingPlayingException;
import me.leoner.jmelody.bot.service.LoggerService;
import me.leoner.jmelody.bot.service.NowPlayingService;

import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class GuildPlayerManager extends AudioEventAdapter {

    private static final float[] BASS_BOOST = {0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f};

    /**
     * The player, this is used to communicate to
     * the audio player when using either Lavalink, or Lavaplayer.
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
     * The music queue, the queue holds all the audio
     * tracks that has been requested by users.
     */
    @Getter
    protected final BlockingDeque<AudioTrack> queue;

    public GuildPlayerManager(AudioPlayerManager manager) {
        this.equalizer = new EqualizerFactory();
        this.setBass(10);

        this.player = manager.createPlayer();
        this.player.setVolume(20);
        this.player.addListener(this);
        this.player.setFilterFactory(this.equalizer);

        this.audioHandler = new AudioHandler(this.player);

        this.queue = new LinkedBlockingDeque<>();
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
            boolean success = queue.offer(track);
            return;
        }

        player.playTrack(track);
    }

    public void stop() throws NothingPlayingException {
        if (Objects.isNull(player.getPlayingTrack())) {
            throw new NothingPlayingException();
        }

        player.stopTrack();
        queue.clear();

        nextTrack();
    }

    public void next() throws NothingPlayingException {
        if (Objects.isNull(player.getPlayingTrack())) {
            throw new NothingPlayingException();
        }

        if (queue.isEmpty()) {
            player.stopTrack();
            // Notify EndOfQueue
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

    private void nextTrack() {
        AudioTrack track = queue.poll();

        player.playTrack(track);
//        NowPlayingService
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LoggerService.error(getClass(), "An error occurred when starting the track: {}", exception.getMessage());
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        LoggerService.debug(getClass(), "Name: {} - URL: {}", track.getInfo().title, track.getInfo().uri);
        NowPlayingService.update(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        } else if (endReason.equals(AudioTrackEndReason.FINISHED) && queue.isEmpty()) {
//            if (manager.getLastActiveMessage() != null) {
//                service.submit(() -> handleEndOfQueueWithLastActiveMessage(true));
//            }
        }
    }
}
