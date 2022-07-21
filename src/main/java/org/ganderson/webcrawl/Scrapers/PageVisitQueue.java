package org.ganderson.webcrawl.Scrapers;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

/**
 * Simple queue wrapper, will only enqueue pages which we haven't already visited.
 */
public class PageVisitQueue {

    private final LinkedList<URL> internalQueue = new LinkedList<>();
    private final Set<String> cache = new HashSet<>();

    /**
     * Offers a new page to the queue.
     *
     * @param pageUrl The URL of the page we're offering.
     */
    public void offer(URL pageUrl) {
        if (cache.contains(pageUrl.toString())) {
            return;
        }

        this.cache.add(pageUrl.toString());
        this.internalQueue.offer(pageUrl);
    }

    /**
     * Polls the queue, de-queuing as we go.
     *
     * @return Empty if the queue is empty, otherwise the next page to visit.
     */
    public Optional<URL> poll() {
        return Optional.ofNullable(internalQueue.poll());
    }
}
