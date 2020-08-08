package com.medium.crawler.controllers;

import com.medium.crawler.dtos.MediumRequestDTO;
import com.medium.crawler.dtos.StatusResponseDTO;
import com.medium.crawler.services.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class MediumController {

    @Autowired
    private CrawlerService crawlerService;

    @MessageMapping("/medium")
    @SendTo("/topic/medium")
    public StatusResponseDTO getResponse(MediumRequestDTO mediumRequestDTO) throws IOException {
        crawlerService.crawlArticles(mediumRequestDTO.getTag() , mediumRequestDTO.getPage());
        return new StatusResponseDTO("Crawling Successfully Ended");
    }
}
