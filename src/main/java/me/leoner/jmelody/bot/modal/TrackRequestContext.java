package me.leoner.jmelody.bot.modal;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TrackRequestContext {

    private String query;

    private TrackProvider provider;

    public String getFullQuery() {
        if (provider.isSearch()) {
            return provider.getPrefix().concat(query);
        }

        return query;
    }

    public boolean isSearch() {
        return provider.isSearch();
    }
}
