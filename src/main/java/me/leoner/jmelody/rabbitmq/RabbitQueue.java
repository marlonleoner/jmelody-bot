package me.leoner.jmelody.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RabbitQueue {

    MESSAGE_DELETE("jmelody.message.delete"),
    AUDIT("jmelody.audit");

    private final String name;
}
