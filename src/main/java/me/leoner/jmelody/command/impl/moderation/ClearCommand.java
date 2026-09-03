package me.leoner.jmelody.command.impl.moderation;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.leoner.jmelody.command.CommandAbstract;
import me.leoner.jmelody.command.CommandContext;
import me.leoner.jmelody.rabbitmq.RabbitQueue;
import me.leoner.jmelody.rabbitmq.dto.DeleteMessageEvent;
import me.leoner.jmelody.service.ApplicationModules;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ClearCommand extends CommandAbstract {

    private static final String AMOUNT_OPTION = "amount";
    private static final int DEFAULT_AMOUNT = 20;
    private static final int MAX_AMOUNT = 100;

    public ClearCommand() {
        super("clear", "Limpa mensagens recentes do canal");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(
                        OptionType.INTEGER,
                        AMOUNT_OPTION,
                        "Quantidade de mensagens que serão removidas",
                        false
                )
                        .setMinValue(1)
                        .setMaxValue(MAX_AMOUNT)
        );
    }

    @Override
    public void handle(final CommandContext context) {
        int amount = context.getIntegerOption(
                AMOUNT_OPTION,
                DEFAULT_AMOUNT
        );

        var guild = context.getGuild();
        var channel = context.getChannel();
        log.info("Removendo mensagens do canal {}", channel.getName());

        var rabbit = ApplicationModules.getInstance().getRabbit();
        var objectMapper = new ObjectMapper();

        channel.getHistory()
                .retrievePast(amount)
                .queue(messages -> {
                    messages.forEach(message -> {
                        var event = new DeleteMessageEvent(
                                guild.getId(),
                                channel.getId(),
                                message.getId()
                        );

                        try {
                            rabbit.publish(RabbitQueue.MESSAGE_DELETE, objectMapper.writeValueAsString(event));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    context.replyPrivate("%d mensagens adicionadas à fila de exclusão.".formatted(messages.size()));
                });
    }

    private void deleteMessages(CommandContext context, List<Message> messages) {
        if (messages.isEmpty()) {
            context.replyPrivate("Não existem mensagens para remover.");
            return;
        }

        var futures = context
                .getChannel()
                .purgeMessages(messages);

        context.replyPrivate("Vamos remover %d mensagens".formatted(futures.size()));

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}