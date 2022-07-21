package org.ganderson.webcrawl.service;

import lombok.RequiredArgsConstructor;
import org.ganderson.webcrawl.Scrapers.PageScraper;

import java.net.URL;

/**
 *
 */
@RequiredArgsConstructor
public class WebCrawler {
    private PageScraper pageScraper;

    public void crawl(URL url) throws Exception {
        // Get web content
//        Document doc = Jsoup.connect("https://en.wikipedia.org/").get();

//        this.pageScraper.scrapeForLinks()

        // GEt

//        throw new UnsupportedOperationException();
    }
}