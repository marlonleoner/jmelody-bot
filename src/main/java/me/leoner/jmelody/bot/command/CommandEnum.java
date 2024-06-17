package me.leoner.jmelody.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.bot.command.music.PlayCommand;
import me.leoner.jmelody.bot.command.music.StopCommand;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum CommandEnum {

    PLAY("play", "Play a track", PlayCommand.class),
    STOP("stop", "Stop the player and clear the queue", StopCommand.class),

    ;

    private final String name;

    private final String description;

    private final Class<?> type;

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
