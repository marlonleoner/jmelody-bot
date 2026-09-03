package me.leoner.jmelody.commandold.meme;

import me.leoner.jmelody.audio.PlayerManager;
import me.leoner.jmelody.button.ButtonInteractionEnum;
import me.leoner.jmelody.commandold.AbstractCommand;
import me.leoner.jmelody.commandold.CommandContext;
import me.leoner.jmelody.exception.BaseException;
import me.leoner.jmelody.modal.MyInstantsItem;
import me.leoner.jmelody.modal.TrackProviderEnum;
import me.leoner.jmelody.modal.TrackRequest;
import me.leoner.jmelody.modal.TrackRequestContext;
import me.leoner.jmelody.serviceold.MyInstantsService;
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
        TrackRequest request = new TrackRequest(context, new TrackRequestContext(item.getUrl(), TrackProviderEnum.getTrackProvider(item.getUrl())));
        String result = PlayerManager.getDefaultAudioManager().loadAndPlay(request);
        return "**added " + result + " to queue**";
    }
}
