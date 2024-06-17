package me.leoner.jmelody.bot.player;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import me.leoner.jmelody.bot.JMelody;
import me.leoner.jmelody.bot.modal.RequestPlay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerManager {

    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);

    private static PlayerManager playerManager;

    private final AudioPlayerManager manager;

    private final Map<String, GuildPlayerManager> guilds = new HashMap<>();

    public PlayerManager() {
        this.manager = new DefaultAudioPlayerManager();

        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager();
        this.manager.registerSourceManager(youtube);

        SpotifySourceManager spotify = new SpotifySourceManager(null, System.getProperty("SPOTIFY_CLIENT_ID"), System.getProperty("SPOTIFY_CLIENT_SECRET"), System.getProperty("SPOTIFY_COUNTRY_CODE"), manager);
        this.manager.registerSourceManager(spotify);

        AudioSourceManagers.registerLocalSource(this.manager);
        AudioSourceManagers.registerRemoteSources(manager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
    }

    private GuildPlayerManager getPlayerManager(Guild guild) {
        String guildId = guild.getId();
        GuildPlayerManager guildPlayerManager = guilds.get(guildId);
        if (Objects.isNull(guildPlayerManager)) {
            guildPlayerManager = new GuildPlayerManager(this.manager);
            guilds.put(guildId, guildPlayerManager);
            guild.getAudioManager().setSendingHandler(guildPlayerManager.getHandler());
        }

        return guildPlayerManager;
    }

    public void play(RequestPlay request) {
        final GuildPlayerManager guildManager = this.getPlayerManager(request.getGuild());

        this.manager.loadItemOrdered(manager, request.getSong(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildManager.queue(track);

                JMelody.startPlayer(request.getGuild(), request.getMember());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    guildManager.queue(track);
                }

                JMelody.startPlayer(request.getGuild(), request.getMember());
            }

            @Override
            public void noMatches() {
                logger.info("noMatches");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error("noMatches: {}", exception.getMessage());
            }
        });
    }

    public void stop(SlashCommandInteractionEvent event) {
        this.getPlayerManager(event.getGuild()).stop();
    }

    public static PlayerManager getInstance() {
        if (Objects.isNull(playerManager)) {
            playerManager = new PlayerManager();
        }

        return playerManager;
    }
}
