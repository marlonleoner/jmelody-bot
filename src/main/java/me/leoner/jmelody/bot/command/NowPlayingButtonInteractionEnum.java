package me.leoner.jmelody.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.bot.command.music.NextCommand;
import me.leoner.jmelody.bot.command.music.PauseCommand;
import me.leoner.jmelody.bot.command.music.StopCommand;
import me.leoner.jmelody.bot.command.music.VolumeCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum NowPlayingButtonInteractionEnum {

    NP_PREV("stop-track", "<:djm_stop:1252671673459671080>", StopCommand.class),
    NP_PAUSE("pause-track", "<:djm_pause:1252671903605461082>", PauseCommand.class),
    NP_NEXT("next-track", "<:djm_skip:1252685712210399242>", NextCommand.class),
    NP_MUTED("mute", "<:djm_muted:1252671812509110374>", VolumeCommand.class),
    NP_VOLUME_DOWN("volume-down", "<:djm_vol_down:1252671699506434050>", VolumeCommand.class),
    NP_VOLUME_UP("volume-up", "<:djm_vol_up:1252671753751232533>", VolumeCommand.class),
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
