package me.leoner.jmelody.command.impl;

import me.leoner.jmelody.command.CommandAbstract;
import me.leoner.jmelody.command.CommandContext;
import me.leoner.jmelody.exception.BaseException;

public class PingCommand extends CommandAbstract {

    private static final String MESSAGE = """
            🏓 Pong!
            
            Gateway: %d ms
            Interaction: %d ms
            """;

    public PingCommand() {
        super("ping", "Check if server is running");
    }

    @Override
    public void handle(CommandContext context) throws BaseException {
        var latency = context.calculateLatency();

        context.replyPrivate(MESSAGE.formatted(latency.gateway(), latency.interaction()));
    }
}
