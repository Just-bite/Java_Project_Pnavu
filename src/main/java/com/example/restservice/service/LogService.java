package com.example.restservice.service;

import com.example.restservice.exception.NotFoundException;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class LogService {
    private static final String LOG_FILE_PATH = "logs/app.log";
    private final Map<String, String> fileStatus = new ConcurrentHashMap<>();
    private final Map<String, Path> fileStorage = new ConcurrentHashMap<>();
    private final Executor asyncProcessor = Executors.newFixedThreadPool(5);

    public CompletableFuture<String> createFilteredLogFileAsync(String date) {
        String fileId = "log_" + System.currentTimeMillis();
        fileStatus.put(fileId, "PROCESSING");

        CompletableFuture.runAsync(() -> processFileAsync(fileId, date), asyncProcessor);

        return CompletableFuture.completedFuture(fileId);
    }

    private void processFileAsync(String fileId, String date) {
        try {
            Thread.sleep(20000);

            List<String> logs = readLogsByDate(date);
            File tempFile = createTempLogFile(logs, date);

            fileStorage.put(fileId, tempFile.toPath());
            fileStatus.put(fileId, "READY");
        } catch (Exception e) {
            fileStatus.put(fileId, "ERROR");
        }
    }

    public CompletableFuture<String> getFileStatusAsync(String fileId) {
        return CompletableFuture.supplyAsync(() ->
                fileStatus.getOrDefault(fileId, "NOT_FOUND"));
    }

    public CompletableFuture<UrlResource> getFileAsync(String fileId) { // Изменено на UrlResource
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path filePath = fileStorage.get(fileId);
                if (filePath == null || !Files.exists(filePath)) {
                    throw new NotFoundException("File not found");
                }
                return new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Failed to read file", e);
            }
        });
    }

    public List<String> readLogsByDate(String date) throws IOException {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            throw new NotFoundException("Log file not found");
        }

        List<String> logs = readLogsFromFile(logFile);
        return filterLogsByDate(logs, date);
    }

    public File createFilteredLogFile(List<String> logs, String date) throws IOException {
        return createTempLogFile(logs, date);
    }

    private List<String> readLogsFromFile(File logFile) throws IOException {
        List<String> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        }
        return logs;
    }

    private List<String> filterLogsByDate(List<String> logs, String date) {
        List<String> filteredLogs = new ArrayList<>();
        for (String log : logs) {
            if (log.startsWith(date)) {
                filteredLogs.add(log);
            }
        }
        if (filteredLogs.isEmpty()) {
            throw new NotFoundException("No logs found for date: " + date);
        }
        return filteredLogs;
    }

    private File createTempLogFile(List<String> logs, String date) throws IOException {
        String safeDate = sanitizeFilename(date);
        Path tempDir = getValidTempDir();
        Path tempFile = createSecureTempFile(tempDir, safeDate);

        writeLogsToFile(logs, tempFile);
        tempFile.toFile().deleteOnExit();

        return tempFile.toFile();
    }

    private Path getValidTempDir() throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
        if (!Files.isDirectory(tempDir)) {
            throw new IOException("Temporary directory is not valid");
        }
        return tempDir;
    }

    private Path createSecureTempFile(Path tempDir, String safeDate) throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "logs-" + safeDate + "-", ".log");
        try {
            Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException e) {
            // Windows не поддерживает POSIX права
        }
        return tempFile;
    }

    private void writeLogsToFile(List<String> logs, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.WRITE)) {
            for (String log : logs) {
                writer.write(log);
                writer.newLine();
            }
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
