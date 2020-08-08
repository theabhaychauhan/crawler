package com.medium.crawler.services;

import com.medium.crawler.dtos.ArticleResponseDTO;
import com.medium.crawler.dtos.MediumResponseDTO;
import com.medium.crawler.enums.Selectors;
import com.medium.crawler.model.Medium;
import com.medium.crawler.repositories.MediumRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CrawlerService {

    @Autowired
    private MediumRepository mediumRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void crawlArticles(String tag, int articleIndex) throws IOException {
        List<Medium> mediumList = getArticleData(tag, articleIndex);
        mediumRepository.saveAll(mediumList);
    }

    private List<Medium> getArticleData(String tag, int page) throws IOException {
        List<Medium> mediumList = new ArrayList<>();
        String articleUrl = "https://medium.com/search/posts?q=" + tag + "&page=" + page;
        Document articles = Jsoup.connect(articleUrl).get();
        for (int index = 0; index < 10; index++) {
            String selector = Selectors.ARTICLE.getValue() + index;
            long startingTime = System.currentTimeMillis();
            Elements article = articles.select("[data-source=" + selector + "]");
            int finalIndex = index;
            article.forEach(element -> {
                Medium medium = new Medium();
                medium.setTitle(element.getElementsByTag(Selectors.TITLE.getValue()).text());
                if (medium.getTitle().isEmpty()) {
                    medium.setTitle(element.getElementsByTag("h2").text());
                }
                medium.setCreator(element.select(Selectors.CREATOR.getValue()).last().text());
                medium.setDate(element.getElementsByTag(Selectors.TIME.getValue()).text());
                medium.setTimeToRead(element.getElementsByClass(Selectors.READING_TIME.getValue()).attr("title"));
                medium.setArticleLink(element.select("div.postArticle-readMore > a").first().attr("href"));
                long endingTime = System.currentTimeMillis();
                medium.setCrawlingTime(endingTime - startingTime);
                String tags = null;
                try {
                    tags = getTags(medium.getArticleLink());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pushData(new MediumResponseDTO(medium, tags ,finalIndex));
                mediumList.add(medium);
            });

        }
        return mediumList;
    }

    private String getTags(String articleLink) throws IOException {
        Document page = Jsoup.connect(articleLink).get();
        ArrayList<String> tags = new ArrayList<>();
        page.select("ul.bf > li > a").forEach(e -> {
            tags.add(e.text());
        });
        return String.join(",", tags);
    }


    private void pushData(MediumResponseDTO mediumResponseDTO) {
        messagingTemplate.convertAndSend("/topic/medium",mediumResponseDTO);
    }

    public ArticleResponseDTO crawlArticleContent(String url) throws IOException {
        Long startingTime = System.currentTimeMillis();
        Document articleContent = Jsoup.connect(url).get();
        Element articleBody = articleContent.getElementsByTag("article").first();
        articleBody.select("img").remove();
        Long endingTime = System.currentTimeMillis();
        return new ArticleResponseDTO(articleBody.toString(), endingTime - startingTime);
    }
}
