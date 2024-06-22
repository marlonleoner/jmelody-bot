package me.leoner.jmelody.bot.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.leoner.jmelody.bot.modal.MyInstantsItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInstantsService {

    private static MyInstantsService instance;

    private static final String MY_INSTANTS_BASE_URL = "https://www.myinstants.com";

    private final Random random = new Random();

    public MyInstantsItem getRandom() {
        try {
            String response = RestService.getInstance().getHtml("https://www.myinstants.com/pt/index/br/");
            Document doc = Jsoup.parse(response);

            Elements instants = doc.getElementsByClass("instant");
            List<MyInstantsItem> items = instants.stream()
                    .map(instant -> {
                        Element button = instant.getElementsByClass("small-button").first();
                        String title = button.attr("title");
                        String onClick = button.attr("onclick");
                        String url = MY_INSTANTS_BASE_URL.concat(onClick.split("play\\('")[1].split("'")[0]);

                        return new MyInstantsItem(title, url);
                    })
                    .toList();

            return items.get(random.nextInt(items.size() - 1));
        } catch (Exception ex) {
            LoggerService.error(getClass(), "Error {}", ex.getMessage());
            return null;
        }
    }

    public static MyInstantsService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MyInstantsService();
        }

        return instance;
    }
}
