package me.leoner.jmelody.bot.button;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leoner.jmelody.bot.command.music.NextCommand;
import me.leoner.jmelody.bot.command.music.PauseCommand;
import me.leoner.jmelody.bot.command.music.StopCommand;
import me.leoner.jmelody.bot.command.music.VolumeCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.leoner.jmelody.bot.button.CategoryButtonInteractionEnum.*;

@AllArgsConstructor
@Getter
public enum ButtonInteractionEnum {

    NP_PREV(NOW_PLAYING, "stop-track", "<:djm_stop:1252671673459671080>", StopCommand.class),
    NP_PAUSE(NOW_PLAYING, "pause-track", "<:djm_pause:1252671903605461082>", PauseCommand.class),
    NP_NEXT(NOW_PLAYING, "next-track", "<:djm_skip:1252685712210399242>", NextCommand.class),
    NP_MUTED(NOW_PLAYING, "mute", "<:djm_muted:1252671812509110374>", VolumeCommand.class),
    NP_VOLUME_DOWN(NOW_PLAYING, "volume-down", "<:djm_vol_down:1252671699506434050>", VolumeCommand.class),
    NP_VOLUME_UP(NOW_PLAYING, "volume-up", "<:djm_vol_up:1252671753751232533>", VolumeCommand.class);

    private final CategoryButtonInteractionEnum category;

    private final String name;

    private final String emote;

    private final Class<?> commandType;

    public static ButtonInteractionEnum getByButtonId(String buttonId) {
        Optional<ButtonInteractionEnum> item = Arrays.stream(ButtonInteractionEnum.values())
                .filter(value -> value.getName().equals(buttonId))
                .findFirst();

        return item.orElse(null);
    }

    public static List<ButtonInteractionEnum> getByCategoryAndLine(CategoryButtonInteractionEnum category, Integer line) {
        List<ButtonInteractionEnum> allButtonsFromCategory = Arrays.stream(ButtonInteractionEnum.values())
                .filter(button -> button.getCategory().equals(category))
                .toList();

        return allButtonsFromCategory.subList((line - 1) * category.getItemsPerLine(), line * category.getItemsPerLine());
    }
}
