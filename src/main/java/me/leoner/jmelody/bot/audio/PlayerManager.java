package me.leoner.jmelody.bot.audio;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.config.ApplicationContext;
import me.leoner.jmelody.bot.exception.ConnectionVoiceChannelException;
import me.leoner.jmelody.bot.exception.NoMatchFoundException;
import me.leoner.jmelody.bot.exception.TrackRequestException;
import me.leoner.jmelody.bot.manager.GuildPlayerManager;
import me.leoner.jmelody.bot.modal.TrackRequest;
import me.leoner.jmelody.bot.service.LoggerService;
import me.leoner.jmelody.bot.service.RedisService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerManager {

    private static final PlayerManager DEFAULT_AUDIO_MANAGER = new PlayerManager();

    private final Map<String, GuildPlayerManager> guildsManager = new HashMap<>();

    private AudioPlayerManager audioPlayerManager;

    private final RedisService redis = RedisService.getClient();

    public static PlayerManager getDefaultAudioManager() {
        return DEFAULT_AUDIO_MANAGER;
    }

    @SuppressWarnings("deprecation")
    public AudioPlayerManager getPlayerManager() {
        if (Objects.isNull(audioPlayerManager)) {
            ApplicationContext context = ApplicationContext.getContext();

            audioPlayerManager = new DefaultAudioPlayerManager();
            audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
            audioPlayerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
            audioPlayerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
            audioPlayerManager.setFrameBufferDuration(1000);
            audioPlayerManager.setItemLoaderThreadPoolSize(500);

            audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
            audioPlayerManager.registerSourceManager(new SpotifySourceManager(null, context.getSpotifyClientId(), context.getSpotifyClientSecret(), context.getSpotifyCountryCode(), audioPlayerManager));

            AudioSourceManagers.registerLocalSource(audioPlayerManager);
            AudioSourceManagers.registerRemoteSources(audioPlayerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);

            LoggerService.info(getClass(), "PlayerManager loaded!");
        }

        return audioPlayerManager;
    }

    public static GuildPlayerManager getGuildPlayerManager(Guild guild) {
        PlayerManager instance = getDefaultAudioManager();
        String guildId = guild.getId();
        GuildPlayerManager manager = instance.guildsManager.get(guildId);
        if (Objects.isNull(manager)) {
            manager = instance.createGuildManager(guildId);
            guild.getAudioManager().setSendingHandler(manager.getAudioHandler());
        }

        return manager;
    }

    private GuildPlayerManager createGuildManager(String guildId) {
        String key = "VOLUME:" + guildId;
        Integer currentVolume = redis.get(key, Integer.class);
        if (Objects.isNull(currentVolume)) {
            currentVolume = 60;
            redis.set(key, currentVolume);
        }

        GuildPlayerManager manager = new GuildPlayerManager(getPlayerManager());
        manager.setVolume(currentVolume);
        guildsManager.put(guildId, manager);

        return manager;
    }

    public String loadAndPlay(TrackRequest request) throws TrackRequestException {
        try {
            AudioPlaylist playlist = new SearchTrackResultHandler(request.getTrackContext()).search();
            if (Objects.isNull(playlist.getTracks()) || playlist.getTracks().isEmpty()) {
                String query = request.getTrackContext().getQuery();
                throw new NoMatchFoundException("nothing was found with the given query " + query);
            }

            if (!request.getTrackContext().isSearch() && playlist.getTracks().size() > 1) {
                play(playlist, request);
                return "`" + playlist.getName() + " [with " + playlist.getTracks().size() + " tracks]`";
            }

            AudioTrack track = playlist.getTracks().get(0);
            play(track, request);
            AudioTrackInfo infos = track.getInfo();
            return "[`" + infos.title + "`](" + infos.uri + ")";
        } catch (Exception ex) {
            throw new TrackRequestException(ex.getMessage());
        }
    }

    private void play(AudioPlaylist playlist, TrackRequest request) throws ConnectionVoiceChannelException {
        CommandContext context = request.getCommandContext();
        ConnectionVoiceChannelEnum voiceConnection = connectToVoiceChannel(context.getGuild(), context.getMember());
        if (!voiceConnection.isSuccess()) {
            throw new ConnectionVoiceChannelException(voiceConnection.getMessage());
        }

        final GuildPlayerManager guildManager = getGuildPlayerManager(context.getGuild());
        for (AudioTrack track : playlist.getTracks()) {
            guildManager.queue(track, context);
        }
    }

    private void play(AudioTrack track, TrackRequest request) throws ConnectionVoiceChannelException {
        CommandContext context = request.getCommandContext();
        ConnectionVoiceChannelEnum voiceConnection = connectToVoiceChannel(context.getGuild(), context.getMember());
        if (!voiceConnection.isSuccess()) {
            throw new ConnectionVoiceChannelException(voiceConnection.getMessage());
        }

        final GuildPlayerManager guildManager = getGuildPlayerManager(context.getGuild());
        guildManager.queue(track, context);
    }

    private ConnectionVoiceChannelEnum connectToVoiceChannel(Guild guild, Member member) {
        GuildVoiceState voiceState = member.getVoiceState();
        if (Objects.isNull(voiceState)) {
            return ConnectionVoiceChannelEnum.NOT_CONNECTED;
        }

        AudioChannel channel = voiceState.getChannel();
        if (Objects.isNull(channel)) {
            return ConnectionVoiceChannelEnum.NOT_CONNECTED;
        }

        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            return ConnectionVoiceChannelEnum.CONNECTED;
        }

        ConnectionVoiceChannelEnum voiceConnectStatus = canConnectToVoiceChannel(channel);
        if (Objects.nonNull(voiceConnectStatus)) {
            return voiceConnectStatus;
        }

        try {
            audioManager.openAudioConnection(channel);
        } catch (Exception ex) {
            return ConnectionVoiceChannelEnum.USER_LIMIT;
        }

        return ConnectionVoiceChannelEnum.CONNECTED;
    }

    private ConnectionVoiceChannelEnum canConnectToVoiceChannel(AudioChannel channel) {
        if (channel.getUserLimit() > 0 && channel.getUserLimit() <= channel.getMembers().size()) {
            return ConnectionVoiceChannelEnum.USER_LIMIT;
        }

        return null;
    }
}
