package me.leoner.jmelody.bot.player;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import me.leoner.jmelody.bot.command.CommandException;
import me.leoner.jmelody.bot.config.RedisClient;
import me.leoner.jmelody.bot.modal.RequestPlay;
import me.leoner.jmelody.bot.player.handler.PlayAudioHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerManager {

    private static PlayerManager playerManager;

    private final AudioPlayerManager manager;

    private final Map<String, GuildPlayerManager> guilds = new HashMap<>();

    private final RedisClient redis = RedisClient.getClient();

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
            guildPlayerManager = createGuildManager(guildId);
            guilds.put(guildId, guildPlayerManager);
            guild.getAudioManager().setSendingHandler(guildPlayerManager.getHandler());
        }

        return guildPlayerManager;
    }

    private GuildPlayerManager createGuildManager(String guildId) {
        String key = "VOLUME:" + guildId;
        Integer currentVolume = redis.get(key, Integer.class);
        if (Objects.isNull(currentVolume)) {
            currentVolume = 50;
            redis.set(key, currentVolume);
        }

        GuildPlayerManager guildPlayerManager = new GuildPlayerManager(this.manager);
        guildPlayerManager.setVolume(currentVolume);

        return guildPlayerManager;
    }

    public void play(RequestPlay request) {
        final GuildPlayerManager guildManager = this.getPlayerManager(request.getGuild());
        this.manager.loadItemOrdered(guildManager, request.getSong(), new PlayAudioHandler(request, guildManager));
    }

    public boolean pause(Guild guild) {
        return this.getPlayerManager(guild).pause();
    }

    public void stop(SlashCommandInteractionEvent event) {
        this.getPlayerManager(event.getGuild()).stop();
    }

    public void prev(Guild guild) {
        this.getPlayerManager(guild).prev();
    }

    public void next(Guild guild) throws CommandException {
        this.getPlayerManager(guild).next();
    }

    public Integer getVolume(Guild guild) {
        return this.getPlayerManager(guild).getVolume();
    }

    public void setVolume(Guild guild, Integer volume) {
        this.getPlayerManager(guild).setVolume(volume);
        redis.set("VOLUME:" + guild.getId(), volume);
    }

    public static PlayerManager getInstance() {
        if (Objects.isNull(playerManager)) {
            playerManager = new PlayerManager();
        }

        return playerManager;
    }
}
