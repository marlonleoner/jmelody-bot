package me.leoner.jmelody.exception;

public class NothingPlayingException extends BaseException {

    public NothingPlayingException() {
        super("nothing is playing right now, request music first with `/play`", true);
    }

    public NothingPlayingException(String message) {
        super(message, true);
    }
}
