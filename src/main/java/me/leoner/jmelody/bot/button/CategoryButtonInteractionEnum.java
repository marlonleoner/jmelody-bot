package me.leoner.jmelody.bot.button;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CategoryButtonInteractionEnum {

    NOW_PLAYING(1, "NOW_PLAYING", 3);

    private final Integer id;

    @Getter
    private final String name;

    @Getter
    private final Integer itemsPerLine;
}
