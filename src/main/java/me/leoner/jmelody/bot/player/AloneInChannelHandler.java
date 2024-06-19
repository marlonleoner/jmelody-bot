package me.leoner.jmelody.bot.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.JMelody;
import me.leoner.jmelody.bot.config.ApplicationContext;
import me.leoner.jmelody.bot.service.LoggerService;
import me.leoner.jmelody.bot.service.RedisClient;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AloneInChannelHandler {

    public static void load() {
        ApplicationContext context = ApplicationContext.getContext();
        context.getScheduler().scheduleWithFixedDelay(AloneInChannelHandler::check, 10, 20, TimeUnit.SECONDS);
    }

    private static void check() {
        List<String> guilds = getAllGuilds();
        LoggerService.debug(AloneInChannelHandler.class, "Total guilds: {}", guilds.size());
        for (String guildId : guilds) {
            Guild guild = JMelody.getInstance().getGuildById(guildId);
            if (Objects.isNull(guild))
                continue;

            LoggerService.debug(AloneInChannelHandler.class, "Validating guild '{}'", guild.getName());
            if (isAlone(guild)) {
                JMelody.disconnectFromChannel(guild, null);
                LoggerService.info(AloneInChannelHandler.class, "Bot is alone on guild '{}'", guild.getName());
            }
        }
    }

    private static List<String> getAllGuilds() {
        RedisClient redis = RedisClient.getClient();
        return redis.getKeys("NOW_PLAYING:*")
                .stream()
                .map(key -> key.split(":")[1])
                .toList();
    }

    private static boolean isAlone(Guild guild) {
        if (Objects.isNull(guild) || Objects.isNull(guild.getAudioManager().getConnectedChannel())) return false;

        return guild.getAudioManager()
                .getConnectedChannel()
                .getMembers()
                .stream()
                .noneMatch(x -> !x.getVoiceState().isDeafened() && !x.getUser().isBot());
    }
}
