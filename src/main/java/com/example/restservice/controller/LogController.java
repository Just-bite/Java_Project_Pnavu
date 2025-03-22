package com.example.restservice.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    // Основной файл логов
    private static final String LOG_FILE_PATH = "logs/app.log";

    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogsByDate(@PathVariable String date) throws IOException {
        // Проверяем, существует ли основной файл логов
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            return ResponseEntity.notFound().build(); // Возвращаем 404, если файл не найден
        }

        // Читаем логи из основного файла
        List<String> logs = readLogsFromFile(logFile);

        // Фильтруем логи по указанной дате
        List<String> filteredLogs = filterLogsByDate(logs, date);

        // Если логи за указанную дату не найдены, возвращаем 404
        if (filteredLogs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Создаём временный файл с отфильтрованными логами
        File tempLogFile = createTempLogFile(filteredLogs, date);

        // Возвращаем временный файл
        return createFileResponse(tempLogFile);
    }

    // Чтение логов из основного файла
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

    // Фильтрация логов по дате
    private List<String> filterLogsByDate(List<String> logs, String date) {
        List<String> filteredLogs = new ArrayList<>();
        for (String log : logs) {
            if (log.contains(date)) { // Проверяем, содержит ли строка лога указанную дату
                filteredLogs.add(log);
            }
        }
        return filteredLogs;
    }

    // Создание временного файла с отфильтрованными логами
    private File createTempLogFile(List<String> logs, String date) throws IOException {
        // Очищаем дату от недопустимых символов
        String safeDate = sanitizeFilename(date);

        // Создаём временный файл с безопасным именем
        File tempFile = File.createTempFile("logs-" + safeDate, ".log");
        tempFile.deleteOnExit(); // Удаляем файл после завершения работы приложения

        // Записываем отфильтрованные логи во временный файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (String log : logs) {
                writer.write(log);
                writer.newLine();
            }
        }

        return tempFile;
    }

    // Очистка имени файла от недопустимых символов
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unknown";
        }

        // Удаляем все недопустимые символы
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    // Создание ответа с файлом
    private ResponseEntity<Resource> createFileResponse(File file) throws MalformedURLException {
        Path path = Paths.get(file.getAbsolutePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                                                                + resource.getFilename() + "\"")
                .body(resource);
    }
}