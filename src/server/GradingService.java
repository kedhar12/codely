package server;

import java.util.ArrayList;
import java.util.List;

public class GradingService {

    public static GradingReport evaluate(String language, String code, List<TestCase> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            return new GradingReport(false, 0, 0, "No Test Cases", 0, List.of(), "No test cases configured for this problem.");
        }

        List<TestResult> results = new ArrayList<>();
        int passedCount = 0;
        long totalDuration = 0;
        String finalVerdict = "Accepted";
        String failureReason = "";

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            ExecutionResult res = ExecutionService.execute(language, code, tc.input);
            totalDuration += res.executionTimeMs;

            if (res.timedOut) {
                finalVerdict = "Time Limit Exceeded";
                failureReason = "Time limit exceeded on test case " + (i + 1);
                results.add(new TestResult(i + 1, false, tc.isHidden ? "[Hidden]" : tc.input, tc.isHidden ? "[Hidden]" : tc.expectedOutput, "Time Limit Exceeded", res.executionTimeMs, tc.isHidden, "Execution timed out (limit: 5.0s)"));
                break;
            }

            if (!res.success && res.error.contains("Compilation")) {
                finalVerdict = "Compilation Error";
                failureReason = res.stderr;
                results.add(new TestResult(i + 1, false, tc.isHidden ? "[Hidden]" : tc.input, tc.isHidden ? "[Hidden]" : tc.expectedOutput, "", res.executionTimeMs, tc.isHidden, res.stderr));
                break;
            }

            String normalizedActual = normalize(res.stdout);
            String normalizedExpected = normalize(tc.expectedOutput);

            boolean isMatch = normalizedActual.equals(normalizedExpected);
            if (isMatch) {
                passedCount++;
                results.add(new TestResult(i + 1, true, tc.isHidden ? "[Hidden Input]" : tc.input, tc.isHidden ? "[Hidden Expected]" : tc.expectedOutput, tc.isHidden ? "[Hidden Output]" : res.stdout, res.executionTimeMs, tc.isHidden, null));
            } else {
                if (finalVerdict.equals("Accepted")) {
                    finalVerdict = "Wrong Answer";
                    failureReason = "Wrong answer on test case " + (i + 1);
                }
                results.add(new TestResult(
                    i + 1,
                    false,
                    tc.isHidden ? "[Hidden Input]" : tc.input,
                    tc.isHidden ? "[Hidden Expected]" : tc.expectedOutput,
                    tc.isHidden ? "[Output hidden for evaluation]" : res.stdout,
                    res.executionTimeMs,
                    tc.isHidden,
                    res.stderr.isEmpty() ? null : res.stderr
                ));
            }
        }

        boolean allPassed = passedCount == testCases.size();
        String summaryMessage;
        if (allPassed) {
            summaryMessage = "🎉 Outstanding! All " + testCases.size() + " test cases passed successfully.";
        } else if (finalVerdict.equals("Compilation Error")) {
            summaryMessage = "❌ Compilation Error: Please fix syntax or type errors before re-submitting.";
        } else if (finalVerdict.equals("Time Limit Exceeded")) {
            summaryMessage = "⏱️ Time Limit Exceeded: Your solution exceeded the 5.0s runtime limit.";
        } else {
            summaryMessage = "⚠️ " + passedCount + " of " + testCases.size() + " test cases passed. Review your logic and edge cases.";
        }

        return new GradingReport(allPassed, testCases.size(), passedCount, finalVerdict, totalDuration, results, summaryMessage);
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String[] lines = s.replace("\r\n", "\n").replace("\r", "\n").split("\n");
        List<String> cleaned = new ArrayList<>();
        for (String line : lines) {
            cleaned.add(line.stripTrailing());
        }
        // Remove trailing empty lines
        while (!cleaned.isEmpty() && cleaned.get(cleaned.size() - 1).isEmpty()) {
            cleaned.remove(cleaned.size() - 1);
        }
        return String.join("\n", cleaned);
    }
}
