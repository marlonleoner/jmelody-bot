package me.leoner.jmelody.bot.modal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestPlay {

    private Guild guild;

    private MessageChannelUnion textChannel;

    private AudioChannelUnion voiceChannel;

    private InteractionHook message;

    private Member member;

    private String song;
}
