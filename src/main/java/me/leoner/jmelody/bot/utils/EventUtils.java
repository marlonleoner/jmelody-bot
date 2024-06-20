package me.leoner.jmelody.bot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.modal.exception.CommandException;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventUtils {

    public static AudioChannelUnion getVoiceChannelFromUser(Member user) throws CommandException {
        GuildVoiceState voiceState = user.getVoiceState();
        if (!voiceState.inAudioChannel()) {
            throw new CommandException("you need to be in a voice channel", true);
        }

        return voiceState.getChannel();
    }

    public static String getParam(SlashCommandInteractionEvent event, String option) throws CommandException {
        OptionMapping optionMapping = event.getOption(option);
        if (Objects.isNull(optionMapping)) {
            throw new CommandException(option + " value not found", true);
        }

        return optionMapping.getAsString();
    }

}
