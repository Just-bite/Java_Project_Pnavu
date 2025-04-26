package com.example.restservice.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {
    private final Map<String, AtomicLong> urlCounterMap = new ConcurrentHashMap<>();

    public void incrementCounter(String url) {
        urlCounterMap.computeIfAbsent(url, k -> new AtomicLong(0)).incrementAndGet();
    }

    public Map<String, Long> getAllCounts() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        urlCounterMap.forEach((key, value) -> result.put(key, value.get()));
        return result;
    }
}