package org.ganderson.webcrawl.service;

import lombok.RequiredArgsConstructor;
import org.ganderson.webcrawl.Scrapers.PageScraper;
import org.ganderson.webcrawl.Scrapers.PageVisitQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@RequiredArgsConstructor
public class WebCrawler {
    private PageScraper pageScraper;
    private PageVisitQueue queue = new PageVisitQueue();

    private URL url;

    public WebCrawler(URL url) {
        this.pageScraper = new PageScraper(url);
        this.url = url;
    }

    public void crawl() throws Exception {
        this.queue.offer(url);

        while (true) {
            Optional<URL> next = this.queue.poll();

            if (!next.isPresent()) {
                return;
            }

            //
            URL nextValue = next.get();
            Document doc = Jsoup.connect(next.get().toString()).get();

            System.out.println(nextValue);

            this.pageScraper.scrapeForLinks(doc)
                            .forEach(link -> {
                                System.out.println("- " + link);
                                this.queue.offer(link);
                            });
        }


    }
}