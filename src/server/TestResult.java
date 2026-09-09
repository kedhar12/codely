package server;

public class TestResult {
    public final int testCaseNumber;
    public final boolean passed;
    public final String input;
    public final String expectedOutput;
    public final String actualOutput;
    public final long executionTimeMs;
    public final boolean isHidden;
    public final String error;

    public TestResult(int testCaseNumber, boolean passed, String input, String expectedOutput, String actualOutput, long executionTimeMs, boolean isHidden, String error) {
        this.testCaseNumber = testCaseNumber;
        this.passed = passed;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.executionTimeMs = executionTimeMs;
        this.isHidden = isHidden;
        this.error = error;
    }
}
