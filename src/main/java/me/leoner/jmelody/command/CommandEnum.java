package me.leoner.jmelody.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.command.impl.PingCommand;
import me.leoner.jmelody.command.impl.moderation.ClearCommand;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum CommandEnum {
    PING(new PingCommand()),
    CLEAR(new ClearCommand()),
    ;

    private final CommandAbstract command;

    public boolean check(final String name) {
        return this.command.getName().equalsIgnoreCase(name);
    }

    public static CommandAbstract get(final String name) {
        return Arrays.stream(CommandEnum.values())
                .filter(command -> command.check(name))
                .map(CommandEnum::getCommand)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such command: " + name));
    }
}
