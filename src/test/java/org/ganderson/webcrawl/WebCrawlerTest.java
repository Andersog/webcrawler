/*
 * ------------------------------------------------------------------------
 *
 * <copyright file="WebCrawlerTest.java" company="Smarter Grid Solutions">
 * Copyright (c) 2022 Smarter Grid Solutions. All rights reserved.
 * </copyright>
 *
 *                  This file is the property of:
 *
 *                     Smarter Grid Solutions
 *               http://www.smartergridsolutions.com
 *
 *  This Source Code and the associated Documentation contain proprietary
 *  information of Smarter Grid Solutions and may not be copied or
 *  distributed in any form without the written permission of Smarter Grid
 *  Solutions.
 *
 * ------------------------------------------------------------------------
 */

package org.ganderson.webcrawl;

import org.ganderson.webcrawl.Scrapers.PageScraper;
import org.ganderson.webcrawl.service.WebCrawler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.print.Doc;
import java.io.ByteArrayOutputStream;

import org.jsoup.nodes.Document;

import java.io.PrintStream;
import java.net.URL;
import java.util.Optional;

import static org.ganderson.webcrawl.HtmlTestUtils.buildAnchorWithReferences;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 */
@DisplayName("Test suite for WebCrawler")
public class WebCrawlerTest {

    private static final String homepage = "http://my-homepage.com";
    private static final String pageA = "http://my-homepage.com/a";
    private static final String pageB = "http://my-homepage.com/b";

    private static final String pageC = "http://my-homepage.com/c";
    private static final String pageAB = "http://my-homepage.com/a/b";

    @DisplayName("End to end tests")
    @Nested
    public class EndToEndTest {

        @DisplayName("End to end tests")
        @Test
        public void endToEnd() throws Exception {
            // Given
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            URL homepageUrl = new URL(homepage);
            WebCrawler crawlerUnderTest = new WebCrawler(
                new PageScraper(homepageUrl),
                WebCrawlerTest::getDocument,
                homepageUrl);

            // When
            crawlerUnderTest.crawl();

            // Then


        }

    }

    private static Optional<Document> getDocument(URL url) {
        try {
            switch (url.toString()) {
                case "http://my-homepage.com":
                    return Optional.of(buildHomePage());
                case "http://my-homepage.com/a":
                    return Optional.of(buildPageA());
                case "http://my-homepage.com/b":
                    return Optional.of(buildPageB());
                case "http://my-homepage.com/c":
                    return Optional.of(buildPageC());
                case "http://my-homepage.com/a/b":
                    return Optional.of(buildPageAB());
                default:
                    fail("A request was made to an unrecognised page");
                    throw new IllegalArgumentException("A request was made to an unrecognised page");
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Document buildHomePage() throws Exception {
        return buildAnchorWithReferences(new URL(homepage), pageA);
    }

    private static Document buildPageA() throws Exception {
        return buildAnchorWithReferences(new URL(pageA), homepage, pageAB, pageB);
    }

    private static Document buildPageB() throws Exception {
        return buildAnchorWithReferences(new URL(pageB), homepage, pageA, pageC, pageAB);
    }

    private static Document buildPageC() throws Exception {
        return buildAnchorWithReferences(new URL(pageC), homepage, pageA, pageA, "htttp://off-domain/", pageAB);
    }

    private static Document buildPageAB() throws Exception {
        return buildAnchorWithReferences(new URL(pageAB));

    }
}
