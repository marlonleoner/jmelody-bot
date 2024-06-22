package me.leoner.jmelody.bot.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class BaseException extends Exception {

    private final String message;

    private final boolean ephemeral;

    protected BaseException(String message) {
        this.message = message;
        this.ephemeral = true;
    }
}
