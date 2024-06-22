package me.leoner.jmelody.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestService {

    @Getter
    private static final RestService instance = new RestService();

    private final HttpClient client = HttpClient.newHttpClient();

    private final ObjectMapper mapper = new ObjectMapper();

    public <T> T get(String url, Class<T> responseType) throws URISyntaxException, IOException, InterruptedException {
        return mapper.readValue(this.execute(url), responseType);
    }

    public String getHtml(String url) throws URISyntaxException, IOException, InterruptedException {
        return this.execute(url);
    }

    private String execute(String url) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
