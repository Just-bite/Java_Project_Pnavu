package com.example.restservice.service;

import com.example.restservice.model.Song;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    private static final int MAX_CACHE_SIZE = 10;

    @Bean
    public Map<String, List<Song>> songsCache() {
        return new LinkedHashMap<String, List<Song>>() {
            private final Map<String, Integer> usageCounter = new HashMap<>();

            @Override
            public List<Song> put(String key, List<Song> value) {
                usageCounter.put(key, usageCounter.getOrDefault(key, 0) + 1);
                if (size() > MAX_CACHE_SIZE) {
                    removeLFUEntry();
                }
                return super.put(key, value);
            }

            @Override
            public List<Song> get(Object key) {
                if (containsKey(key)) {
                    usageCounter.put((String) key, usageCounter.getOrDefault(key, 0) + 1);
                }
                return super.get(key);
            }

            @Override
            public List<Song> remove(Object key) {
                usageCounter.remove(key);
                return super.remove(key);
            }

            private void removeLFUEntry() {
                String leastUsedKey = usageCounter.entrySet().stream()
                        .min(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);

                if (leastUsedKey != null) {
                    usageCounter.remove(leastUsedKey);
                    remove(leastUsedKey);
                }
            }
        };
    }
}
