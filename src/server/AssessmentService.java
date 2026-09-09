package server;

import java.util.*;

public class AssessmentService {

    public static class Assessment {
        public final String id;
        public final String title;
        public final String description;
        public final int durationMinutes;
        public final int totalMarks;
        public final List<String> problemIds;
        public final String instructions;

        public Assessment(String id, String title, String description, int durationMinutes, int totalMarks, List<String> problemIds, String instructions) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.durationMinutes = durationMinutes;
            this.totalMarks = totalMarks;
            this.problemIds = problemIds;
            this.instructions = instructions;
        }
    }

    private static final Map<String, Assessment> assessments = new LinkedHashMap<>();

    static {
        assessments.put("lab-exam-101", new Assessment(
            "lab-exam-101",
            "University Lab Assessment 1: Core Problem Solving",
            "Official timed laboratory test evaluating array manipulation, string algorithms, and dynamic programming.",
            45,
            100,
            List.of("two-sum", "palindrome-checker", "fibonacci-dp"),
            "1. You have 45 minutes to solve all 3 coding challenges.\n2. You can code in any language of your choice (Java, Python, C, C++, JavaScript).\n3. Code will be graded automatically against both public and hidden test cases.\n4. Click 'Submit Assessment' once all questions are attempted."
        ));

        assessments.put("dsa-sprint", new Assessment(
            "dsa-sprint",
            "Data Structures & Systems Sprint",
            "Intensive timed assessment covering stack implementations and nested bracket parsers.",
            30,
            60,
            List.of("balanced-parentheses", "two-sum"),
            "1. 30 minutes timed sprint.\n2. Ensure all edge cases are handled before finalizing your submission."
        ));
    }

    public static Collection<Assessment> getAllAssessments() {
        return assessments.values();
    }

    public static Assessment getAssessment(String id) {
        return assessments.get(id);
    }
}
