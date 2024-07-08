package me.leoner.jmelody.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.leoner.jmelody.bot.manager.GuildPlayerManager;
import me.leoner.jmelody.bot.service.LoggerService;
import me.leoner.jmelody.bot.service.NowPlayingService;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class TrackManager extends AudioEventAdapter {

    private final GuildPlayerManager guildManager;

    /**
     * The music queue, the queue holds all the audio
     * tracks that has been requested by users.
     */
    protected final BlockingDeque<AudioTrack> queue;

    public TrackManager(GuildPlayerManager guildPlayerManager) {
        this.guildManager = guildPlayerManager;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track) {
        queue.offer(track);
    }

    public AudioTrack next() {
        return queue.poll();
    }

    public void clear() {
        queue.clear();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
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
            guildManager.nextTrack();
        } else if (endReason.equals(AudioTrackEndReason.FINISHED) && queue.isEmpty()) {
            LoggerService.info(getClass(), "Aqui?? {}", endReason);
//            if (manager.getLastActiveMessage() != null) {
//                service.submit(() -> handleEndOfQueueWithLastActiveMessage(true));
//            }
        }
    }
}
