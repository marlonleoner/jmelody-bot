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
import me.leoner.jmelody.bot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerManager {

    private static PlayerManager INSTANCE;

    private final AudioPlayerManager playerManager;

    private final Map<String, GuildPlayerManager> guilds = new HashMap<>();

    public PlayerManager() {
        this.playerManager = new DefaultAudioPlayerManager();

        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager();
        this.playerManager.registerSourceManager(youtube);

        SpotifySourceManager spotify = new SpotifySourceManager(null, System.getProperty("SPOTIFY_CLIENT_ID"), System.getProperty("SPOTIFY_CLIENT_SECRET"), System.getProperty("SPOTIFY_COUNTRY_CODE"), playerManager);
        this.playerManager.registerSourceManager(spotify);

        AudioSourceManagers.registerLocalSource(this.playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
    }

    private GuildPlayerManager getPlayerManager(Guild guild) {
        String guildId = guild.getId();
        GuildPlayerManager guildPlayerManager = guilds.get(guildId);
        if (Objects.isNull(guildPlayerManager)) {
            guildPlayerManager = new GuildPlayerManager(this.playerManager);
            guilds.put(guildId, guildPlayerManager);
            guild.getAudioManager().setSendingHandler(guildPlayerManager.getHandler());
        }

        return guildPlayerManager;
    }

    public void play(SlashCommandInteractionEvent event) {
        final GuildPlayerManager manager = this.getPlayerManager(event.getGuild());
        String track = event.getOption("song").getAsString();
        this.playerManager.loadItemOrdered(manager, track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.queue(track);

                Bot.startPlayer(event.getGuild(), event.getMember());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    manager.queue(track);
                }

                Bot.startPlayer(event.getGuild(), event.getMember());
            }

            @Override
            public void noMatches() {
                System.out.println("noMatches");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                System.out.println("loadFailed");
            }
        });
    }

    public void stop(SlashCommandInteractionEvent event) {
        this.getPlayerManager(event.getGuild()).stop();
    }

    public static PlayerManager getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
