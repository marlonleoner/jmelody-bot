package me.leoner.jmelody.bot.player;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import lombok.Getter;
import me.leoner.jmelody.bot.command.CommandException;
import me.leoner.jmelody.bot.config.ApplicationContext;
import me.leoner.jmelody.bot.modal.RequestPlay;
import me.leoner.jmelody.bot.player.handler.PlayAudioHandler;
import me.leoner.jmelody.bot.service.LoggerService;
import me.leoner.jmelody.bot.service.RedisClient;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerManager {

    @Getter
    private static final PlayerManager instance = new PlayerManager();

    private final AudioPlayerManager audioManager;

    private final Map<String, GuildPlayerManager> guilds = new HashMap<>();

    private final RedisClient redis = RedisClient.getClient();

    private PlayerManager() {
        ApplicationContext context = ApplicationContext.getContext();

        audioManager = new DefaultAudioPlayerManager();

        audioManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioManager.registerSourceManager(new SpotifySourceManager(
                null,
                context.getSpotifyClientId(),
                context.getSpotifyClientSecret(),
                context.getSpotifyCountryCode(),
                audioManager
        ));

        AudioSourceManagers.registerLocalSource(audioManager);
        AudioSourceManagers.registerRemoteSources(audioManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);

        LoggerService.info(getClass(), "PlayerManager loaded!");
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

        GuildPlayerManager guildPlayerManager = new GuildPlayerManager(audioManager);
        guildPlayerManager.setVolume(currentVolume);

        return guildPlayerManager;
    }

    public void play(RequestPlay request) {
        final GuildPlayerManager guildManager = getPlayerManager(request.getGuild());
        audioManager.loadItemOrdered(guildManager, request.getSong(), new PlayAudioHandler(request, guildManager));
    }

    public boolean pause(Guild guild) {
        return getPlayerManager(guild).pause();
    }

    public void stop(Guild guild) throws CommandException {
        getPlayerManager(guild).stop();
    }

    public void next(Guild guild) throws CommandException {
        getPlayerManager(guild).next();
    }

    public Integer getVolume(Guild guild) {
        return getPlayerManager(guild).getVolume();
    }

    public void setVolume(Guild guild, Integer volume) {
        getPlayerManager(guild).setVolume(volume);
        redis.set("VOLUME:" + guild.getId(), volume);
    }
}
