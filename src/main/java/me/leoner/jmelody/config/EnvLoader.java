package me.leoner.jmelody.config;

import java.util.Optional;

public class EnvLoader {

    public static String get(String key) {
        String value = Optional
                .ofNullable(System.getenv(key))
                .orElseThrow(() -> new IllegalStateException("Variável de ambiente não configurada: " + key));

        return value;
    }
}
