package me.leoner.jmelody.bot.command.meme;

import me.leoner.jmelody.bot.audio.PlayerManager;
import me.leoner.jmelody.bot.button.ButtonInteractionEnum;
import me.leoner.jmelody.bot.command.AbstractCommand;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.BaseException;
import me.leoner.jmelody.bot.modal.MyInstantsItem;
import me.leoner.jmelody.bot.modal.TrackProvider;
import me.leoner.jmelody.bot.modal.TrackRequest;
import me.leoner.jmelody.bot.modal.TrackRequestContext;
import me.leoner.jmelody.bot.service.MyInstantsService;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class MyInstantsCommand extends AbstractCommand {

    private final MyInstantsService service = MyInstantsService.getInstance();

    @Override
    public String getAlias() {
        return "instants";
    }

    @Override
    public String getName() {
        return "Play MyInstants sounds";
    }

    @Override
    public String getDescription() {
        return "Play sounds from MyInstants";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public String handle(CommandContext context, ButtonInteractionEnum button) throws BaseException {
        MyInstantsItem item = this.service.getRandom();
        TrackRequest request = new TrackRequest(context, new TrackRequestContext(item.getUrl(), TrackProvider.getTrackProvider(item.getUrl())));
        String result = PlayerManager.getDefaultAudioManager().loadAndPlay(request);
        return "**added " + result + " to queue**";
    }
}
