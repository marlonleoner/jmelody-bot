package me.leoner.jmelody.bot.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.command.CommandContext;
import me.leoner.jmelody.bot.exception.BaseException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageFactory {

    public static WebhookMessageEditAction<Message> success(CommandContext context, String message) {
        LoggerService.debug(MessageFactory.class, "Creating SUCCESS replay message '{}' by {}", message, context.getMember());
        return create(context.getMessage(), false, EmbedFactory.withSuccessMessage(context.getMember().getAsMention() + " " + message));
    }

    public static WebhookMessageEditAction<Message> failure(CommandContext context, BaseException ex) {
        LoggerService.debug(MessageFactory.class, "Creating FAILURE replay message '{}' by {}", ex.getMessage(), context.getMember());
        return create(context.getMessage(), ex.isEphemeral(), EmbedFactory.withErrorMessage(context.getMember().getAsMention() + " " + ex.getMessage()));
    }

    private static WebhookMessageEditAction<Message> create(InteractionHook action, boolean ephemeral, MessageEmbed embed) {
        return action.setEphemeral(ephemeral).editOriginalEmbeds(embed);
    }
}
