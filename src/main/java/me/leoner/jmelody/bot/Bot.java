package me.leoner.jmelody.bot;

import me.leoner.jmelody.bot.command.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Bot {

    private static JDA bot;

    public static void run() {
        bot = JDABuilder.createDefault(System.getProperty("DISCORD_TOKEN"))
                .setActivity(Activity.listening("some music"))
                .addEventListeners(new CommandManager())
                .build();
    }

    public static JDA getInstance() {
        return bot;
    }
}
