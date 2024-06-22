package me.leoner.jmelody.bot.exception;

import lombok.Getter;

@Getter
public class CommandException extends BaseException {

    private final Boolean ephemeral;

    public CommandException(String message) {
        this(message, true);
    }

    public CommandException(String message, Boolean ephemeral) {
        super(message);
        this.ephemeral = ephemeral;
    }
}
