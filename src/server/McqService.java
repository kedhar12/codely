package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class McqService {

    private static final Map<String, List<McqQuestion>> languageBanks = new HashMap<>();

    static {
        // ========== JAVA MCQs ==========
        List<McqQuestion> javaList = new ArrayList<>();
        javaList.add(new McqQuestion(
            "java-1", "java",
            "What will be the output of the following Java code?",
            "String s1 = \"hello\";\nString s2 = new String(\"hello\");\nSystem.out.println((s1 == s2) + \" \" + s1.equals(s2));",
            List.of("false true", "true true", "false false", "true false"),
            0,
            "`==` compares object references (s1 is in string pool, s2 is on the heap), whereas `.equals()` compares string content.",
            "Easy"
        ));
        javaList.add(new McqQuestion(
            "java-2", "java",
            "Which feature introduced in modern Java (Java 21+) enables lightweight concurrency managed by the JVM rather than OS threads?",
            null,
            List.of("Virtual Threads", "ForkJoinPool", "Reactive Streams", "Green Threads"),
            0,
            "Virtual Threads (Project Loom) in Java 21+ allow creating millions of concurrent lightweight threads with near-zero overhead.",
            "Medium"
        ));
        javaList.add(new McqQuestion(
            "java-3", "java",
            "What is the output of this static block execution?",
            "class Demo {\n    static int x = 10;\n    static { x += 5; }\n    public static void main(String[] args) {\n        System.out.println(Demo.x);\n    }\n}",
            List.of("15", "10", "5", "Compilation Error"),
            0,
            "Static initialization blocks execute once when the class is loaded into the JVM, setting x to 10 + 5 = 15.",
            "Easy"
        ));
        javaList.add(new McqQuestion(
            "java-4", "java",
            "Why does a finally block always execute in Java?",
            null,
            List.of(
                "To ensure critical resource cleanup (e.g. closing streams) regardless of whether an exception was thrown",
                "To catch uncaught checked exceptions automatically",
                "To speed up garbage collection invocation",
                "To restart failed threads"
            ),
            0,
            "The finally block is guaranteed to execute (unless System.exit() is called) to guarantee deterministic resource cleanup.",
            "Easy"
        ));
        javaList.add(new McqQuestion(
            "java-5", "java",
            "Which collection class does NOT permit null keys or null values in Java?",
            null,
            List.of("ConcurrentHashMap", "HashMap", "LinkedHashMap", "TreeMap"),
            0,
            "ConcurrentHashMap throws NullPointerException if either key or value is null to avoid ambiguous lookups in multithreaded environments.",
            "Medium"
        ));
        javaList.add(new McqQuestion(
            "java-6", "java",
            "What is the result of autoboxing when comparing two Integer objects with values of 100 vs 200?",
            "Integer a = 100, b = 100;\nInteger c = 200, d = 200;\nSystem.out.println((a == b) + \" \" + (c == d));",
            List.of("true false", "true true", "false false", "false true"),
            0,
            "Java caches Integer objects between -128 and 127. Values outside this range create distinct object instances on the heap.",
            "Hard"
        ));
        languageBanks.put("java", javaList);

        // ========== PYTHON MCQs ==========
        List<McqQuestion> pyList = new ArrayList<>();
        pyList.add(new McqQuestion(
            "py-1", "python",
            "What is the output of the following default argument snippet in Python?",
            "def append_to(num, target=[]):\n    target.append(num)\n    return target\n\nprint(append_to(1))\nprint(append_to(2))",
            List.of("[1] then [1, 2]", "[1] then [2]", "[1] then [1]", "[1] then []"),
            0,
            "Default arguments in Python are evaluated once when the function is defined, not every time it is called. The list is mutable and shared.",
            "Medium"
        ));
        pyList.add(new McqQuestion(
            "py-2", "python",
            "What does the Global Interpreter Lock (GIL) in CPython do?",
            null,
            List.of(
                "Prevents multiple native threads from executing Python bytecode simultaneously",
                "Locks variables globally across all modules",
                "Restricts file access across processes",
                "Encrypts Python bytecode in memory"
            ),
            0,
            "The GIL is a mutex that protects access to Python objects, preventing multiple threads from executing Python bytecodes at once.",
            "Medium"
        ));
        pyList.add(new McqQuestion(
            "py-3", "python",
            "What is the result of evaluating: `[i for i in range(5) if i % 2 == 0]`?",
            null,
            List.of("[0, 2, 4]", "[2, 4]", "[0, 1, 2, 3, 4]", "[1, 3]"),
            0,
            "The comprehension filters range(5) (0, 1, 2, 3, 4) for even numbers where i % 2 == 0, yielding [0, 2, 4].",
            "Easy"
        ));
        pyList.add(new McqQuestion(
            "py-4", "python",
            "Which of the following data types in Python is IMMUTABLE?",
            null,
            List.of("tuple", "list", "dict", "set"),
            0,
            "Tuples are immutable in Python; once initialized, their elements cannot be reassigned or modified.",
            "Easy"
        ));
        pyList.add(new McqQuestion(
            "py-5", "python",
            "What will `print(type(lambda x: x))` output in Python?",
            null,
            List.of("<class 'function'>", "<class 'lambda'>", "<class 'expression'>", "<class 'object'>"),
            0,
            "Lambda expressions in Python construct instances of the standard function type.",
            "Easy"
        ));
        languageBanks.put("python", pyList);

        // ========== JAVASCRIPT MCQs ==========
        List<McqQuestion> jsList = new ArrayList<>();
        jsList.add(new McqQuestion(
            "js-1", "javascript",
            "What is the output of `console.log(typeof NaN);` in JavaScript?",
            null,
            List.of("\"number\"", "\"NaN\"", "\"undefined\"", "\"object\""),
            0,
            "In JavaScript, NaN (Not-a-Number) is technically a numeric value according to the IEEE 754 floating-point standard.",
            "Easy"
        ));
        jsList.add(new McqQuestion(
            "js-2", "javascript",
            "What is the output order in the JavaScript Event Loop?",
            "console.log('1');\nsetTimeout(() => console.log('2'), 0);\nPromise.resolve().then(() => console.log('3'));\nconsole.log('4');",
            List.of("1, 4, 3, 2", "1, 2, 3, 4", "1, 4, 2, 3", "1, 3, 4, 2"),
            0,
            "Synchronous code runs first (1, 4), followed by Microtasks/Promises (3), and finally Macrotasks/setTimeout (2).",
            "Hard"
        ));
        jsList.add(new McqQuestion(
            "js-3", "javascript",
            "What is the difference between `==` and `===` in JavaScript?",
            null,
            List.of(
                "`===` checks both value and type without type coercion, whereas `==` coerces types",
                "`==` is faster than `===`",
                "`===` only works for objects",
                "`==` performs memory address comparison"
            ),
            0,
            "`===` is strict equality: it returns false if types differ. `==` applies implicit type coercion before comparison.",
            "Easy"
        ));
        jsList.add(new McqQuestion(
            "js-4", "javascript",
            "What is a closure in JavaScript?",
            null,
            List.of(
                "A function bundled together with references to its surrounding lexical environment",
                "A method that closes a database connection",
                "An anonymous callback function",
                "A private class constructor"
            ),
            0,
            "A closure gives an inner function access to an outer function's scope even after the outer function has returned.",
            "Medium"
        ));
        languageBanks.put("javascript", jsList);

        // ========== C MCQs ==========
        List<McqQuestion> cList = new ArrayList<>();
        cList.add(new McqQuestion(
            "c-1", "c",
            "What does the `sizeof` operator return when applied to an integer pointer on a 64-bit architecture?",
            "int *ptr = NULL;\nprintf(\"%zu\", sizeof(ptr));",
            List.of("8", "4", "2", "Undefined"),
            0,
            "On any modern 64-bit system, memory addresses are 64 bits (8 bytes) long, so any pointer is 8 bytes.",
            "Easy"
        ));
        cList.add(new McqQuestion(
            "c-2", "c",
            "What happens when you allocate memory using `malloc()` but never call `free()` before program exit?",
            null,
            List.of(
                "A memory leak occurs on the heap during execution",
                "The compiler gives a syntax warning",
                "The program crashes immediately",
                "The garbage collector frees it automatically"
            ),
            0,
            "C does not have garbage collection. Neglecting free() causes memory leaks that exhaust heap memory in long-running processes.",
            "Easy"
        ));
        cList.add(new McqQuestion(
            "c-3", "c",
            "What does the `static` keyword mean when applied to a global variable in C?",
            null,
            List.of(
                "The variable's scope is restricted to the translation unit (file) where it is defined",
                "The variable becomes immutable/constant",
                "The variable is placed in register memory",
                "The variable is destroyed when main() finishes"
            ),
            0,
            "Static globals have internal linkage, meaning they cannot be accessed or externed from other source files.",
            "Medium"
        ));
        cList.add(new McqQuestion(
            "c-4", "c",
            "What is the result of pointer arithmetic on `int arr[5]; int *p = arr; p += 2;`?",
            null,
            List.of(
                "p points to arr[2], advancing by 2 * sizeof(int) bytes",
                "p points to arr[1]",
                "p increments the memory address by exactly 2 bytes",
                "p produces a compilation error"
            ),
            0,
            "Adding an integer k to a pointer of type T* advances the memory address by k * sizeof(T) bytes, pointing to arr[2].",
            "Medium"
        ));
        languageBanks.put("c", cList);

        // ========== C++ MCQs ==========
        List<McqQuestion> cppList = new ArrayList<>();
        cppList.add(new McqQuestion(
            "cpp-1", "cpp",
            "What is the primary benefit of using `std::unique_ptr` in modern C++?",
            null,
            List.of(
                "Guarantees exclusive ownership and automatically frees memory when out of scope without reference count overhead",
                "Allows multiple pointers to share ownership safely",
                "Enables multithreaded thread-safe writes",
                "Compiles faster than raw pointers"
            ),
            0,
            "std::unique_ptr enforces exclusive ownership semantics (RAII). When it leaves scope, its destructor invokes delete automatically with zero overhead.",
            "Medium"
        ));
        cppList.add(new McqQuestion(
            "cpp-2", "cpp",
            "Why should a base class destructor always be declared `virtual` in C++?",
            null,
            List.of(
                "To ensure the derived class destructor is called when deleting an object through a base class pointer",
                "To allow private inheritance",
                "To allocate the object on the heap rather than the stack",
                "To enable friend class access"
            ),
            0,
            "Deleting a derived object through a base pointer without a virtual destructor causes undefined behavior as derived cleanup is skipped.",
            "Hard"
        ));
        cppList.add(new McqQuestion(
            "cpp-3", "cpp",
            "What is the concept of RAII in C++?",
            null,
            List.of(
                "Resource Acquisition Is Initialization: tying resource lifetime to object lifetime",
                "Rapid Application Iteration Interface",
                "Runtime Algorithm Inline Inspection",
                "Recursive Array Index Initialization"
            ),
            0,
            "RAII ties acquisition of resources (memory, sockets, file handles) to constructor initialization and release to destructor destruction.",
            "Easy"
        ));
        cppList.add(new McqQuestion(
            "cpp-4", "cpp",
            "What does `std::move` actually do in C++11 and later?",
            null,
            List.of(
                "It performs an unconditional static_cast to an rvalue reference, enabling move semantics",
                "It physically copies bytes from one memory location to another",
                "It zeroes out the source object's memory immediately",
                "It spawns a background thread to transfer data"
            ),
            0,
            "std::move doesn't move anything at runtime; it merely casts an lvalue to an rvalue reference so a move constructor or assignment operator can be matched.",
            "Hard"
        ));
        languageBanks.put("cpp", cppList);

        // ========== SQL MCQs ==========
        List<McqQuestion> sqlList = new ArrayList<>();
        sqlList.add(new McqQuestion(
            "sql-1", "sql",
            "What is the difference between `WHERE` and `HAVING` in SQL?",
            null,
            List.of(
                "`WHERE` filters rows before aggregation; `HAVING` filters groups after `GROUP BY` aggregation",
                "`HAVING` is faster than `WHERE`",
                "`WHERE` can only be used with primary keys",
                "`HAVING` is deprecated in modern SQL standards"
            ),
            0,
            "WHERE operates on individual records before group aggregates are computed. HAVING filters the aggregated summary rows.",
            "Easy"
        ));
        sqlList.add(new McqQuestion(
            "sql-2", "sql",
            "Which JOIN type returns all rows from the left table and matched rows from the right table, filling with NULL where unmatched?",
            null,
            List.of("LEFT OUTER JOIN", "INNER JOIN", "CROSS JOIN", "RIGHT OUTER JOIN"),
            0,
            "LEFT JOIN retains every record from the left table, joining matching right rows or providing NULL values if no match exists.",
            "Easy"
        ));
        sqlList.add(new McqQuestion(
            "sql-3", "sql",
            "What is the proper way to check if a column contains a NULL value in SQL?",
            null,
            List.of("column_name IS NULL", "column_name = NULL", "column_name == NULL", "column_name.isNull()"),
            0,
            "In ANSI SQL, NULL represents an unknown value. Comparing NULL using `=` returns UNKNOWN (false in WHERE), so `IS NULL` is required.",
            "Easy"
        ));
        sqlList.add(new McqQuestion(
            "sql-4", "sql",
            "What do the letters in ACID transaction properties stand for in relational databases?",
            null,
            List.of(
                "Atomicity, Consistency, Isolation, Durability",
                "Access, Concurrency, Indexing, Data",
                "Authentication, Control, Integrity, Distribution",
                "Asynchronous, Compiled, Indexed, Distributed"
            ),
            0,
            "ACID guarantees that database transactions are processed reliably: Atomicity, Consistency, Isolation, Durability.",
            "Medium"
        ));
        languageBanks.put("sql", sqlList);
    }

    public static List<McqQuestion> getDynamicQuestions(String language, int requestedCount) {
        if (language == null || language.isEmpty()) language = "all";
        language = language.toLowerCase().trim();

        List<McqQuestion> candidates = new ArrayList<>();

        if ("all".equals(language)) {
            for (List<McqQuestion> list : languageBanks.values()) {
                candidates.addAll(list);
            }
            // Try fetching extra real-time questions from Open Trivia DB API
            List<McqQuestion> apiQuestions = fetchFromOpenTriviaApi(3);
            candidates.addAll(apiQuestions);
        } else {
            List<McqQuestion> bank = languageBanks.get(language);
            if (bank != null) {
                candidates.addAll(bank);
            } else {
                // Fallback to all
                for (List<McqQuestion> list : languageBanks.values()) {
                    candidates.addAll(list);
                }
            }
        }

        // Shuffle questions randomly
        Collections.shuffle(candidates);

        int count = Math.min(requestedCount, candidates.size());
        List<McqQuestion> selected = new ArrayList<>(candidates.subList(0, count));

        // Randomize the 4 options for each question so choice positions change every time
        List<McqQuestion> shuffledQuestions = new ArrayList<>();
        for (McqQuestion q : selected) {
            String correctText = q.options.get(q.correctIndex);
            List<String> randomizedOptions = new ArrayList<>(q.options);
            Collections.shuffle(randomizedOptions);
            int newCorrectIndex = randomizedOptions.indexOf(correctText);

            shuffledQuestions.add(new McqQuestion(
                q.id,
                q.language,
                q.question,
                q.codeSnippet,
                randomizedOptions,
                newCorrectIndex,
                q.explanation,
                q.difficulty
            ));
        }

        return shuffledQuestions;
    }

    private static List<McqQuestion> fetchFromOpenTriviaApi(int amount) {
        List<McqQuestion> list = new ArrayList<>();
        try {
            URI uri = URI.create("https://opentdb.com/api.php?amount=" + amount + "&category=18&type=multiple");
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String json = sb.toString();

                    // Parse simple opentdb json
                    // Matches "question":"...", "correct_answer":"...", "incorrect_answers":["...","...","..."]
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                        "\"question\"\\s*:\\s*\"([^\"]+)\".*?\"correct_answer\"\\s*:\\s*\"([^\"]+)\".*?\"incorrect_answers\"\\s*:\\s*\\[([^\\]]+)\\]"
                    );
                    java.util.regex.Matcher m = p.matcher(json);
                    int idCounter = 1;
                    while (m.find()) {
                        String qText = decodeHtmlEntities(m.group(1));
                        String correctAns = decodeHtmlEntities(m.group(2));
                        String incArrStr = m.group(3);

                        List<String> options = new ArrayList<>();
                        options.add(correctAns);
                        for (String item : incArrStr.split(",")) {
                            String cleaned = item.trim().replaceAll("^\"|\"$", "");
                            options.add(decodeHtmlEntities(cleaned));
                        }

                        if (options.size() >= 4) {
                            list.add(new McqQuestion(
                                "api-" + idCounter++,
                                "general",
                                qText,
                                null,
                                options.subList(0, 4),
                                0, // initially 0, gets shuffled in getDynamicQuestions
                                "Fetched dynamically from Open Trivia Computer Science API.",
                                "Medium"
                            ));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // If offline or network timeout, candidate questions are seamlessly served from the built-in pool
        }
        return list;
    }

    private static String decodeHtmlEntities(String s) {
        if (s == null) return "";
        return s.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&rsquo;", "'");
    }
}
