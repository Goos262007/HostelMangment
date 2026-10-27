package com.hostel.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;

/**
 * HTTP Handler for serving static frontend resources (HTML, CSS, JS).
 */
public class StaticFileHandler implements HttpHandler {
    private final File baseDir;

    public StaticFileHandler(String basePath) {
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir = new File("web");
        }
        this.baseDir = dir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path == null || path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }

        // Prevent path traversal
        path = path.replace("..", "");
        File requestedFile = new File(baseDir, path);

        if (!requestedFile.exists() || requestedFile.isDirectory()) {
            // Check index.html fallback
            requestedFile = new File(baseDir, "index.html");
        }

        if (requestedFile.exists() && !requestedFile.isDirectory()) {
            String mimeType = getContentType(requestedFile.getName());
            byte[] fileBytes = Files.readAllBytes(requestedFile.toPath());

            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        } else {
            String response = "<h1>404 Not Found</h1><p>Resource " + path + " was not found.</p>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private String getContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
