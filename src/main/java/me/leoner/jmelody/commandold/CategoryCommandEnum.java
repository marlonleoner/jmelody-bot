package me.leoner.jmelody.commandold;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CategoryCommandEnum {

    MUSIC(1, "MUSIC"),
    OTHER(2, "OTHER");

    private final Integer id;

    @Getter
    private final String name;
}
