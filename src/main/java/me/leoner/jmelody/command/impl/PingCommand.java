package me.leoner.jmelody.command.impl;

import me.leoner.jmelody.command.CommandAbstract;
import me.leoner.jmelody.command.CommandContext;
import me.leoner.jmelody.exception.BaseException;
import me.leoner.jmelody.service.LatencyMetricsService;

public class PingCommand extends CommandAbstract {

    private static final String MESSAGE = """
            🏓 Pong!
            
            WebSocket: %d ms
            Message:   %d ms
            Process:   %d ms
            """;

    public PingCommand() {
        super("ping", "Check if server is running");
    }

    @Override
    public void handle(CommandContext context) throws BaseException {
        var latency = LatencyMetricsService.calculate(context.event());

        context.replyPrivate(MESSAGE.formatted(
                latency.websocket(),
                latency.message(),
                latency.process()
        ));
    }
}
