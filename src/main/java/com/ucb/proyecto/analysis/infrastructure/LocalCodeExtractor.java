package com.ucb.proyecto.analysis.infrastructure;

import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class LocalCodeExtractor {

    public String extractContext(String componentKey, int errorLine) {
        String relativePath = componentKey.contains(":") 
                ? componentKey.substring(componentKey.lastIndexOf(":") + 1) 
                : componentKey;

        try {
            Path path = Paths.get(relativePath);
            if (!Files.exists(path)) {
                return "ERROR_FILE_NOT_FOUND: " + relativePath;
            }

            List<String> lines = Files.readAllLines(path);
            
            int start = Math.max(0, errorLine - 6);
            int end = Math.min(lines.size(), errorLine + 10);
            
            StringBuilder snippet = new StringBuilder();
            snippet.append("ARCHIVO: ").append(relativePath).append("\n");
            snippet.append("LINEA_VULNERABLE: ").append(errorLine).append("\n\n");
            
            for (int i = start; i < end; i++) {
                snippet.append(i + 1).append(" | ").append(lines.get(i)).append("\n");
            }
            
            return snippet.toString();
        } catch (Exception e) {
            return "ERROR_EXTRACTION: " + e.getMessage();
        }
    }
}