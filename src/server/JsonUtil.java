package server;

import java.util.*;

public class JsonUtil {

    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Collection<?> col) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof ExecutionResult res) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("success", res.success);
            map.put("exitCode", res.exitCode);
            map.put("stdout", res.stdout);
            map.put("stderr", res.stderr);
            map.put("executionTimeMs", res.executionTimeMs);
            map.put("timedOut", res.timedOut);
            map.put("error", res.error);
            return toJson(map);
        }
        if (obj instanceof GradingReport gr) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("allPassed", gr.allPassed);
            map.put("totalTestCases", gr.totalTestCases);
            map.put("passedTestCases", gr.passedTestCases);
            map.put("verdict", gr.verdict);
            map.put("totalTimeMs", gr.totalTimeMs);
            map.put("message", gr.message);
            List<Map<String, Object>> trs = new ArrayList<>();
            for (TestResult tr : gr.testResults) {
                Map<String, Object> trm = new LinkedHashMap<>();
                trm.put("testCaseNumber", tr.testCaseNumber);
                trm.put("passed", tr.passed);
                trm.put("input", tr.input);
                trm.put("expectedOutput", tr.expectedOutput);
                trm.put("actualOutput", tr.actualOutput);
                trm.put("executionTimeMs", tr.executionTimeMs);
                trm.put("isHidden", tr.isHidden);
                trm.put("error", tr.error);
                trs.add(trm);
            }
            map.put("testResults", trs);
            return toJson(map);
        }
        if (obj instanceof Problem p) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.id);
            map.put("title", p.title);
            map.put("difficulty", p.difficulty);
            map.put("category", p.category);
            map.put("description", p.description);
            map.put("inputFormat", p.inputFormat);
            map.put("outputFormat", p.outputFormat);
            map.put("constraints", p.constraints);
            map.put("sampleInput", p.sampleInput);
            map.put("sampleOutput", p.sampleOutput);
            map.put("starterTemplates", p.starterTemplates);
            List<Map<String, Object>> sampleCases = new ArrayList<>();
            for (TestCase tc : p.testCases) {
                if (!tc.isHidden) {
                    Map<String, Object> tcm = new LinkedHashMap<>();
                    tcm.put("input", tc.input);
                    tcm.put("expectedOutput", tc.expectedOutput);
                    tcm.put("explanation", tc.explanation);
                    sampleCases.add(tcm);
                }
            }
            map.put("sampleTestCases", sampleCases);
            return toJson(map);
        }
        if (obj instanceof CourseService.Course c) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", c.id);
            map.put("title", c.title);
            map.put("description", c.description);
            map.put("icon", c.icon);
            map.put("level", c.level);
            List<Map<String, Object>> chs = new ArrayList<>();
            for (CourseService.Chapter ch : c.chapters) {
                Map<String, Object> chm = new LinkedHashMap<>();
                chm.put("id", ch.id);
                chm.put("title", ch.title);
                chm.put("language", ch.language);
                chm.put("content", ch.content);
                chm.put("starterCode", ch.starterCode);
                chm.put("expectedOutput", ch.expectedOutput);
                chs.add(chm);
            }
            map.put("chapters", chs);
            return toJson(map);
        }
        if (obj instanceof AssessmentService.Assessment a) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.id);
            map.put("title", a.title);
            map.put("description", a.description);
            map.put("durationMinutes", a.durationMinutes);
            map.put("totalMarks", a.totalMarks);
            map.put("problemIds", a.problemIds);
            map.put("instructions", a.instructions);
            return toJson(map);
        }
        if (obj instanceof McqQuestion mq) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", mq.id);
            map.put("language", mq.language);
            map.put("question", mq.question);
            map.put("codeSnippet", mq.codeSnippet);
            map.put("options", mq.options);
            map.put("correctIndex", mq.correctIndex);
            map.put("explanation", mq.explanation);
            map.put("difficulty", mq.difficulty);
            return toJson(map);
        }
        if (obj instanceof User u) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.id);
            map.put("name", u.name);
            map.put("username", u.username);
            map.put("email", u.email);
            map.put("streak", u.streak);
            map.put("xp", u.xp);
            map.put("token", u.token);
            map.put("createdAt", u.createdAt);
            return toJson(map);
        }
        if (obj instanceof DashboardService.SubmissionRecord sr) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", sr.id);
            map.put("problemId", sr.problemId);
            map.put("problemTitle", sr.problemTitle);
            map.put("language", sr.language);
            map.put("verdict", sr.verdict);
            map.put("passedCases", sr.passedCases);
            map.put("totalCases", sr.totalCases);
            map.put("timestamp", sr.timestamp);
            map.put("executionTimeMs", sr.executionTimeMs);
            map.put("code", sr.code);
            return toJson(map);
        }

        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    public static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        String hex = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(hex.substring(hex.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Extracts a string field value from a simple flat JSON object
     */
    public static String getString(String json, String fieldName) {
        if (json == null) return "";
        // Match "fieldName"\s*:\s*"((?:[^"\\]|\\.)*)"
        String patternStr = "\"" + java.util.regex.Pattern.quote(fieldName) + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternStr);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        return "";
    }

    public static String unescapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"': sb.append('"'); i += 2; break;
                    case '\\': sb.append('\\'); i += 2; break;
                    case '/': sb.append('/'); i += 2; break;
                    case 'b': sb.append('\b'); i += 2; break;
                    case 'f': sb.append('\f'); i += 2; break;
                    case 'n': sb.append('\n'); i += 2; break;
                    case 'r': sb.append('\r'); i += 2; break;
                    case 't': sb.append('\t'); i += 2; break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            try {
                                int code = Integer.parseInt(s.substring(i + 2, i + 6), 16);
                                sb.append((char) code);
                                i += 6;
                            } catch (NumberFormatException e) {
                                sb.append(c);
                                i++;
                            }
                        } else {
                            sb.append(c);
                            i++;
                        }
                        break;
                    default:
                        sb.append(next);
                        i += 2;
                        break;
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}
