package me.leoner.jmelody.bot.command;

import lombok.Getter;
import me.leoner.jmelody.bot.command.meme.MyInstantsCommand;
import me.leoner.jmelody.bot.command.music.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Getter
public enum CommandEnum {

    PLAY(PlayCommand.class, CategoryCommandEnum.MUSIC),
    STOP(StopCommand.class, CategoryCommandEnum.MUSIC),
    NEXT(NextCommand.class, CategoryCommandEnum.MUSIC),
    PAUSE(PauseCommand.class, CategoryCommandEnum.MUSIC),
    VOLUME(VolumeCommand.class, CategoryCommandEnum.MUSIC),
    BASS(BassCommand.class, CategoryCommandEnum.MUSIC),
    MY_INSTANTS(MyInstantsCommand.class, CategoryCommandEnum.OTHER);

    private final Class<?> type;

    private final CategoryCommandEnum category;

    CommandEnum(Class<?> type, CategoryCommandEnum category) {
        this.type = type;
        this.category = category;
    }

    public static List<AbstractCommand> getAllCommands() {
        return Arrays.stream(CommandEnum.values())
                .map(item -> {
                    try {
                        return (AbstractCommand) item.getType()
                                .getDeclaredConstructor()
                                .newInstance();
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
