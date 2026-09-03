package me.leoner.jmelody.command.impl;

import me.leoner.jmelody.command.CommandAbstract;
import me.leoner.jmelody.command.CommandContext;
import me.leoner.jmelody.exception.BaseException;

public class PingCommand extends CommandAbstract {

    public PingCommand() {
        super("ping", "Check if server is running");
    }

    @Override
    public void handle(CommandContext context) throws BaseException {
        context.replyPrivate("pong");
    }
}
