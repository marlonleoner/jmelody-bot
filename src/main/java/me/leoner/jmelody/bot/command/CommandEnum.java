package me.leoner.jmelody.bot.command;

import lombok.Getter;
import me.leoner.jmelody.bot.command.music.*;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Getter
public enum CommandEnum {

    PLAY("play", "Play a track", PlayCommand.class),
    STOP("stop", "Stop the player and clear the queue", StopCommand.class),
    NEXT("next", "Skip to the previous track", NextCommand.class),
    PAUSE("pause", "Pause the player", PauseCommand.class),
    VOLUME("volume", "Change or view the volume of player", VolumeCommand.class),
    ;

    private final String name;

    private final String description;

    private final Class<?> type;

    private final List<OptionData> options;

    CommandEnum(String name, String description, Class<?> type, OptionData... options) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.options = Arrays.asList(options);
    }

    public static List<AbstractCommand> getAllCommands() {
        return Arrays.stream(CommandEnum.values())
                .map(item -> {
                    try {
                        return (AbstractCommand) item.getType()
                                .getDeclaredConstructor(String.class, String.class)
                                .newInstance(item.getName(), item.getDescription());
                    } catch (InstantiationException
                             | IllegalAccessException
                             | InvocationTargetException
                             | NoSuchMethodException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .toList();
    }
}
