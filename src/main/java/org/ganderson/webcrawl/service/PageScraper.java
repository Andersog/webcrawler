package org.ganderson.webcrawl.service;

import one.util.streamex.StreamEx;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

/**
 * Given a base domain, provides functionality for scraping the HTML of a web page and identifying any links which
 * are contained within this domain.
 */
public class PageScraper {

    private static final Logger logger = LoggerFactory.getLogger(PageScraper.class);
    private final URL baseDomain;

    /**
     * @param baseDomain The domain which we're going to be looking for links on.
     */
    public PageScraper(URL baseDomain) {
        this.baseDomain = baseDomain;
    }

    /**
     * Scrapes the page for any links which are matched to the provided base domain for this instance.
     *
     * @param document The document to scrape.
     * @return The stream of links which we've discovered on the page, filtered to those only on the base domain.
     */
    public Stream<URL> scrapeForLinks(Element document) {
        return StreamEx
            .of(document.select("a[href]"))
            .map(hyperlink -> hyperlink.absUrl("href"))
            .filter(rawUrl -> rawUrl.startsWith(this.baseDomain.toString()))
            .map(rawUrl -> {
                try {
                    return new URL(rawUrl);
                } catch (MalformedURLException exception) {
                    logger.warn("Unhandled url on page, this is most likely an issue within the HTML.", exception);
                    return null;
                }
            })
            .distinct()
            .nonNull();
    }
}

