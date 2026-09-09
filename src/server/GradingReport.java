package server;

import java.util.List;

public class GradingReport {
    public final boolean allPassed;
    public final int totalTestCases;
    public final int passedTestCases;
    public final String verdict;
    public final long totalTimeMs;
    public final List<TestResult> testResults;
    public final String message;

    public GradingReport(boolean allPassed, int totalTestCases, int passedTestCases, String verdict, long totalTimeMs, List<TestResult> testResults, String message) {
        this.allPassed = allPassed;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.verdict = verdict;
        this.totalTimeMs = totalTimeMs;
        this.testResults = testResults;
        this.message = message;
    }
}
