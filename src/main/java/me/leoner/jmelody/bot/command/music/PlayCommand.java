package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.audio.PlayerManager;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.BaseException;
import me.leoner.jmelody.bot.modal.TrackProvider;
import me.leoner.jmelody.bot.modal.TrackRequest;
import me.leoner.jmelody.bot.modal.TrackRequestContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PlayCommand extends AbstractCommand {

    private static final String QUERY_PARAM = "query";

    @Override
    public String getAlias() {
        return "play";
    }

    @Override
    public String getName() {
        return "Play Music Command";
    }

    @Override
    public String getDescription() {
        return "Plays the provided song for you.";
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        String query = context.getValueParamByKey(QUERY_PARAM);
        TrackRequest request = new TrackRequest(context, createTrackRequestContext(query));
        String result = PlayerManager.getDefaultAudioManager().loadAndPlay(request);
        return "**added " + result + " to queue**";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, QUERY_PARAM, "Name or URL of track to play", true),
                new OptionData(OptionType.STRING, "source", "Platform to search song", false)
        );
    }

    private TrackRequestContext createTrackRequestContext(String query) {
        return new TrackRequestContext(query, TrackProvider.getTrackProvider(query));
    }
}
