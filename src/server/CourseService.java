package server;

import java.util.*;

public class CourseService {

    public static class Chapter {
        public final String id;
        public final String title;
        public final String language;
        public final String content;
        public final String starterCode;
        public final String expectedOutput;

        public Chapter(String id, String title, String language, String content, String starterCode, String expectedOutput) {
            this.id = id;
            this.title = title;
            this.language = language;
            this.content = content;
            this.starterCode = starterCode;
            this.expectedOutput = expectedOutput;
        }
    }

    public static class Course {
        public final String id;
        public final String title;
        public final String description;
        public final String icon;
        public final String level;
        public final List<Chapter> chapters;

        public Course(String id, String title, String description, String icon, String level, List<Chapter> chapters) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.level = level;
            this.chapters = chapters;
        }
    }

    private static final Map<String, Course> courses = new LinkedHashMap<>();

    static {
        // Course 1: Java Core
        List<Chapter> javaChapters = List.of(
            new Chapter(
                "java-ch1",
                "1. Java Syntax & Standard I/O",
                "java",
                "### Understanding Java Program Structure\n\nIn Java, all code resides inside a `class`. The entry point for execution is `public static void main(String[] args)`.\n\nUse `System.out.println()` to output to the standard console.\n\n#### Hands-on Task:\nWrite a program that prints your greeting to the Codely platform!",
                "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Welcome to Codely Java Lab!\");\n    }\n}",
                "Welcome to Codely Java Lab!"
            ),
            new Chapter(
                "java-ch2",
                "2. Conditionals & Decision Making",
                "java",
                "### Conditionals in Java\n\nJava uses `if`, `else if`, and `else` blocks with boolean conditions.\n\n#### Hands-on Task:\nGiven an integer `score`, determine if the student passed (score >= 50) or needs re-examination.",
                "public class Main {\n    public static void main(String[] args) {\n        int score = 85;\n        if (score >= 50) {\n            System.out.println(\"PASSED: Grade A\");\n        } else {\n            System.out.println(\"FAILED\");\n        }\n    }\n}",
                "PASSED: Grade A"
            ),
            new Chapter(
                "java-ch3",
                "3. Loops & Sequences",
                "java",
                "### For and While Loops\n\nLoops allow executing a block of statements repeatedly. A standard `for` loop has initialization, condition, and increment/decrement steps.\n\n#### Hands-on Task:\nPrint numbers from 1 to 5 separated by spaces.",
                "public class Main {\n    public static void main(String[] args) {\n        for (int i = 1; i <= 5; i++) {\n            System.out.print(i + (i < 5 ? \" \" : \"\\n\"));\n        }\n    }\n}",
                "1 2 3 4 5"
            ),
            new Chapter(
                "java-ch4",
                "4. Object-Oriented Principles: Classes & Methods",
                "java",
                "### Classes and Objects\n\nA class is a blueprint for creating objects. It defines fields (attributes) and methods (behavior).\n\n#### Hands-on Task:\nCreate a `Rectangle` class with `width` and `height`, and calculate its area.",
                "class Rectangle {\n    int width, height;\n    Rectangle(int w, int h) { this.width = w; this.height = h; }\n    int getArea() { return width * height; }\n}\n\npublic class Main {\n    public static void main(String[] args) {\n        Rectangle rect = new Rectangle(10, 5);\n        System.out.println(\"Area: \" + rect.getArea());\n    }\n}",
                "Area: 50"
            )
        );

        courses.put("course-java", new Course(
            "course-java",
            "Java Programming Masterclass",
            "Master modern Java from syntax and object-oriented architecture to collections and problem-solving.",
            "☕",
            "Beginner to Advanced",
            javaChapters
        ));

        // Course 2: Python
        List<Chapter> pythonChapters = List.of(
            new Chapter(
                "py-ch1",
                "1. Python Essentials & Dynamic Typing",
                "python",
                "### Python Syntax\n\nPython uses indentation instead of curly braces to define code blocks. Variables do not require explicit type declarations.\n\n#### Hands-on Task:\nPrint formatted strings using Python f-strings.",
                "language = 'Python 3'\nplatform = 'Codely'\nprint(f'Learning {language} on {platform}!')",
                "Learning Python 3 on Codely!"
            ),
            new Chapter(
                "py-ch2",
                "2. Lists & List Comprehensions",
                "python",
                "### List Comprehensions\n\nList comprehensions provide a concise way to create lists based on existing iterables.\n\n#### Hands-on Task:\nGenerate squares of even numbers from 1 to 10.",
                "even_squares = [x**2 for x in range(1, 11) if x % 2 == 0]\nprint(even_squares)",
                "[4, 16, 36, 64, 100]"
            ),
            new Chapter(
                "py-ch3",
                "3. Dictionaries & Frequency Counting",
                "python",
                "### Dictionaries in Python\n\nDictionaries store key-value pairs with O(1) average lookup time.\n\n#### Hands-on Task:\nCount character frequencies in a word.",
                "word = 'banana'\nfreq = {}\nfor ch in word:\n    freq[ch] = freq.get(ch, 0) + 1\nfor k in sorted(freq.keys()):\n    print(f'{k}: {freq[k]}')",
                "a: 3\nb: 1\nn: 2"
            )
        );

        courses.put("course-python", new Course(
            "course-python",
            "Python for Algorithms & Data Structures",
            "Learn Python idioms, algorithmic techniques, and data manipulation through interactive exercises.",
            "🐍",
            "All Levels",
            pythonChapters
        ));

        // Course 3: C Programming
        List<Chapter> cChapters = List.of(
            new Chapter(
                "c-ch1",
                "1. C Memory Model & Pointers",
                "c",
                "### Pointers in C\n\nA pointer is a variable that stores the memory address of another variable. The `&` operator gets the address, and `*` dereferences it.\n\n#### Hands-on Task:\nSwap two integers using pointers.",
                "#include <stdio.h>\n\nvoid swap(int *a, int *b) {\n    int temp = *a;\n    *a = *b;\n    *b = temp;\n}\n\nint main() {\n    int x = 10, y = 20;\n    swap(&x, &y);\n    printf(\"x=%d, y=%d\\n\", x, y);\n    return 0;\n}",
                "x=20, y=10"
            ),
            new Chapter(
                "c-ch2",
                "2. Dynamic Memory Allocation with malloc & free",
                "c",
                "### Dynamic Memory Management\n\nAllocate memory on the heap using `malloc()` and release it using `free()` to prevent memory leaks.\n\n#### Hands-on Task:\nAllocate an array of 3 integers dynamically and print their sum.",
                "#include <stdio.h>\n#include <stdlib.h>\n\nint main() {\n    int n = 3;\n    int *arr = (int*)malloc(n * sizeof(int));\n    arr[0] = 5; arr[1] = 15; arr[2] = 25;\n    int sum = 0;\n    for(int i = 0; i < n; i++) sum += arr[i];\n    printf(\"Total Sum = %d\\n\", sum);\n    free(arr);\n    return 0;\n}",
                "Total Sum = 45"
            )
        );

        courses.put("course-c", new Course(
            "course-c",
            "C Systems & Pointer Engineering",
            "Dive into low-level systems programming, memory addressing, pointers, and manual memory management.",
            "⚙️",
            "Intermediate",
            cChapters
        ));

        // Course 4: SQL
        List<Chapter> sqlChapters = List.of(
            new Chapter(
                "sql-ch1",
                "1. Table Creation & Queries",
                "sql",
                "### Creating Tables and Basic SELECT\n\nSQL is the standard language for relational database management systems.\n\n#### Hands-on Task:\nCreate a students table, insert records, and query active students.",
                "CREATE TABLE students (id INT, name TEXT, gpa REAL);\nINSERT INTO students VALUES (1, 'Rahul', 3.8), (2, 'Priya', 3.9), (3, 'Aman', 3.2);\nSELECT name, gpa FROM students WHERE gpa >= 3.5 ORDER BY gpa DESC;\n",
                "+-------+-----+\n| name  | gpa |\n+-------+-----+\n| Priya | 3.9 |\n| Rahul | 3.8 |\n+-------+-----+\n(2 row(s) returned)"
            )
        );

        courses.put("course-sql", new Course(
            "course-sql",
            "Database Systems & SQL Mastery",
            "Master relational schemas, SQL joins, aggregate functions, subqueries, and window functions.",
            "🗄️",
            "Beginner to Intermediate",
            sqlChapters
        ));
    }

    public static Collection<Course> getAllCourses() {
        return courses.values();
    }

    public static Course getCourse(String id) {
        return courses.get(id);
    }
}
