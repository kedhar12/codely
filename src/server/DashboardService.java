package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DashboardService {

    public static class SubmissionRecord {
        public final String id;
        public final String problemId;
        public final String problemTitle;
        public final String language;
        public final String verdict;
        public final int passedCases;
        public final int totalCases;
        public final String timestamp;
        public final long executionTimeMs;
        public final String code;

        public SubmissionRecord(String problemId, String problemTitle, String language, String verdict, int passedCases, int totalCases, long executionTimeMs, String code) {
            this.id = UUID.randomUUID().toString().substring(0, 8);
            this.problemId = problemId;
            this.problemTitle = problemTitle;
            this.language = language;
            this.verdict = verdict;
            this.passedCases = passedCases;
            this.totalCases = totalCases;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"));
            this.executionTimeMs = executionTimeMs;
            this.code = code;
        }
    }

    private static final Set<String> solvedProblems = ConcurrentHashMap.newKeySet();
    private static final Map<String, Integer> languageStats = new ConcurrentHashMap<>();
    private static final List<SubmissionRecord> submissions = new CopyOnWriteArrayList<>();
    private static int totalXp = 120;
    private static int streak = 4;

    static {
        // Initialize initial showcase data for dashboard
        languageStats.put("java", 3);
        languageStats.put("python", 2);
        languageStats.put("c", 1);
        languageStats.put("cpp", 1);
        languageStats.put("sql", 1);
        languageStats.put("javascript", 1);
    }

    public static synchronized void recordSubmission(String problemId, String problemTitle, String language, GradingReport report, String code) {
        SubmissionRecord rec = new SubmissionRecord(
            problemId,
            problemTitle,
            language,
            report.verdict,
            report.passedTestCases,
            report.totalTestCases,
            report.totalTimeMs,
            code
        );
        submissions.add(0, rec); // Add to head

        languageStats.put(language, languageStats.getOrDefault(language, 0) + 1);

        if (report.allPassed) {
            if (!solvedProblems.contains(problemId)) {
                solvedProblems.add(problemId);
                totalXp += 50;
            } else {
                totalXp += 10;
            }
        }
    }

    public static synchronized Map<String, Object> getDashboardData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("solvedCount", solvedProblems.size());
        data.put("totalProblems", ProblemService.getAllProblems().size());
        data.put("totalSubmissions", submissions.size());
        data.put("streak", streak);
        data.put("totalXp", totalXp);
        data.put("solvedProblems", new ArrayList<>(solvedProblems));
        data.put("languageStats", new HashMap<>(languageStats));
        data.put("recentSubmissions", new ArrayList<>(submissions.subList(0, Math.min(submissions.size(), 20))));
        return data;
    }

    public static synchronized void reset() {
        solvedProblems.clear();
        submissions.clear();
        languageStats.clear();
        totalXp = 0;
        streak = 1;
    }
}
