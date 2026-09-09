package server;

public class ExecutionResult {
    public final boolean success;
    public final int exitCode;
    public final String stdout;
    public final String stderr;
    public final long executionTimeMs;
    public final boolean timedOut;
    public final String error;

    public ExecutionResult(boolean success, int exitCode, String stdout, String stderr, long executionTimeMs, boolean timedOut, String error) {
        this.success = success;
        this.exitCode = exitCode;
        this.stdout = stdout != null ? stdout : "";
        this.stderr = stderr != null ? stderr : "";
        this.executionTimeMs = executionTimeMs;
        this.timedOut = timedOut;
        this.error = error != null ? error : "";
    }

    public static ExecutionResult failure(String errorMessage) {
        return new ExecutionResult(false, -1, "", errorMessage, 0, false, errorMessage);
    }

    public static ExecutionResult timeout(long durationMs) {
        return new ExecutionResult(false, -1, "", "Execution Timed Out (Exceeded limit of 5.0 seconds). Check for infinite loops or heavy computation.", durationMs, true, "Timeout");
    }
}
