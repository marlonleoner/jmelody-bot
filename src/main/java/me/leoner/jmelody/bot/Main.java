package me.leoner.jmelody.bot;

import me.leoner.jmelody.bot.config.DotenvConfig;

public class Main {
    public static void main(String[] args) {
        DotenvConfig.run();
        JMelody.run();
    }
}