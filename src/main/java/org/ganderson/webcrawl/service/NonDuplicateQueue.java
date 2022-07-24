package org.ganderson.webcrawl.service;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Simple queue wrapper, will only enqueue URLs which are new to the queue.
 */
public class NonDuplicateQueue {

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
     * Checks if this queue is empty.
     *
     * @return True if the queue is empty, else false.
     */
    public boolean isEmpty() {
        return this.internalQueue.size() == 0;
    }

    /**
     * Polls the queue, de-queuing as we go.
     *
     * @return Empty if the queue is empty, otherwise the next page to visit.
     */
    public URL poll() {
        return internalQueue.poll();
    }
}
