package me.leoner.jmelody.bot.command;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class CommandContext {

    private final Guild guild;

    private final MessageChannel textChannel;

    private final Member member;

    private final InteractionHook message;

    private final AudioChannel voiceChannel;

    @Setter
    private Map<String, Object> params;

    public CommandContext(SlashCommandInteractionEvent event, InteractionHook message) {
        this(event.getGuild(), event.getChannel(), event.getMember(), message);
    }

    public CommandContext(ButtonInteractionEvent event, InteractionHook message) {
        this(event.getGuild(), event.getChannel(), event.getMember(), message);
    }

    private CommandContext(Guild guild, MessageChannel textChannel, Member member, InteractionHook action) {
        this.guild = guild;
        this.textChannel = textChannel;
        this.member = member;
        this.message = action;
        this.voiceChannel = member.getVoiceState().getChannel();
        this.params = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueParamByKey(String key) {
        return (T) params.get(key);
    }
}
