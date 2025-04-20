package com.example.restservice;

import com.example.restservice.service.VisitCounter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class VisitCounterTest {

    @Test
    void shouldIncrementAndReturnCount() {
        VisitCounter counter = new VisitCounter();

        assertEquals(0, counter.getCount("/test"));

        counter.increment("/test");
        assertEquals(1, counter.getCount("/test"));

        counter.increment("/test");
        assertEquals(2, counter.getCount("/test"));
    }

    @Test
    void shouldHandleConcurrentIncrements() throws InterruptedException {
        final VisitCounter counter = new VisitCounter();
        final int threadsCount = 100;
        final int incrementsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            executor.execute(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment("/concurrent");
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadsCount * incrementsPerThread, counter.getCount("/concurrent"));
    }
}
