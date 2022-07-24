package org.ganderson.webcrawl.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import org.jsoup.nodes.Document;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.ganderson.webcrawl.HtmlTestUtils.buildAnchorWithReferences;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@link WebCrawler} class.
 */
@DisplayName("Test suite for WebCrawler")
public class WebCrawlerTest {

    private static final String HOMEPAGE_URL = "http://my-homepage.com";
    private static final String A_URL = HOMEPAGE_URL + "/a";
    private static final String B_URL = HOMEPAGE_URL + "/b";
    private static final String C_URL = HOMEPAGE_URL + "/c";
    private static final String AB_URL = HOMEPAGE_URL + "/a/b";
    private static final Map<String, Document> pages = new HashMap<>();

    /**
     * This pags are setup as:
     * <p>
     * http://my-homepage.com
     * - http://my-homepage.com/
     * - http://my-homepage.com/a
     * <p>
     * http://my-homepage.com/a
     * - http://my-homepage.com
     * - http://my-homepage.com/a/b
     * - http://my-homepage.com/b
     * <p>
     * http://my-homepage.com/b
     * - http://my-homepage.com
     * - http://my-homepage.com/a
     * - http://my-homepage.com/c
     * - http://my-homepage.com/a/b
     * <p>
     * http://my-homepage.com/c
     * - http://my-homepage.com
     * - http://my-homepage.com/a
     * - http://my-homepage.com/a
     * - http://off-domain/
     * - http://my-homepage.com/a/b
     * - http://my-homepage.com/a-non-existent-page
     * <p>
     * http://my-homepage.com/a/b
     * - http://my-homepage.com/a
     * </p>
     */
    @BeforeAll
    public static void setup() {
        try {
            pages.put(HOMEPAGE_URL, buildAnchorWithReferences(new URL(HOMEPAGE_URL), HOMEPAGE_URL, "./a"));
            pages.put(A_URL, buildAnchorWithReferences(new URL(A_URL), HOMEPAGE_URL, "/a/b", B_URL));
            pages.put(B_URL, buildAnchorWithReferences(new URL(B_URL), HOMEPAGE_URL, A_URL, C_URL, AB_URL));
            pages.put(
                C_URL,
                buildAnchorWithReferences(
                    new URL(C_URL),
                    HOMEPAGE_URL,
                    A_URL,
                    A_URL,
                    "http://off-domain.com",
                    AB_URL,
                    HOMEPAGE_URL + "/another-page"));
            pages.put(AB_URL, buildAnchorWithReferences(new URL(AB_URL), "../a"));
        } catch (MalformedURLException e) {
            fail("Unable to setup pages for tests", e);
        }
    }

    @DisplayName("End to end tests")
    @Nested
    public class EndToEndTest {
        @DisplayName("End to end tests")
        @Test
        public void endToEnd() throws Exception {
            // Given
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            URL homepageUrl = new URL(HOMEPAGE_URL);
            WebCrawler crawlerUnderTest = new WebCrawler(
                new PageScraper(homepageUrl),
                WebCrawlerTest::getDocument,
                homepageUrl);

            // When
            crawlerUnderTest.crawl();

            // Then
            String lineSeperator = System.lineSeparator();
            assertEquals(
                "http://my-homepage.com" + lineSeperator +
                    "- http://my-homepage.com" + lineSeperator +
                    "- http://my-homepage.com/a" + lineSeperator +
                    "http://my-homepage.com/a" + lineSeperator +
                    "- http://my-homepage.com" + lineSeperator +
                    "- http://my-homepage.com/a/b" + lineSeperator +
                    "- http://my-homepage.com/b" + lineSeperator +
                    "http://my-homepage.com/a/b" + lineSeperator +
                    "- http://my-homepage.com/a" + lineSeperator +
                    "http://my-homepage.com/b" + lineSeperator +
                    "- http://my-homepage.com" + lineSeperator +
                    "- http://my-homepage.com/a" + lineSeperator +
                    "- http://my-homepage.com/c" + lineSeperator +
                    "- http://my-homepage.com/a/b" + lineSeperator +
                    "http://my-homepage.com/c" + lineSeperator +
                    "- http://my-homepage.com" + lineSeperator +
                    "- http://my-homepage.com/a" + lineSeperator +
                    "- http://my-homepage.com/a/b" + lineSeperator +
                    "- http://my-homepage.com/another-page" + lineSeperator +
                    "http://my-homepage.com/another-page" + lineSeperator,
                out.toString());
        }
    }

    /**
     * Simple mock of our getDocument function.
     *
     * @param url The url of the document to get.
     * @return An associated mock, or empty if none exist.
     */
    private static Optional<Document> getDocument(URL url) {
        return Optional.ofNullable(pages.get(url.toString()));
    }
}
