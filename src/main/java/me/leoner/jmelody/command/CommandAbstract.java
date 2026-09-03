package me.leoner.jmelody.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.exception.BaseException;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class CommandAbstract {

    private final String name;
    private final String description;

    public List<OptionData> getOptions() {
        return List.of();
    }

    public boolean hasOption() {
        return !getOptions().isEmpty();
    }

    public abstract void handle(CommandContext context) throws BaseException;

    @Override
    public String toString() {
        return String.format("<%s: %s />", name, description);
    }
}
