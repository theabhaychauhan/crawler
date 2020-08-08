package com.medium.crawler.enums;

public enum Selectors {
    TITLE("h3"),READING_TIME("readingTime"),TIME("time"),CREATOR("[data-action=show-user-card]"),BLOG_CONTENT("p")
    ,ARTICLE("search_post---------"),LINK("[data-action=\"open-post\"]");

    String value;

    Selectors(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
