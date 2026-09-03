package me.leoner.jmelody.command;

import me.leoner.jmelody.domain.LatencyMetrics;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

public record CommandContext(SlashCommandInteractionEvent event) {

    public Guild getGuild() {
        return event.getGuild();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public int getIntegerOption(String name, int defaultValue) {
        return Optional
                .ofNullable(event.getOption(name))
                .map(OptionMapping::getAsInt)
                .orElse(defaultValue);
    }

    public void reply(final String message, final ResponseVisibility visibility) {
        event.reply(message)
                .setEphemeral(ResponseVisibility.PRIVATE.equals(visibility))
                .queue();
    }

    public void replyPrivate(String message) {
        reply(message, ResponseVisibility.PRIVATE);
    }

    public void replyPublic(String message) {
        reply(message, ResponseVisibility.PUBLIC);
    }

    public LatencyMetrics calculateLatency() {
        var gatewayLatency = event.getJDA().getGatewayPing();

        var interactionLatency = System.currentTimeMillis() - (event.getTimeCreated().toEpochSecond() * 1000);

        return new LatencyMetrics(
                gatewayLatency,
                interactionLatency
        );
    }
}