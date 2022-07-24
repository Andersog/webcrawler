package org.ganderson.webcrawl.Scrapers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link NonDuplicateQueue}.
 */
@DisplayName("Test suite for NonDuplicateQueue")
public class NonDuplicateQueueTest {

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
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            // When
            cacheUnderTest.offer(unvisitedUrl);

            // Then
            URL polledValue = cacheUnderTest.poll();
            assertEquals(unvisitedUrl, polledValue);
        }

        @Test
        @DisplayName(
            "Given a page which has been visited before"
                + " Then the URL is not enqueued")
        public void givenVisitedValue_OfferDoesNotQueuePage() throws Exception {
            // Given
            URL unvisitedUrl = new URL("http://some-value/");
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            // The page has previously been visited
            cacheUnderTest.offer(unvisitedUrl);

            // But no longer enqueued
            cacheUnderTest.poll();

            // When
            cacheUnderTest.offer(unvisitedUrl);

            // Then
            assertNull(cacheUnderTest.poll());
        }

        @Test
        @DisplayName(
            "Given a page which has been visited before"
                + " And is still on the queue"
                + " Then the URL is not enqueued again")
        public void givenVisitedValueAndCurrentlyInQueue_OfferDoesNotQueuePage() throws Exception {
            // Given
            URL unvisitedUrl = new URL("http://some-value/");
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            // The page has previously been visited
            cacheUnderTest.offer(unvisitedUrl);

            // When
            cacheUnderTest.offer(unvisitedUrl);

            // Then
            assertNotNull(cacheUnderTest.poll());
            assertNull(cacheUnderTest.poll());
        }
    }

    @DisplayName("When isEmpty called")
    @Nested
    public class IsEmptyTest {
        @Test
        @DisplayName(
            "Given no value enqueued"
                + " Then true returned")
        public void givenNoQueuedValue_FalseReturned() throws Exception {

            // Given
            // No enqueued value
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            // When
            boolean result = cacheUnderTest.isEmpty();

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName(
            "Given value enqueued"
                + " Then false returned")
        public void givenQueuedValue_FalseReturned() throws Exception {

            // Given
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();
            cacheUnderTest.offer(new URL("http://www.google.com"));

            // When
            boolean result = cacheUnderTest.isEmpty();

            // Then
            assertFalse(result);
        }
    }

    @DisplayName("When poll called")
    @Nested
    public class PollTest {

        @Test
        @DisplayName(
            "Given no value enqueued"
                + " Then null is returned")
        public void givenNoQueuedValue_ReturnsEmpty() throws Exception {
            // Given
            // No enqueued value
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            // When
            URL result = cacheUnderTest.poll();

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName(
            "Given a queued value"
                + " Then an optional with the value is returned")
        public void givenQueuedValue_ReturnsThatValue() throws Exception {
            // Given
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            URL urlOfInterest = new URL("http://some-value/");
            cacheUnderTest.offer(new URL("http://some-value/"));

            // When
            URL result = cacheUnderTest.poll();

            // Then
            assertEquals(urlOfInterest, result);
        }

        @Test
        @DisplayName(
            "Given a value is in the queue"
                + " Then the value is removed from the queue")
        public void givenQueuedValue_ValueDequeud() throws Exception {
            // Given
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();
            cacheUnderTest.offer(new URL("http://some-value/"));

            // When
            cacheUnderTest.poll();

            // Then
            assertNull(cacheUnderTest.poll());
        }

        @Test
        @DisplayName(
            "Given multiple value is in the queue"
                + " Then the values are removed from the queue on equal polls")
        public void givenMultipleQueuedValues_ValueDequeud() throws Exception {
            // Given
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();
            cacheUnderTest.offer(new URL("http://some-value/"));
            cacheUnderTest.offer(new URL("http://some-other-value/"));

            // When
            cacheUnderTest.poll();
            cacheUnderTest.poll();

            // Then
            assertNull(cacheUnderTest.poll());
        }
    }


    @DisplayName("End to end functional tests")
    @Nested
    public class EndToEndTest {

        @Test
        @DisplayName(
            "End to end test for various functionality")
        public void endToEndTest() throws Exception {
            NonDuplicateQueue cacheUnderTest = new NonDuplicateQueue();

            URL urlA = new URL("http://some-value/");
            URL urlB = new URL("http://some-value/a");
            URL urlC = new URL("http://some-value/a/b/");

            // The queue is initially empty
            assertTrue(cacheUnderTest.isEmpty());

            // Offer two unique values
            cacheUnderTest.offer(urlA);
            cacheUnderTest.offer(urlB);

            // The queue now contains these two values
            assertFalse(cacheUnderTest.isEmpty());
            assertEquals(urlA, cacheUnderTest.poll());
            assertEquals(urlB, cacheUnderTest.poll());

            // After polling both it's empty again
            assertTrue(cacheUnderTest.isEmpty());

            // Offer a unique value
            cacheUnderTest.offer(urlC);

            // And a value which was previously queued
            cacheUnderTest.offer(urlA);

            // Polling the unique value should leave the queue empty
            assertEquals(urlC, cacheUnderTest.poll());
            assertTrue(cacheUnderTest.isEmpty());

            // Finally polling on an empty queue returns null
            assertNull(cacheUnderTest.poll());
        }
    }
}
