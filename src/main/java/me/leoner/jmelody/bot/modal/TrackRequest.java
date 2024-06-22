package me.leoner.jmelody.bot.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.leoner.jmelody.bot.command.CommandContext;

@AllArgsConstructor
@Data
public class TrackRequest {

    private CommandContext commandContext;

    private TrackRequestContext trackContext;
}
