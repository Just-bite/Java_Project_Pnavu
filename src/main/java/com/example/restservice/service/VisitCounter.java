package com.example.restservice.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VisitCounter {
    private final Map<String, Integer> counter = new ConcurrentHashMap<>();

    public void increment(String url) {
        counter.compute(url, (key, val) -> val == null ? 1 : val + 1);
    }

    public int getCount(String url) {
        return counter.getOrDefault(url, 0);
    }

    public Map<String, Integer> getAllCounts() {
        return new HashMap<>(counter);
    }
}
