package me.leoner.jmelody.service;

import lombok.NoArgsConstructor;
import me.leoner.jmelody.domain.LatencyMetrics;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
import java.time.Instant;

@NoArgsConstructor
public class LatencyMetricsService {

    public static LatencyMetrics calculate(final SlashCommandInteractionEvent event,
                                           final long processStart) {
        var websocketLatency = event.getJDA().getGatewayPing();

        var messageLatency = Duration
                .between(event.getTimeCreated(), Instant.now())
                .toMillis();

        var processLatency = System.nanoTime() - processStart;

        return new LatencyMetrics(
                websocketLatency,
                messageLatency,
                processLatency / 1_000_000
        );
    }
}
