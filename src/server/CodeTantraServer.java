package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;

public class CodeTantraServer {
    private static final int DEFAULT_PORT = 8080;
    private static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "web";

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // API Endpoints
        server.createContext("/api/auth/signup", new AuthSignupHandler());
        server.createContext("/api/auth/login", new AuthLoginHandler());
        server.createContext("/api/auth/me", new AuthMeHandler());
        server.createContext("/api/auth/logout", new AuthLogoutHandler());
        server.createContext("/api/languages", new LanguagesHandler());
        server.createContext("/api/execute", new ExecuteHandler());
        server.createContext("/api/submit", new SubmitHandler());
        server.createContext("/api/problems", new ProblemsHandler());
        server.createContext("/api/courses", new CoursesHandler());
        server.createContext("/api/assessments", new AssessmentsHandler());
        server.createContext("/api/dashboard", new DashboardHandler());
        server.createContext("/api/mcq", new McqHandler());
        server.createContext("/api/interview", new InterviewHandler());
        server.createContext("/api/reset", new ResetHandler());

        // Static Web Files
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.println("==================================================================");
        System.out.println("🚀 Codely - Interactive Multi-Language Compiler & Learning Platform!");
        System.out.println("🌐 URL: http://localhost:" + port);
        System.out.println("⚡ Java Runtime: " + System.getProperty("java.version") + " (Virtual Threads Enabled)");
        System.out.println("💻 Web UI Root: " + WEB_ROOT);
        System.out.println("==================================================================");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {}
    }

    private static void sendCorsAndHeaders(HttpExchange exchange, int statusCode, String contentType, byte[] data) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS, HEAD");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Content-Type", contentType);

        if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(statusCode, -1);
            try (OutputStream os = exchange.getResponseBody()) {
                // close empty body
            }
            return;
        }

        exchange.sendResponseHeaders(statusCode, data.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }

    private static void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = JsonUtil.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        sendCorsAndHeaders(exchange, statusCode, "application/json; charset=UTF-8", bytes);
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // Handlers
    static class AuthSignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 405, "text/plain", "Method Not Allowed".getBytes(StandardCharsets.UTF_8));
                return;
            }

            String body = readRequestBody(exchange);
            String name = JsonUtil.getString(body, "name");
            String username = JsonUtil.getString(body, "username");
            String email = JsonUtil.getString(body, "email");
            String password = JsonUtil.getString(body, "password");

            try {
                User user = AuthService.register(name, username, email, password);
                sendJson(exchange, 200, user);
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, Map.of("error", e.getMessage()));
            } catch (Exception e) {
                sendJson(exchange, 500, Map.of("error", "Server error during registration: " + e.getMessage()));
            }
        }
    }

    static class AuthLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 405, "text/plain", "Method Not Allowed".getBytes(StandardCharsets.UTF_8));
                return;
            }

            String body = readRequestBody(exchange);
            String identifier = JsonUtil.getString(body, "identifier");
            if (identifier.isEmpty()) {
                identifier = JsonUtil.getString(body, "username");
            }
            if (identifier.isEmpty()) {
                identifier = JsonUtil.getString(body, "email");
            }
            String password = JsonUtil.getString(body, "password");

            User user = AuthService.login(identifier, password);
            if (user != null) {
                sendJson(exchange, 200, user);
            } else {
                sendJson(exchange, 401, Map.of("error", "Invalid username/email or password."));
            }
        }
    }

    static class AuthMeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            User user = AuthService.getUserByToken(authHeader);
            if (user != null) {
                sendJson(exchange, 200, user);
            } else {
                sendJson(exchange, 401, Map.of("error", "Unauthorized"));
            }
        }
    }

    static class AuthLogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            AuthService.logout(authHeader);
            sendJson(exchange, 200, Map.of("success", true, "message", "Logged out successfully"));
        }
    }

    static class LanguagesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            List<Map<String, Object>> list = List.of(
                Map.of("id", "java", "name", "Java 23 (OpenJDK)", "mode", "java", "extension", ".java", "version", "23.0.1", "template", "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, CodeTantra!\");\n    }\n}"),
                Map.of("id", "python", "name", "Python 3.14", "mode", "python", "extension", ".py", "version", "3.14.2", "template", "# CodeTantra Python 3 Playground\nname = 'Learner'\nprint(f'Hello, {name}!')\n"),
                Map.of("id", "javascript", "name", "JavaScript (Node.js 24)", "mode", "javascript", "extension", ".js", "version", "24.16.0", "template", "// CodeTantra JavaScript Playground\nconsole.log('Hello from Node.js ' + process.version);\n"),
                Map.of("id", "c", "name", "C (GCC 16.2.0)", "mode", "c", "extension", ".c", "version", "16.2.0", "template", "#include <stdio.h>\n\nint main() {\n    printf(\"Hello from C!\\n\");\n    return 0;\n}\n"),
                Map.of("id", "cpp", "name", "C++ (G++ 16.2.0)", "mode", "cpp", "extension", ".cpp", "version", "16.2.0", "template", "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hello from Modern C++!\" << endl;\n    return 0;\n}\n"),
                Map.of("id", "sql", "name", "SQL (Relational Engine)", "mode", "sql", "extension", ".sql", "version", "SQLite 3", "template", "-- CodeTantra SQL Playground\nCREATE TABLE employees (id INT, name TEXT, role TEXT, salary INT);\nINSERT INTO employees VALUES (1, 'Alice', 'Engineer', 95000), (2, 'Bob', 'Architect', 120000);\nSELECT * FROM employees;\n")
            );

            sendJson(exchange, 200, list);
        }
    }

    static class ExecuteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 405, "text/plain", "Method Not Allowed".getBytes(StandardCharsets.UTF_8));
                return;
            }

            String body = readRequestBody(exchange);
            String language = JsonUtil.getString(body, "language");
            String code = JsonUtil.getString(body, "code");
            String stdin = JsonUtil.getString(body, "stdin");

            ExecutionResult result = ExecutionService.execute(language, code, stdin);
            sendJson(exchange, 200, result);
        }
    }

    static class SubmitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 405, "text/plain", "Method Not Allowed".getBytes(StandardCharsets.UTF_8));
                return;
            }

            String body = readRequestBody(exchange);
            String problemId = JsonUtil.getString(body, "problemId");
            String language = JsonUtil.getString(body, "language");
            String code = JsonUtil.getString(body, "code");

            Problem problem = ProblemService.getProblem(problemId);
            if (problem == null) {
                sendJson(exchange, 404, Map.of("error", "Problem not found: " + problemId));
                return;
            }

            GradingReport report = GradingService.evaluate(language, code, problem.testCases);
            DashboardService.recordSubmission(problem.id, problem.title, language, report, code);

            sendJson(exchange, 200, report);
        }
    }

    static class ProblemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/api/problems/")) {
                String id = path.substring("/api/problems/".length()).trim();
                Problem p = ProblemService.getProblem(id);
                if (p != null) {
                    sendJson(exchange, 200, p);
                } else {
                    sendJson(exchange, 404, Map.of("error", "Problem not found"));
                }
                return;
            }

            sendJson(exchange, 200, ProblemService.getAllProblems());
        }
    }

    static class CoursesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/api/courses/")) {
                String id = path.substring("/api/courses/".length()).trim();
                CourseService.Course c = CourseService.getCourse(id);
                if (c != null) {
                    sendJson(exchange, 200, c);
                } else {
                    sendJson(exchange, 404, Map.of("error", "Course not found"));
                }
                return;
            }

            sendJson(exchange, 200, CourseService.getAllCourses());
        }
    }

    static class AssessmentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/api/assessments/")) {
                String id = path.substring("/api/assessments/".length()).trim();
                AssessmentService.Assessment a = AssessmentService.getAssessment(id);
                if (a != null) {
                    sendJson(exchange, 200, a);
                } else {
                    sendJson(exchange, 404, Map.of("error", "Assessment not found"));
                }
                return;
            }

            sendJson(exchange, 200, AssessmentService.getAllAssessments());
        }
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            sendJson(exchange, 200, DashboardService.getDashboardData());
        }
    }

    static class McqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String lang = "all";
            String difficulty = "all";
            int count = 5;

            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        if ("language".equalsIgnoreCase(pair[0])) {
                            lang = pair[1];
                        } else if ("difficulty".equalsIgnoreCase(pair[0])) {
                            difficulty = pair[1];
                        } else if ("count".equalsIgnoreCase(pair[0])) {
                            try { count = Integer.parseInt(pair[1]); } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }

            List<McqQuestion> questions = McqService.getDynamicQuestions(lang, difficulty, count);
            sendJson(exchange, 200, questions);
        }
    }

    static class InterviewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.endsWith("/meta")) {
                sendJson(exchange, 200, InterviewService.getMetadata());
                return;
            }

            String category = "all";
            String subject = "all";
            String level = "all";
            int limit = 30;

            String query = exchange.getRequestURI().getRawQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        String key = pair[0].toLowerCase();
                        String val = java.net.URLDecoder.decode(pair[1], java.nio.charset.StandardCharsets.UTF_8);
                        if ("category".equals(key)) {
                            category = val;
                        } else if ("subject".equals(key)) {
                            subject = val;
                        } else if ("level".equals(key) || "difficulty".equals(key)) {
                            level = val;
                        } else if ("limit".equals(key) || "count".equals(key)) {
                            try { limit = Integer.parseInt(val); } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }

            List<InterviewService.InterviewQuestion> questions = InterviewService.getQuestions(category, subject, level, limit);
            List<Map<String, Object>> result = questions.stream()
                .map(InterviewService.InterviewQuestion::toMap)
                .toList();
            sendJson(exchange, 200, result);
        }
    }

    static class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndHeaders(exchange, 204, "text/plain", new byte[0]);
                return;
            }

            DashboardService.reset();
            sendJson(exchange, 200, Map.of("success", true, "message", "Dashboard statistics reset."));
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            Path filePath = Paths.get(WEB_ROOT, path.replace('/', File.separatorChar));
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                // Fallback to index.html for SPA client-side routing
                filePath = Paths.get(WEB_ROOT, "index.html");
            }

            if (!Files.exists(filePath)) {
                sendCorsAndHeaders(exchange, 404, "text/plain", "404 Not Found".getBytes(StandardCharsets.UTF_8));
                return;
            }

            String contentType = getMimeType(filePath.getFileName().toString());
            byte[] content = Files.readAllBytes(filePath);
            sendCorsAndHeaders(exchange, 200, contentType, content);
        }

        private static String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "text/plain; charset=UTF-8";
        }
    }
}
