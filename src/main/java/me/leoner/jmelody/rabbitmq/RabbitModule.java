package me.leoner.jmelody.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class RabbitModule implements AutoCloseable {

    private final RabbitConfiguration configuration;

    private final List<Channel> consumerChannels = new ArrayList<>();

    private Channel publisherChannel;

    public RabbitModule(RabbitConfiguration configuration) {
        this.configuration = configuration;
    }

    public void start() throws IOException {
        log.info("RabbitModule starting.");

        this.publisherChannel = configuration.createChannel();

        declareQueues();
    }

    private void declareQueues() throws IOException {
        for (var queue : RabbitQueue.values()) {
            publisherChannel.queueDeclare(
                    queue.getName(),
                    true,
                    false,
                    false,
                    null
            );

            log.info("Queue {} declared.", queue.getName());
        }
    }

    public synchronized void publish(RabbitQueue queue, String message) throws IOException {
        publisherChannel.basicPublish(
                "",
                queue.getName(),
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void consume(String queue, Consumer<String> handler) throws IOException {
        var channel = configuration.createChannel();

        consumerChannels.add(channel);

        channel.basicConsume(queue, true, (consumerTag, delivery) -> {
            var message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            handler.accept(message);
        }, consumerTag -> {
        });
    }

    @Override
    public void close() throws Exception {
        for (var channel : consumerChannels) {
            if (channel.isOpen()) {
                channel.close();
            }
        }

        if (publisherChannel != null && publisherChannel.isOpen()) {
            publisherChannel.close();
        }
    }
}
