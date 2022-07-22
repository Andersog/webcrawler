package org.ganderson.webcrawl.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ganderson.webcrawl.Scrapers.NonDuplicateQueue;
import org.ganderson.webcrawl.Scrapers.PageScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 */
@RequiredArgsConstructor
public class WebCrawler {
    private static final Logger logger = LogManager.getLogger(WebCrawler.class);
    private final PageScraper pageScraper;
    private final NonDuplicateQueue queue = new NonDuplicateQueue();

    private final Function<URL, Optional<Document>> getDocument;
    private URL url;

    /**
     * @param scraper The page scraper for getting links on pages.
     * @param getDocument Supplier for a document from a provided URL.
     * @param url The base url of our crawler.
     */
    WebCrawler(PageScraper scraper, Function<URL, Optional<Document>> getDocument, URL url) {
        this.getDocument = getDocument;
        this.pageScraper = scraper;
        this.url = url;

    }

    /**
     * @param url The base url of our crawler.
     */
    public WebCrawler(URL url) {
        this(new PageScraper(url), WebCrawler::defaultGetDocument, url);
    }

    /**
     * Crawl from our base page and print all links encountered.
     */
    public void crawl() {

        this.queue.offer(url);
        while (!this.queue.isEmpty()) {

            URL next = this.queue.poll();
            System.out.println(next);

            this.getDocument.apply(next).ifPresent(doc -> this.pageScraper.scrapeForLinks(doc).forEach(link -> {
                System.out.println("- " + link);
                this.queue.offer(link);
            }));
        }
    }

    /**
     * The default "getDocument" function to use if no alternative is provided.
     *
     * @param url The url to get the document for.
     * @return The document, or empty if the page couldn't be parsed.
     */
    private static Optional<Document> defaultGetDocument(URL url) {
        try {
            return Optional.of(Jsoup.connect(url.toString()).get());
        } catch (IOException ex) {
            logger.warn("Non HTML page encountered.");
            return Optional.empty();
        }
    }
}