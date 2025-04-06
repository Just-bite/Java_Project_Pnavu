package com.example.restservice;

import com.example.restservice.service.CacheConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheConfigTest {

    @Test
    void testCacheEviction() {
        CacheConfig<Integer, String> cache = new CacheConfig<>();

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        cache.put(4, "Four");


        assertEquals(3, cache.size());
        assertFalse(cache.containsKey(1));
        assertTrue(cache.containsKey(2));
        assertTrue(cache.containsKey(3));
        assertTrue(cache.containsKey(4));
    }

    @Test
    void testCacheOrder() {
        CacheConfig<Integer, String> cache = new CacheConfig<>();

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        cache.get(1);
        cache.put(4, "Four");

        assertEquals(3, cache.size());
        assertFalse(cache.containsKey(2));
        assertTrue(cache.containsKey(1));
    }

    @Test
    void testEquals() {
        CacheConfig<Integer, String> cache1 = new CacheConfig<>();
        CacheConfig<Integer, String> cache2 = new CacheConfig<>();
        CacheConfig<Integer, String> cache3 = new CacheConfig<>();

        cache1.put(1, "One");
        cache2.put(1, "One");
        cache3.put(2, "Two");

        assertEquals(cache1, cache1);
        assertEquals(cache1, cache2);
        assertNotEquals(cache1, cache3);
        assertNotEquals(null, cache1);
        Object notCache = new Object();
        assertNotEquals(cache1, notCache);
    }

    @Test
    void testHashCode() {
        CacheConfig<Integer, String> cache1 = new CacheConfig<>();
        CacheConfig<Integer, String> cache2 = new CacheConfig<>();

        cache1.put(1, "One");
        cache2.put(1, "One");

        assertEquals(cache1.hashCode(), cache2.hashCode());
    }

    @Test
    void testMaxSizeConstant() {
        assertEquals(3, CacheConfig.MAXSIZE, "Max Cache Size must be 3");
    }
}
