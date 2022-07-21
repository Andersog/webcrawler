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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
            @ValueSource(strings = {
                "http://my-other-domain",
                "http://my-other-domain/",
                "http://my-other-domain/some-value/resource",
                "http://my-other-domain/some-value/resource?query=asd"
            })
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
            @ValueSource(strings =
                {
                    "",
                    "/",
                    "/some-value",
                    "/some-value/resource",
                    "/some-value/resource?query=asd",
                })
            @DisplayName("Given an absolute domain link which matches our base domain"
                + " Then the page is returned")
            public void givenOnDomainLinks_ReturnPage(String linkToTest) throws Exception {
                // Given
                URL base = new URL("http://my-domain.com");
                URL absoluteLink = new URL(base, linkToTest);

                Element element = buildAnchorWithReference(absoluteLink.toString(), base);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(absoluteLink.toString(), results);
            }
        }

        @Nested
        @DisplayName("Given a relative link")
        public class RelativeLinkTest {

            @DisplayName(
                "Given our base domain and current page are on the root of the domain"
                    + " Then the page is returned")
            @ParameterizedTest
            @CsvSource( {
                "/some-value, /some-value",
                "/some-value/resource, /some-value/resource",
                "/some-value/resource?query=a-query, /some-value/resource?query=a-query",
                "./some-value, /some-value",
                "../some-value, /some-value",
                "../../some-value, /some-value",
            })
            public void givenRelativeOnBaseDomainLinks_ReturnPage(String relativeLink, String expectedSubDomain) throws
                Exception {
                // Given
                // A base which is at the root of our domain
                URL base = new URL("http://my-domain.com");

                // A current page which is also at the root of our domain
                URL currentPage = new URL("http://my-domain.com");

                Element element = buildAnchorWithReference(relativeLink, currentPage);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(currentPage + expectedSubDomain, results);
            }

            @DisplayName(
                "Given a relative link which will adjust sub-folders but still match the parent domain"
                    + " Then the page is returned")
            @ParameterizedTest
            @CsvSource( {
                "/some-value, /some-value",
                "/some-value/resource?query=a-query, /some-value/resource?query=a-query",
                "./some-value, /sub-a/sub-b/some-value",
                "../some-value, /sub-a/some-value",
                "../../some-value, /some-value"
            })
            public void givenRelativeOnSubFolderLinks_ReturnPage(String relativeLink, String expectedSubDomain) throws
                Exception {
                // Given
                // A base which is at the root of our domain
                URL base = new URL("http://my-domain.com");

                // A current page which is also at the root of our domain
                URL currentPage = new URL("http://my-domain.com/sub-a/sub-b/");

                Element element = buildAnchorWithReference(relativeLink, currentPage);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(base + expectedSubDomain, results);
            }

            @DisplayName(
                "Given a relative path will move us beyond our base domain"
                    + " Then the page is not returned")
            @ParameterizedTest
            @ValueSource(strings = {
                "/some-value",
                "/some-value/resource",
                "/some-value/resource?query=a-query",
                "../some-value, /some-value",
                "../../some-value",
                "/some-value"}
            )
            public void givenRelativeNotOnBaseDomainLinks_ReturnPage(String relativeLink) throws
                Exception {
                // Given
                // A base which is down a sub folder
                URL base = new URL("http://my-domain.com/sub-folder/");

                // A page which is also a sub-folder
                URL currentPage = new URL("http://my-domain.com/sub-folder/");

                Element element = buildAnchorWithReference(relativeLink, currentPage);
                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertTrue(results.isEmpty());
            }
        }

        @Nested
        @DisplayName("Integration test.")
        public class IntegrationTests {
            // TODO
        }
    }


    /**
     * Utility for asserting that a list contains only the expected value and nothing else.
     *
     * @param expectedValue The value to check for.
     * @param listToValidate The list we're going to assert against.
     */
    private static void assertContainsOnly(String expectedValue, List<URL> listToValidate) {
        assertEquals(1, listToValidate.size());
        assertEquals(expectedValue, listToValidate.get(0).toString());
    }

    /**
     * Builds an HTML anchor with the provided href.
     *
     * @param reference The href.
     * @param baseUrl The base URL of the 'page'.
     */
    private Element buildAnchorWithReference(String reference, URL baseUrl) {
        return Jsoup.parseBodyFragment("<div><a href=\"" + reference + "\"/><div>", baseUrl.toString());
    }
}
