package me.leoner.jmelody.bot.utils;

import me.leoner.jmelody.bot.command.CommandException;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Objects;

public class EventUtils {

    private EventUtils() {
        // empty constructor
    }

    public static AudioChannelUnion getVoiceChannelFromUser(Member user) throws CommandException {
        GuildVoiceState voiceState = user.getVoiceState();
        if (!voiceState.inAudioChannel()) {
            throw new CommandException("Você precisa estar em um canal de voz para que isso funcione", false);
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
