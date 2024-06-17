package me.leoner.jmelody.bot.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;

public class EmbedGenerator {

    private static final int COLOR = 0xd4e300;

    private EmbedGenerator() {
        // empty constructor
    }

    private static EmbedBuilder createBase(boolean timestamp) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(COLOR);
        if (timestamp) {
            embed.setTimestamp(Instant.now());
        }

        return embed;
    }

    public static MessageEmbed withMessage(String message) {
        EmbedBuilder embed = EmbedGenerator.createBase(false);
        embed.setDescription(message);

        return embed.build();
    }

    public static MessageEmbed withErrorMessage(String message) {
        return EmbedGenerator.withMessage(":cross: " + message);
    }

}
