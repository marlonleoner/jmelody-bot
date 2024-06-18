package me.leoner.jmelody.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.bot.command.music.NextCommand;
import me.leoner.jmelody.bot.command.music.PlayCommand;
import me.leoner.jmelody.bot.command.music.PrevCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum ButtonInteractionEnum {

    NP_PREV("now-playing", "prev-track", "<:jmelody_prev:1252303882156118066>", PrevCommand.class),
    NP_PAUSE("now-playing", "pause-track", "<:jmelody_play:1252303919833419939>", PlayCommand.class),
    NP_NEXT("now-playing", "next-track", "<:jmelody_next:1252303841483821087>", NextCommand.class);

    private final String prefix;

    private final String name;

    private final String emote;

    private final Class<?> commandType;

    public String getButtonId() {
        return this.prefix.concat("-").concat(this.name);
    }

    public static ButtonInteractionEnum getByButtonId(String buttonId) {
        Optional<ButtonInteractionEnum> item = Arrays.stream(ButtonInteractionEnum.values())
                .filter(value -> buttonId.equals(value.getButtonId()))
                .findFirst();

        return item.orElse(null);
    }

    public static List<ButtonInteractionEnum> getAllByPrefix(String prefix) {
        return Arrays.stream(ButtonInteractionEnum.values())
                .filter(value -> prefix.equals(value.getPrefix()))
                .toList();
    }
}
