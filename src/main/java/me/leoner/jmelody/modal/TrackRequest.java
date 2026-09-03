package me.leoner.jmelody.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.leoner.jmelody.commandold.CommandContext;

@AllArgsConstructor
@Data
public class TrackRequest {

    private CommandContext commandContext;

    private TrackRequestContext trackContext;
}
