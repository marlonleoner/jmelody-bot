package me.leoner.jmelody.bot.command;

import lombok.Getter;

@Getter
public class CommandException extends Exception {

    private final Boolean ephemeral;

    public CommandException(String message, Boolean ephemeral) {
        super(message);
        this.ephemeral = ephemeral;
    }
}
