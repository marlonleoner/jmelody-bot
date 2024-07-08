package me.leoner.jmelody.bot.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.command.CommandContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndQueueService {

    @Getter(AccessLevel.PRIVATE)
    private static final EndQueueService instance = new EndQueueService();

    public static void update(AudioTrack track) {
        EndQueueService service = getInstance();

        CommandContext context = track.getUserData(CommandContext.class);
        context.getTextChannel()
                .sendMessageEmbeds(EmbedFactory.withSuccessMessage("Acabou a fila?"))
                .queue();
    }
}
