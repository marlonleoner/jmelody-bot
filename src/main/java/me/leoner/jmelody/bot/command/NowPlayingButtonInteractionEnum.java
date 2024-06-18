package me.leoner.jmelody.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.bot.command.music.NextCommand;
import me.leoner.jmelody.bot.command.music.PauseCommand;
import me.leoner.jmelody.bot.command.music.PrevCommand;
import me.leoner.jmelody.bot.command.music.VolumeCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum NowPlayingButtonInteractionEnum {

    NP_PREV("prev-track", "<:djm_next:1252433069600935987>", PrevCommand.class),
    NP_PAUSE("pause-track", "<:djm_play:1252432761931956315>", PauseCommand.class),
    NP_NEXT("next-track", "<:djm_next:1252433069600935987>", NextCommand.class),
    NP_MUTED("mute", "<:djm_muted:1252434332845604945>", VolumeCommand.class),
    NP_VOLUME_DOWN("volume-down", "<:djm_volume_down:1252434361983303793>", VolumeCommand.class),
    NP_VOLUME_UP("volume-up", "<:djm_volume_up:1252434285609222225>", VolumeCommand.class),
    ;

    private final String name;

    private final String emote;

    private final Class<?> commandType;

    public static NowPlayingButtonInteractionEnum getByButtonId(String buttonId) {
        Optional<NowPlayingButtonInteractionEnum> item = Arrays.stream(NowPlayingButtonInteractionEnum.values())
                .filter(value -> value.getName().equals(buttonId))
                .findFirst();

        return item.orElse(null);
    }

    public static List<NowPlayingButtonInteractionEnum> getByLine(Integer line) {
        return Arrays.stream(NowPlayingButtonInteractionEnum.values())
                .toList()
                .subList((line - 1) * 3, line * 3);
    }
}
