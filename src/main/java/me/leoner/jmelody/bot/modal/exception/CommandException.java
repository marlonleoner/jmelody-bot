package me.leoner.jmelody.bot.modal.exception;

import lombok.Getter;

@Getter
public class CommandException extends Exception {

    private final Boolean ephemeral;

    public CommandException(String message) {
        this(message, true);
    }

    public CommandException(String message, Boolean ephemeral) {
        super(message);
        this.ephemeral = ephemeral;
    }
}
