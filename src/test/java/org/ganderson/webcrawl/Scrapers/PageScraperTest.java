package org.ganderson.webcrawl.Scrapers;

import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URL;
import java.util.List;

import static org.ganderson.webcrawl.HtmlTestUtils.buildAnchorWithReferences;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link PageScraper}
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

                Element element = buildAnchorWithReferences(base, offDomainLink);

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

                Element element = buildAnchorWithReferences(base, absoluteLink.toString());

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(absoluteLink.toString(), results);
            }

            @DisplayName(
                "Given multiple links which are duplicates"
                    + " Then only a single value is returned")
            @Test
            public void givenValidDuplicateRelativeLinks_IgnoredDuplicates() throws
                Exception {
                // Given
                URL base = new URL("http://my-domain.com/sub-folder/");
                URL currentPage = new URL("http://my-domain.com/sub-folder/");

                String absoluteLink = "http://my-domain.com/sub-folder/another-value";
                Element element = buildAnchorWithReferences(currentPage, absoluteLink, absoluteLink);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly(absoluteLink, results);
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

                Element element = buildAnchorWithReferences(currentPage, relativeLink);

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

                Element element = buildAnchorWithReferences(currentPage, relativeLink);

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

                Element element = buildAnchorWithReferences(currentPage, relativeLink);
                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertTrue(results.isEmpty());
            }

            @DisplayName(
                "Given multiple relative paths which are duplicates"
                    + " Then only a single value is returned")
            @Test
            public void givenValidDuplicateRelativeLinks_IgnoredDuplicates() throws
                Exception {
                // Given
                URL base = new URL("http://my-domain.com/sub-folder/");
                URL currentPage = new URL("http://my-domain.com/sub-folder/");

                String relativeLink = "./test";
                Element element = buildAnchorWithReferences(currentPage, relativeLink, relativeLink);

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertContainsOnly("http://my-domain.com/sub-folder/test", results);
            }
        }

        @DisplayName("End to end functional tests")
        @Nested
        public class EndToEndTest {

            @Test
            @DisplayName(
                "End to end test for various functionality")
            public void endToEndTest() throws Exception {
                // Given
                URL base = new URL("http://my-domain.com/a/");
                URL currentPage = new URL("http://my-domain.com/a/b/c");

                Element element = buildAnchorWithReferences(
                    currentPage,
                    // Some valid relative times
                    "./valid-relative",

                    // Duplicate
                    "../valid-directory-up",
                    "../valid-directory-up",

                    // Some values which will move us out of the base domain
                    "../../invalid-directory-up",
                    "/invalid-root",

                    // Invalid absolute
                    "http://my-domain.com/an-absolute-value",
                    "http://other-domain.com/an-absolute-value",

                    // Valid but duplicate absolute URLS
                    "http://my-domain.com/a/an-absolute-value",
                    "http://my-domain.com/a/an-absolute-value");

                PageScraper scraperUnderTest = new PageScraper(base);

                // When
                List<URL> results = scraperUnderTest.scrapeForLinks(element);

                // Then
                assertEquals(3, results.size());
                assertTrue(results.contains(new URL("http://my-domain.com/a/b/valid-relative")));
                assertTrue(results.contains(new URL("http://my-domain.com/a/valid-directory-up")));
                assertTrue(results.contains(new URL("http://my-domain.com/a/an-absolute-value")));
            }
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
}
