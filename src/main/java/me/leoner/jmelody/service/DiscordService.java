package me.leoner.jmelody.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Optional;

public final class DiscordService {

    private final JDA jda;

    public DiscordService(JDA jda) {
        this.jda = jda;
    }

    public JDA getJda() {
        return jda;
    }

    public Guild getGuildOrException(long guildId) {
        return Optional
                .ofNullable(jda.getGuildById(guildId))
                .orElseThrow(() -> new IllegalArgumentException("<Guild: %s /> not found"));
    }

    public TextChannel getTextChannelOrException(long guildId, long channelId) {
        var guild = getGuildOrException(guildId);

        return Optional
                .ofNullable(guild.getTextChannelById(channelId))
                .orElseThrow(() -> new IllegalArgumentException("<TextChannel: %s /> not found"));
    }

    public void deleteMessage(long guildId, long channelId, long messageId) {
        var channel = getTextChannelOrException(guildId, channelId);

        channel.deleteMessageById(messageId).queue();
    }
}