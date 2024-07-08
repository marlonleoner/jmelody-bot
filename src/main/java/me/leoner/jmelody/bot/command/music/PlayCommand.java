package me.leoner.jmelody.bot.command.music;

import me.leoner.jmelody.bot.audio.PlayerManager;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.BaseException;
import me.leoner.jmelody.bot.modal.TrackProviderEnum;
import me.leoner.jmelody.bot.modal.TrackRequest;
import me.leoner.jmelody.bot.modal.TrackRequestContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PlayCommand extends AbstractCommand {

    private static final String QUERY_PARAM = "query";
    private static final String SOURCE_PARAM = "source";

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
    public List<OptionData> getOptions() {
        OptionData provider = new OptionData(OptionType.INTEGER, SOURCE_PARAM, "Platform to search song", false);
        TrackProviderEnum.getActives().forEach(trackProvider -> provider.addChoice(trackProvider.getName(), trackProvider.getId()));

        return List.of(
                new OptionData(OptionType.STRING, QUERY_PARAM, "Name or URL of track to play", true),
                provider
        );
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        String query = context.getValueParamByKey(QUERY_PARAM);
        Integer provider = context.getValueParamByKey(SOURCE_PARAM);
        TrackRequest request = new TrackRequest(context, createTrackRequestContext(query, provider));
        String result = PlayerManager.getDefaultAudioManager().loadAndPlay(request);
        return "**added " + result + " to queue**";
    }

    private TrackRequestContext createTrackRequestContext(String query, Integer provider) {
        return new TrackRequestContext(query, TrackProviderEnum.getTrackProvider(query, provider));
    }
}
