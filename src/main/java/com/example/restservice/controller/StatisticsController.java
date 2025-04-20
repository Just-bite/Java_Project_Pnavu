package com.example.restservice.controller;

import com.example.restservice.service.VisitCounterService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final VisitCounterService visitCounterService;

    @Autowired
    public StatisticsController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/visits")
    public Map<String, Long> getVisitStatistics() {
        return visitCounterService.getAllCounts();
    }
}