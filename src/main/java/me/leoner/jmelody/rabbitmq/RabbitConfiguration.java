package me.leoner.jmelody.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import me.leoner.jmelody.config.EnvironmentConfiguration;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitConfiguration implements AutoCloseable {

    private final String host;
    private final int port;
    private final String virtualHost;
    private final String username;
    private final String password;

    private Connection connection;

    public RabbitConfiguration(EnvironmentConfiguration environment) {
        this.host = environment.getRabbitHost();
        this.port = environment.getRabbitPort();
        this.virtualHost = environment.getRabbitVirtualHost();
        this.username = environment.getRabbitUsername();
        this.password = environment.getRabbitPassword();

        log.info("RabbitMQ configuration loaded.");
    }

    public void connect() throws IOException, TimeoutException {
        var factory = new ConnectionFactory();

        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        this.connection = factory.newConnection();

        log.info("RabbitMQ connection established.");
    }

    public Channel createChannel() throws IOException {
        return Optional
                .ofNullable(connection.createChannel())
                .orElseThrow(() -> new IllegalStateException("RabbitMQ connection has not been started."));
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    @Override
    public void close() throws IOException {
        if (connection != null && connection.isOpen()) {
            connection.close();
        }

        log.info("RabbitMQ connection closed.");
    }
}