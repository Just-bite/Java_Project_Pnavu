package com.example.restservice;

import com.example.restservice.service.VisitCounterService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VisitCounterServiceTest {

    private VisitCounterService visitCounterService;

    @BeforeEach
    void setUp() {
        visitCounterService = new VisitCounterService();
    }

    @Test
    void incrementCounter_shouldInitializeCounterOnFirstCall() {
        // Arrange
        String testUrl = "/api/test";

        // Act
        visitCounterService.incrementCounter(testUrl);

        // Assert
        Map<String, Long> counts = visitCounterService.getAllCounts();
        assertThat(counts).containsOnlyKeys(testUrl);
        assertThat(counts.get(testUrl)).isEqualTo(1L);
    }

    @Test
    void incrementCounter_shouldIncrementExistingCounter() {
        // Arrange
        String testUrl = "/api/test";
        visitCounterService.incrementCounter(testUrl); // Первый вызов

        // Act
        visitCounterService.incrementCounter(testUrl); // Второй вызов

        // Assert
        assertThat(visitCounterService.getAllCounts().get(testUrl)).isEqualTo(2L);
    }

    @Test
    void incrementCounter_shouldHandleMultipleUrls() {
        // Arrange
        String url1 = "/api/test1";
        String url2 = "/api/test2";

        // Act
        visitCounterService.incrementCounter(url1);
        visitCounterService.incrementCounter(url2);
        visitCounterService.incrementCounter(url1);

        // Assert
        Map<String, Long> counts = visitCounterService.getAllCounts();
        assertThat(counts)
                .hasSize(2)
                .containsEntry(url1, 2L)
                .containsEntry(url2, 1L);
    }

    @Test
    void getAllCounts_shouldReturnEmptyMapForNoIncrements() {
        // Act
        Map<String, Long> counts = visitCounterService.getAllCounts();

        // Assert
        assertThat(counts).isEmpty();
    }


    @Test
    void incrementCounter_shouldHandleEmptyUrl() {
        // Act
        visitCounterService.incrementCounter("");

        // Assert
        Map<String, Long> counts = visitCounterService.getAllCounts();
        assertThat(counts).containsOnlyKeys("");
        assertThat(counts.get("")).isEqualTo(1L);
    }

    @Test
    void concurrentIncrements_shouldNotLoseCounts() throws InterruptedException {
        // Arrange
        String testUrl = "/api/concurrent";
        int threads = 10;
        int incrementsPerThread = 100;

        // Act
        Runnable task = () -> {
            for (int i = 0; i < incrementsPerThread; i++) {
                visitCounterService.incrementCounter(testUrl);
            }
        };

        Thread[] threadsArray = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            threadsArray[i] = new Thread(task);
            threadsArray[i].start();
        }

        for (Thread thread : threadsArray) {
            thread.join();
        }

        // Assert
        assertThat(visitCounterService.getAllCounts().get(testUrl))
                .isEqualTo(threads * incrementsPerThread);
    }
}