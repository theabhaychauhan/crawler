package com.medium.crawler.bootstrap;

import com.medium.crawler.services.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class BootstrapLoader implements CommandLineRunner {

    @Autowired
    private CrawlerService crawlerService;

    @Override
    public void run(String... args) throws Exception {
//        crawlerService.crawlArticleContent("https://towardsdatascience.com/how-to-learn-data-science-if-youre-broke-7ecc408b53c7?source=search_post---------0");

    }




}
