/*
 * ------------------------------------------------------------------------
 *
 * <copyright file="PageScraperTest.java" company="Smarter Grid Solutions">
 * Copyright (c) 2022 Smarter Grid Solutions. All rights reserved.
 * </copyright>
 *
 *                  This file is the property of:
 *
 *                     Smarter Grid Solutions
 *               http://www.smartergridsolutions.com
 *
 *  This Source Code and the associated elementation contain proprietary
 *  information of Smarter Grid Solutions and may not be copied or
 *  distributed in any form without the written permission of Smarter Grid
 *  Solutions.
 *
 * ------------------------------------------------------------------------
 */

package org.ganderson.webcrawl.Scrapers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
@DisplayName("Test suite for PageScraper")
public class PageScraperTest {

    @Nested
    @DisplayName("When scrapeForLinks called")
    public class ScrapeForLinksTest {

        @Nested
        @DisplayName("Given an absolute link")
        public class AbsoluteLinkTest {

            @ParameterizedTest
            @ValueSource(strings = {"http://my-other-domain", "http://my-other-domain/",
                "http://my-other-domain/some-value/resource", "http://my-other-domain/some-value/resource?query=asd"})
            @DisplayName("Given an absolute off domain link"
                + " Then the page is not returned")
            public void givenOffDomainLink_ReturnsEmpty(String offDomainLink) throws Exception {
                // Given
                URL base = new URL("http://my-domain.com");

                Element element = buildAnchorWithReference(offDomainLink, base);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertTrue(results.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(strings = {"/some-value/resource", "/some-value", "/some-value/resource?query=asd", "/", ""})
            @DisplayName("Given an absolute domain link which matches our base domain"
                + " Then the page is returned")
            public void givenOnDomainLinks_ReturnPage(String link) throws Exception {
                // Given
                URL base = new URL("http://my-domain.com");
                URL subDomain = new URL(base, link);

                Element element = buildAnchorWithReference(subDomain.toString(), base);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(subDomain.toString(), results);
            }
        }

        @Nested
        @DisplayName("Given a relative link")
        public class RelativeLinkTest {

            @ParameterizedTest
            @ValueSource(strings = {
                "/some-value",
                "/some-value/resource",
                "/some-value/resource?query=a-query",
                "../some-value",
                "../../some-value",
                "/some-value"})
            @DisplayName("Given our base is on the root of the domain"
                + " Then the page is returned")
            public void givenRelativeOnBaseDomainLinks_ReturnPage(String relativeLink) throws Exception {
                // Given
                // A base which is not at the root of the domain
                URL base = new URL("http://my-domain.com/suba/resource");

                Element element = buildAnchorWithReference(relativeLink, base);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(new URI(base, relativeLink), results);
            }

            @ParameterizedTest
            @ValueSource(strings = {"/a-relative-resource", "#a-relative-resource/", "./a-relative-resource", "../a" +
                "-relative-resource"})
            @DisplayName(
                "Given a relative resource which matches our base domain"
                    + " Then the page is returned")
            public void givenRelativeOnDomainLinks_ReturnPage(String relativeLink) throws Exception {
                // Given
                // A base URL which is the root domain
                URL base = new URL("http://my-domain.com/");

                Element element = buildAnchorWithReference(relativeLink, base);
                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(base + "a-relative-resource", results);
            }
        }
    }

    private static void assertContainsOnly(String expectedValue, List<URL> listToValidate) {
        assertEquals(1, listToValidate.size());
        assertEquals(expectedValue, listToValidate.get(0).toString());
    }

    private Element buildAnchorWithReference(String reference, URL baseUrl) {
        return Jsoup.parseBodyFragment("<div><a href=\"" + reference + "\"/><div>", baseUrl.toString());
    }
}
