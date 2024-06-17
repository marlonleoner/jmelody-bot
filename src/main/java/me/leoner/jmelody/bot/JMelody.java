package me.leoner.jmelody.bot;

import me.leoner.jmelody.bot.command.CommandManager;
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
        bot = JDABuilder.createDefault(System.getProperty("DISCORD_TOKEN"))
                .setActivity(Activity.listening("some music"))
                .addEventListeners(new CommandManager())
                .build();
    }

    public static void startPlayer(Guild guild, Member user) {
        GuildVoiceState voiceState = user.getVoiceState();
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceState.getChannel());
    }

    public static JDA getInstance() {
        return bot;
    }
}
