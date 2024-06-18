package me.leoner.jmelody.bot;

import me.leoner.jmelody.bot.command.CommandManager;
import me.leoner.jmelody.bot.config.DotenvConfig;
import me.leoner.jmelody.bot.config.RedisClient;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;

public class JMelody {

    private static JDA bot;

    private JMelody() {
        // empty constructor
    }

    public static void run() {
        JMelody.config();

        bot = JDABuilder.createDefault(System.getProperty("DISCORD_TOKEN"))
                .setActivity(Activity.listening("some music"))
                .addEventListeners(new CommandManager())
                .build();
    }

    private static void config() {
        DotenvConfig.run();
    }

    public static void startPlayer(Guild guild, Member user) {
        GuildVoiceState voiceState = user.getVoiceState();
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceState.getChannel());
    }

    public static void removeMessageFromChannel(String channelId, String messageId) {
        getInstance().getTextChannelById(channelId).deleteMessageById(messageId).queue();
    }

    public static JDA getInstance() {
        return bot;
    }
}
