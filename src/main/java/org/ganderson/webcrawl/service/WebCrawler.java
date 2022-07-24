package org.ganderson.webcrawl.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Crawls a domain for all pages on that domain and the links contained on each page.
 */
public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
    private final PageScraper pageScraper;
    private final NonDuplicateQueue queue = new NonDuplicateQueue();
    private final Function<URL, Optional<Document>> getDocument;
    private final URL url;

    /**
     * @param scraper The page scraper for getting links on pages.
     * @param getDocument Supplier for a document from a provided URL.
     * @param url The base url of our crawler.
     */
    public WebCrawler(PageScraper scraper, Function<URL, Optional<Document>> getDocument, URL url) {
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
            logger.warn("Non HTML page encountered [{}].", url);
            return Optional.empty();
        }
    }
}