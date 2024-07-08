package me.leoner.jmelody.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.audio.AloneInChannelHandler;
import me.leoner.jmelody.bot.command.CommandManager;
import me.leoner.jmelody.bot.config.ApplicationContext;
import me.leoner.jmelody.bot.config.BotConfig;
import me.leoner.jmelody.bot.config.RedisConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.SessionControllerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JMelody {

    private static JDA bot;

    public static void run() {
        JMelody.config();

        ApplicationContext context = ApplicationContext.getContext();
        bot = JDABuilder.createDefault(context.getToken())
                .setActivity(Activity.listening("some music"))
                .addEventListeners(new CommandManager())
                .setSessionController(new SessionControllerAdapter())
                .setBulkDeleteSplittingEnabled(false)
                .setChunkingFilter(ChunkingFilter.NONE)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                .setEnableShutdownHook(true)
                .setAutoReconnect(true)
                .setContextEnabled(true)
                .build();
    }

    private static void config() {
        BotConfig.load();
        RedisConfig.load();
        AloneInChannelHandler.load();
    }

    public static void startPlayer(Guild guild, Member user) {
        GuildVoiceState voiceState = user.getVoiceState();
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceState.getChannel());
    }

    public static void removeMessageFromChannel(String channelId, String messageId) {
        TextChannel channel = getInstance().getTextChannelById(channelId);
        if (Objects.isNull(channel)) return;

        channel.deleteMessageById(messageId).queue();
    }

    public static void disconnectFromChannel(Guild guild) {
        ApplicationContext context = ApplicationContext.getContext();
        context.getScheduler().submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    public static JDA getInstance() {
        return bot;
    }
}
