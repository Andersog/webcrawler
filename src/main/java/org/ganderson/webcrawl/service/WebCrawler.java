package org.ganderson.webcrawl.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Crawls a domain for all pages on that domain and the links contained on each page.
 *
 * <p>
 * Any links contained on the crawled pages which do not match the base domain will be ignored
 * </p>
 */
public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
    private final PageScraper pageScraper;
    private final NonDuplicateQueue queue = new NonDuplicateQueue();
    private final DocumentParser documentParser;
    private final URL url;

    /**
     * @param scraper The page scraper for getting links on pages.
     * @param documentParser The parser which will source our web pages and parse the HTML.
     * @param url The base url which the crawler will search links for.
     */
    WebCrawler(PageScraper scraper, DocumentParser documentParser, URL url) {
        this.documentParser = documentParser;
        this.pageScraper = scraper;
        this.url = url;
    }

    /**
     * Creates a new instance, using the default document parser and page scraper.
     *
     * @param url The base url which the crawler will search links for.
     */
    public WebCrawler(URL url) {
        this(new PageScraper(url), WebCrawler::defaultGetDocument, url);
    }

    /**
     * Crawl from our base page and print all links encountered to system out.
     */
    public void crawl() {
        this.queue.offer(url);
        while (!this.queue.isEmpty()) {

            URL next = this.queue.poll();
            System.out.println(next);

            this.documentParser
                .parseDocument(next)
                .ifPresent(doc -> this.pageScraper.scrapeForLinks(doc).forEach(link -> {
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

    /**
     * A parser which will locate a document at a given URL and parse the HTML found at the location.
     */
    public interface DocumentParser {
        /**
         * Parses the document at the provided URL.
         *
         * @param url The URL to source the document from.
         * @return The parsed document, or empty if the document could not be parsed.
         */
        Optional<Document> parseDocument(URL url);
    }
}