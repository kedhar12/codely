package server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutionService {
    private static final String TEMP_BASE_DIR = System.getProperty("user.dir") + File.separator + "temp";
    private static final long DEFAULT_TIMEOUT_SECONDS = 6;

    // Cache binary paths
    private static String gccPath = null;
    private static String gppPath = null;
    private static String pythonPath = null;
    private static String nodePath = null;
    private static String localW64Bin = null;

    static {
        // Look for GCC and G++ in known local directories first
        String localW64 = "C:\\Users\\user\\.gemini\\antigravity\\scratch\\w64devkit\\bin";
        if (new File(localW64, "gcc.exe").exists()) {
            localW64Bin = new File(localW64).getAbsolutePath();
            gccPath = new File(localW64, "gcc.exe").getAbsolutePath();
            gppPath = new File(localW64, "g++.exe").getAbsolutePath();
        } else {
            gccPath = "gcc";
            gppPath = "g++";
        }

        // Detect python launcher
        pythonPath = "py";
        nodePath = "node";
    }

    public static ExecutionResult execute(String language, String code, String stdin) {
        if (code == null) code = "";
        if (stdin == null) stdin = "";
        language = language.toLowerCase().trim();

        String runId = UUID.randomUUID().toString();
        File sandboxDir = new File(TEMP_BASE_DIR, "sandbox_" + runId);
        if (!sandboxDir.mkdirs()) {
            // fallback if temp folder didn't exist
            new File(TEMP_BASE_DIR).mkdirs();
            sandboxDir.mkdirs();
        }

        try {
            switch (language) {
                case "java":
                    return runJava(sandboxDir, code, stdin);
                case "python":
                case "py":
                    return runPython(sandboxDir, code, stdin);
                case "javascript":
                case "js":
                case "node":
                    return runJavaScript(sandboxDir, code, stdin);
                case "c":
                    return runC(sandboxDir, code, stdin);
                case "cpp":
                case "c++":
                    return runCpp(sandboxDir, code, stdin);
                case "sql":
                    return runSql(sandboxDir, code, stdin);
                default:
                    return ExecutionResult.failure("Unsupported language: " + language + ". Supported: java, python, javascript, c, cpp, sql");
            }
        } catch (Exception e) {
            return ExecutionResult.failure("System execution error: " + e.getMessage());
        } finally {
            cleanupDirectory(sandboxDir);
        }
    }

    private static ExecutionResult runJava(File dir, String code, String stdin) throws Exception {
        // Detect class name or default to Main
        String className = "Main";
        Pattern pattern = Pattern.compile("public\\s+class\\s+([A-Za-z0-9_]+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            className = matcher.group(1);
        } else if (!code.contains("class ")) {
            // Wrap bare code if user typed snippet
            code = "public class Main {\n    public static void main(String[] args) {\n" + code + "\n    }\n}";
        }

        File srcFile = new File(dir, className + ".java");
        Files.writeString(srcFile.toPath(), code, StandardCharsets.UTF_8);

        // Compile
        ProcessBuilder compilePb = new ProcessBuilder("javac", "-encoding", "UTF-8", srcFile.getName());
        compilePb.directory(dir);
        long compileStart = System.currentTimeMillis();
        ExecutionResult compileRes = executeProcess(compilePb, "", 10);
        if (!compileRes.success || compileRes.exitCode != 0) {
            String err = compileRes.stderr.isEmpty() ? compileRes.stdout : compileRes.stderr;
            return new ExecutionResult(false, compileRes.exitCode, "", "Compilation Error:\n" + err, System.currentTimeMillis() - compileStart, false, "Compilation Failed");
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder("java", "-Dfile.encoding=UTF-8", className);
        runPb.directory(dir);
        return executeProcess(runPb, stdin, DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecutionResult runPython(File dir, String code, String stdin) throws Exception {
        File srcFile = new File(dir, "solution.py");
        Files.writeString(srcFile.toPath(), code, StandardCharsets.UTF_8);

        ProcessBuilder pb = new ProcessBuilder(pythonPath, "-3", "-u", "solution.py");
        pb.directory(dir);
        return executeProcess(pb, stdin, DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecutionResult runJavaScript(File dir, String code, String stdin) throws Exception {
        File srcFile = new File(dir, "solution.js");
        Files.writeString(srcFile.toPath(), code, StandardCharsets.UTF_8);

        ProcessBuilder pb = new ProcessBuilder(nodePath, "solution.js");
        pb.directory(dir);
        return executeProcess(pb, stdin, DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecutionResult runC(File dir, String code, String stdin) throws Exception {
        File srcFile = new File(dir, "solution.c");
        Files.writeString(srcFile.toPath(), code, StandardCharsets.UTF_8);
        File exeFile = new File(dir, "solution.exe");

        // Compile
        ProcessBuilder compilePb = new ProcessBuilder(gccPath, "-O2", "-o", "solution.exe", "solution.c");
        compilePb.directory(dir);
        long compileStart = System.currentTimeMillis();
        ExecutionResult compileRes = executeProcess(compilePb, "", 10);
        if (!compileRes.success || compileRes.exitCode != 0) {
            String err = compileRes.stderr.isEmpty() ? compileRes.stdout : compileRes.stderr;
            return new ExecutionResult(false, compileRes.exitCode, "", "Compilation Error:\n" + err, System.currentTimeMillis() - compileStart, false, "Compilation Failed");
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder(exeFile.getAbsolutePath());
        runPb.directory(dir);
        return executeProcess(runPb, stdin, DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecutionResult runCpp(File dir, String code, String stdin) throws Exception {
        File srcFile = new File(dir, "solution.cpp");
        Files.writeString(srcFile.toPath(), code, StandardCharsets.UTF_8);
        File exeFile = new File(dir, "solution.exe");

        // Compile
        ProcessBuilder compilePb = new ProcessBuilder(gppPath, "-O2", "-o", "solution.exe", "solution.cpp");
        compilePb.directory(dir);
        long compileStart = System.currentTimeMillis();
        ExecutionResult compileRes = executeProcess(compilePb, "", 10);
        if (!compileRes.success || compileRes.exitCode != 0) {
            String err = compileRes.stderr.isEmpty() ? compileRes.stdout : compileRes.stderr;
            return new ExecutionResult(false, compileRes.exitCode, "", "Compilation Error:\n" + err, System.currentTimeMillis() - compileStart, false, "Compilation Failed");
        }

        // Run
        ProcessBuilder runPb = new ProcessBuilder(exeFile.getAbsolutePath());
        runPb.directory(dir);
        return executeProcess(runPb, stdin, DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecutionResult runSql(File dir, String sqlScript, String stdin) throws Exception {
        File sqlFile = new File(dir, "query.sql");
        Files.writeString(sqlFile.toPath(), sqlScript, StandardCharsets.UTF_8);

        // Python SQLite bridge script that parses multiple SQL statements, prints grid tables
        String bridgePy = "import sqlite3, sys\n"
            + "try:\n"
            + "    with open('query.sql', 'r', encoding='utf-8') as f:\n"
            + "        sql = f.read()\n"
            + "    con = sqlite3.connect(':memory:')\n"
            + "    cur = con.cursor()\n"
            + "    # Split statements\n"
            + "    statements = [s.strip() for s in sql.split(';') if s.strip()]\n"
            + "    has_select = False\n"
            + "    for stmt in statements:\n"
            + "        cur.execute(stmt)\n"
            + "        if cur.description:\n"
            + "            has_select = True\n"
            + "            col_names = [d[0] for d in cur.description]\n"
            + "            rows = cur.fetchall()\n"
            + "            # Format grid\n"
            + "            col_widths = [len(c) for c in col_names]\n"
            + "            for row in rows:\n"
            + "                for i, val in enumerate(row):\n"
            + "                    col_widths[i] = max(col_widths[i], len(str(val) if val is not None else 'NULL'))\n"
            + "            sep = '+' + '+'.join(['-' * (w + 2) for w in col_widths]) + '+'\n"
            + "            header = '|' + '|'.join([f' {col_names[i]:<{col_widths[i]}} ' for i in range(len(col_names))]) + '|'\n"
            + "            print(sep)\n"
            + "            print(header)\n"
            + "            print(sep)\n"
            + "            for row in rows:\n"
            + "                line = '|' + '|'.join([f' {str(row[i]) if row[i] is not None else \"NULL\":<{col_widths[i]}} ' for i in range(len(row))]) + '|'\n"
            + "                print(line)\n"
            + "            print(sep)\n"
            + "            print(f'({len(rows)} row(s) returned)\\n')\n"
            + "    con.commit()\n"
            + "    if not has_select:\n"
            + "        print('Query OK, statements executed successfully.')\n"
            + "except Exception as e:\n"
            + "    sys.stderr.write(f'SQL Error: {str(e)}\\n')\n"
            + "    sys.exit(1)\n";

        File bridgeFile = new File(dir, "run_sql.py");
        Files.writeString(bridgeFile.toPath(), bridgePy, StandardCharsets.UTF_8);

        ProcessBuilder pb = new ProcessBuilder(pythonPath, "-3", "-u", "run_sql.py");
        pb.directory(dir);
        return executeProcess(pb, "", DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecutionResult executeProcess(ProcessBuilder pb, String stdin, long timeoutSec) {
        long startTime = System.currentTimeMillis();
        Process process = null;
        try {
            if (localW64Bin != null) {
                String existingPath = pb.environment().get("Path");
                if (existingPath == null) existingPath = pb.environment().get("PATH");
                if (existingPath == null) existingPath = System.getenv("Path");
                if (existingPath == null) existingPath = System.getenv("PATH");
                if (existingPath == null) existingPath = "";
                pb.environment().put("Path", localW64Bin + File.pathSeparator + existingPath);
                pb.environment().put("PATH", localW64Bin + File.pathSeparator + existingPath);
            }
            process = pb.start();

            // Provide stdin
            if (stdin != null && !stdin.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                    writer.write(stdin);
                    writer.flush();
                } catch (IOException ignored) {
                }
            } else {
                process.getOutputStream().close();
            }

            final Process proc = process;
            // Capture streams concurrently
            Future<String> stdoutFuture = Executors.newVirtualThreadPerTaskExecutor().submit(() -> readStream(proc.getInputStream()));
            Future<String> stderrFuture = Executors.newVirtualThreadPerTaskExecutor().submit(() -> readStream(proc.getErrorStream()));

            boolean finished = process.waitFor(timeoutSec, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - startTime;

            if (!finished) {
                process.destroyForcibly();
                return ExecutionResult.timeout(duration);
            }

            String stdout = stdoutFuture.get(1, TimeUnit.SECONDS);
            String stderr = stderrFuture.get(1, TimeUnit.SECONDS);
            int exitCode = process.exitValue();

            return new ExecutionResult(exitCode == 0, exitCode, stdout, stderr, duration, false, exitCode != 0 ? stderr : null);
        } catch (Exception e) {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
            return ExecutionResult.failure("Execution failed: " + e.getMessage());
        }
    }

    private static String readStream(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                if (sb.length() > 500_000) {
                    sb.append("\n...[Output Truncated: Exceeded 500KB]");
                    break;
                }
            }
            return sb.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static void cleanupDirectory(File dir) {
        if (dir == null || !dir.exists()) return;
        try {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) cleanupDirectory(f);
                    else f.delete();
                }
            }
            dir.delete();
        } catch (Exception ignored) {
        }
    }
}
