package com.medium.crawler.controllers;

import com.medium.crawler.dtos.ArticleResponseDTO;
import com.medium.crawler.dtos.StatusResponseDTO;
import com.medium.crawler.services.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
public class ArticleController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping("/medium/article")
    public ArticleResponseDTO getArticle(@RequestBody Map<String,String> url) throws IOException {
        return crawlerService.crawlArticleContent(url.get("url"));
    }
}
