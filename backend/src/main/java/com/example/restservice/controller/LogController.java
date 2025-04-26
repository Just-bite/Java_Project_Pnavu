package com.example.restservice.controller;

import com.example.restservice.service.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "Provides async log file operations")
public class LogController {
    private final LogService logService;

    @PostMapping("/filter")
    public CompletableFuture<ResponseEntity<Map<String, String>>>
                            filterLogs(@RequestParam String date) {
        return logService.createFilteredLogFileAsync(date)
                .thenApply(fileId -> ResponseEntity.ok(Map.of(
                        "fileId", fileId,
                        "status", "PROCESSING"
                )));
    }

    @GetMapping("/status/{fileId}")
    public CompletableFuture<ResponseEntity<Map<String, String>>> getStatus(
            @PathVariable String fileId) {
        return logService.getFileStatusAsync(fileId)
                .thenApply(status -> ResponseEntity.ok(Map.of(
                        "fileId", fileId,
                        "status", status
                )));
    }

    @GetMapping("/download/{fileId}")
    public CompletableFuture<ResponseEntity<Resource>> downloadFile(
            @PathVariable String fileId) {
        return logService.getFileAsync(fileId)
                .thenApply(resource -> {
                    String filename = "logs_" + fileId + ".log";

                    return ResponseEntity.ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + filename + "\"")
                            .cacheControl(CacheControl.noCache())
                            .body(resource);
                });
    }

    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogsByDate(@PathVariable String date) throws IOException {
        List<String> filteredLogs = logService.readLogsByDate(date);
        File tempLogFile = logService.createFilteredLogFile(filteredLogs, date);
        return createFileResponse(tempLogFile);
    }

    private ResponseEntity<Resource> createFileResponse(File file) throws IOException {
        UrlResource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}