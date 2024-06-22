package me.leoner.jmelody.bot.audio;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ConnectionVoiceChannelEnum {

    /**
     * The bot was connected to the voice channel successfully.
     */
    CONNECTED(true),

    /**
     * The user that ran the music command is not in a valid voice channel.
     */
    NOT_CONNECTED(false,"You have to be connected to a voice channel."),

    /**
     * There is a user limit on the voice channel the user is currently
     * in and the bot doesn't have permission to bypass it.
     */
    USER_LIMIT(false,"Unable to connect to the voice channel you're in due to user limit!"),

    /**
     * The bot doesn't have permissions to connect to the users current voice channel.
     */
    MISSING_PERMISSIONS(false,"Unable to connect to the voice channel you're in due to missing permissions!"),

    /**
     * Lavalink is used with the bot, but there are no available music nodes to stream to(No connected nodes or no nodes added).
     */
    NO_AVAILABLE_NODES(false,"There are currently no available music nodes to stream to, try again later.");

    private final boolean success;

    private final String message;

    private ConnectionVoiceChannelEnum(boolean success) {
        this(success, null);
    }
}
