package me.leoner.jmelody.domain;

public record LatencyMetrics(long websocket,
                             long message,
                             long process) {
}
