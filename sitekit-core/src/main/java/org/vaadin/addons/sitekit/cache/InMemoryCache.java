/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.sitekit.cache;

import java.util.LinkedList;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

/**
 * Simple in memory cache.
 *
 * @author Tommi S.E. Laukkanen
 */

public class InMemoryCache<K, T> {
    /**
     * The time to live for all cached object in ms.
     */
    private final long timeToLiveMillis;
    /**
     * The internal map containing cached objects.
     */
    private final LRUMap cacheMap;

    /**
     * Constructor defining time to live, cache evict expired intervals and maximum cached items.
     *
     * @param timeToLiveMillis      the time to live in milliseconds
     * @param evictIntervalMillis the clean up interval in milliseconds
     * @param maxItems              the maximum number of cached items.
     */
    public InMemoryCache(final long timeToLiveMillis, final long evictIntervalMillis, final int maxItems) {
        this.timeToLiveMillis = timeToLiveMillis;

        cacheMap = new LRUMap(maxItems);

        if (this.timeToLiveMillis > 0 && evictIntervalMillis > 0) {

            final Thread evictThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(evictIntervalMillis);
                        } catch (final InterruptedException e) {
                        }
                        evictExpired();
                    }
                }
            });

            evictThread.setDaemon(true);
            evictThread.start();
        }
    }

    /**
     * Puts object in cache.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(K key, T value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheObject(value));
        }
    }

    /**
     * Gets object from cache.
     *
     * @param key the key
     * @return the cached object or null.
     */
    public T get(K key) {
        synchronized (cacheMap) {
            CacheObject c = (CacheObject) cacheMap.get(key);

            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    /**
     * Removes object from cache.
     *
     * @param key the key
     */
    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    /**
     * Gets cache size.
     *
     * @return the size
     */
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    /**
     * Evicts expired objects from cache.
     */
    public void evictExpired() {
        final long now = System.currentTimeMillis();
        final LinkedList<K> expiredKeys = new LinkedList<K>();

        synchronized (cacheMap) {
            final MapIterator itr = cacheMap.mapIterator();
            while (itr.hasNext()) {
                final K key = (K) itr.next();
                final CacheObject cacheObject = (CacheObject) itr.getValue();

                if (cacheObject != null && (now > (timeToLiveMillis + cacheObject.lastAccessed))) {
                    expiredKeys.add(key);
                }
            }
        }

        for (K key : expiredKeys) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }
        }
    }

    /**
     * The cache object containing lastAccess and value.
     */
    private class CacheObject {
        /**
         * The last access time.
         */
        public long lastAccessed = System.currentTimeMillis();
        /**
         * The cached value.
         */
        public final T value;

        /**
         * Constructor which sets the cached value.
         * @param value the cached value
         */
        private CacheObject(final T value) {
            this.value = value;
        }


    }

}