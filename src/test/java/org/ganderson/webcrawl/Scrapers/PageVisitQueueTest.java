package org.ganderson.webcrawl.Scrapers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for PageVisitQueue
 */
@DisplayName("Test suite for PageVisitQueue")
public class PageVisitQueueTest {

    @DisplayName("When offer called")
    @Nested
    public class OfferTest {

        @Test
        @DisplayName(
            "Given a page which hasn't been visited before"
                + " Then the URL is enqueued")
        public void givenNewValue_OfferQueuesPage() throws Exception {
            // Given
            URL unvisitedUrl = new URL("http://some-value/");
            PageVisitQueue cacheUnderTest = new PageVisitQueue();

            // When
            cacheUnderTest.offer(unvisitedUrl);

            // Then
            Optional<URL> polledValue = cacheUnderTest.poll();
            assertTrue(polledValue.isPresent());
            assertEquals(unvisitedUrl, polledValue.get());
        }

        @Test
        @DisplayName(
            "Given a page which has been visited before"
                + " Then the URL is not enqueued")
        public void givenVisitedValue_OfferDoesNotQueuePage() throws Exception {
            // Given
            URL unvisitedUrl = new URL("http://some-value/");
            PageVisitQueue cacheUnderTest = new PageVisitQueue();

            // The page has previously been visited
            cacheUnderTest.offer(unvisitedUrl);

            // But no longer enqueued
            cacheUnderTest.poll();

            // When
            cacheUnderTest.offer(unvisitedUrl);

            // Then
            assertFalse(cacheUnderTest.poll().isPresent());
        }

        @Test
        @DisplayName(
            "Given a page which has been visited before"
                + " And is still on the queue"
                + " Then the URL is not enqueued again")
        public void givenVisitedValueAndCurrentlyInQueue_OfferDoesNotQueuePage() throws Exception {
            // Given
            URL unvisitedUrl = new URL("http://some-value/");
            PageVisitQueue cacheUnderTest = new PageVisitQueue();

            // The page has previously been visited
            cacheUnderTest.offer(unvisitedUrl);

            // When
            cacheUnderTest.offer(unvisitedUrl);

            // Then
            assertTrue(cacheUnderTest.poll().isPresent());
            assertFalse(cacheUnderTest.poll().isPresent());
        }
    }

    @DisplayName("When poll called")
    @Nested
    public class PollTest {

        @Test
        @DisplayName(
            "Given no value enqueued"
                + " Then empty is returned")
        public void givenNoQueuedValue_ReturnsEmpty() throws Exception {
            // Given
            // No enqueued value
            PageVisitQueue cacheUnderTest = new PageVisitQueue();

            // When
            Optional<URL> result = cacheUnderTest.poll();

            // Then
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName(
            "Given a queued value"
                + " Then an optional with the value is returned")
        public void givenQueuedValue_ReturnsThatValue() throws Exception {
            // Given
            PageVisitQueue cacheUnderTest = new PageVisitQueue();

            URL urlOfInterest = new URL("http://some-value/");
            cacheUnderTest.offer(new URL("http://some-value/"));

            // When
            Optional<URL> result = cacheUnderTest.poll();

            // Then
            assertTrue(result.isPresent());
            assertEquals(urlOfInterest, result.get());
        }

        @Test
        @DisplayName(
            "Given a value is in the queue"
                + " Then the value is removed from the queue")
        public void givenQueuedValue_ValueDequeud() throws Exception {
            // Given
            PageVisitQueue cacheUnderTest = new PageVisitQueue();
            cacheUnderTest.offer(new URL("http://some-value/"));

            // When
            cacheUnderTest.poll();

            // Then
            assertFalse(cacheUnderTest.poll().isPresent());
        }

        @Test
        @DisplayName(
            "Given multiple value is in the queue"
                + " Then the values are removed from the queue on equal polls")
        public void givenMultipleQueuedValues_ValueDequeud() throws Exception {
            // Given
            PageVisitQueue cacheUnderTest = new PageVisitQueue();
            cacheUnderTest.offer(new URL("http://some-value/"));
            cacheUnderTest.offer(new URL("http://some-other-value/"));

            // When
            cacheUnderTest.poll();
            cacheUnderTest.poll();

            // Then
            assertFalse(cacheUnderTest.poll().isPresent());
        }
    }


    @DisplayName("End to end functional tests")
    @Nested
    public class EndToEndTest {

        @Test
        @DisplayName(
            "End to end test for various functionality")
        public void endToEndTest() throws Exception {
            PageVisitQueue cacheUnderTest = new PageVisitQueue();

            URL urlA = new URL("http://some-value/");
            URL urlB = new URL("http://some-value/a");
            URL urlC = new URL("http://some-value/a/b/");

            cacheUnderTest.offer(urlA);
            cacheUnderTest.offer(urlB);

            assertEquals(urlA, cacheUnderTest.poll().get());
            assertEquals(urlB, cacheUnderTest.poll().get());

            cacheUnderTest.offer(urlA);
            cacheUnderTest.offer(urlC);

            assertEquals(urlC, cacheUnderTest.poll().get());

            assertFalse(cacheUnderTest.poll().isPresent());
        }
    }
}
