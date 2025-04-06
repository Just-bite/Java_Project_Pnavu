package com.example.restservice.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig<K, V> extends LinkedHashMap<K, V> {
    public static final int MAXSIZE = 3;

    public CacheConfig() {
        super(MAXSIZE + 1, 1.f, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > MAXSIZE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
