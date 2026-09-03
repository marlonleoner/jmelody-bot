package me.leoner.jmelody.rabbitmq.dto;

public record DeleteMessageEvent(String guildId,
                                 String channelId,
                                 String messageId) {
}
