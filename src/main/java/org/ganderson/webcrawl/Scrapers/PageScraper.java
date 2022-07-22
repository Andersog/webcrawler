package org.ganderson.webcrawl.Scrapers;

import one.util.streamex.StreamEx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Scrapes a weblink for a
 */
public class PageScraper {

    private static final Logger logger = LogManager.getLogger(PageScraper.class);
    private final URL baseDomain;

    /**
     * @param baseDomain The domain which we're going to be looking for links on.
     */
    public PageScraper(URL baseDomain) {
        this.baseDomain = baseDomain;
    }

    /**
     * Scrapes the page for links which are on the supplied base domain.
     *
     * @param document The document to scrape.
     * @return The list of links which we've discovered on the page, filtered to those only on the base domain.
     */
    public List<URL> scrapeForLinks(Element document) {
        return StreamEx
            .of(document.select("a[href]"))
            .map(hyperlink -> hyperlink.absUrl("href"))
            .filter(url -> url.startsWith(this.baseDomain.toString()))
            .map(rawUrl -> {
                try {
                    return new URL(rawUrl);
                } catch (MalformedURLException exception) {
                    logger.warn("Unhandled url on page, this is most likely an issue within the HTML.", exception);
                    return null;
                }
            })
            .distinct()
            .nonNull()
            .toList();
    }
}

