-- ============================================================================
-- CODELY ENTERPRISE PLATFORM - MYSQL DATABASE SCHEMA & INITIAL SEED DATA
-- Database Name: codely_db
-- Compatibility: MySQL 8.0+, MariaDB 10.5+
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `codely_db`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `codely_db`;

-- Drop existing tables in reverse dependency order
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `user_progress`;
DROP TABLE IF EXISTS `submissions`;
DROP TABLE IF EXISTS `test_cases`;
DROP TABLE IF EXISTS `starter_templates`;
DROP TABLE IF EXISTS `chapters`;
DROP TABLE IF EXISTS `courses`;
DROP TABLE IF EXISTS `mcq_questions`;
DROP TABLE IF EXISTS `problems`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 1. USERS TABLE
-- ============================================================================
CREATE TABLE `users` (
    `id` VARCHAR(64) NOT NULL,
    `username` VARCHAR(64) NOT NULL UNIQUE,
    `email` VARCHAR(128) NOT NULL UNIQUE,
    `full_name` VARCHAR(128) NOT NULL,
    `password_hash` VARCHAR(128) NOT NULL,
    `salt` VARCHAR(64) NOT NULL,
    `streak_days` INT NOT NULL DEFAULT 1,
    `experience_xp` INT NOT NULL DEFAULT 0,
    `role` ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 2. PROBLEMS TABLE (Practice Lab & Coding Challenges)
-- ============================================================================
CREATE TABLE `problems` (
    `id` VARCHAR(64) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `difficulty` ENUM('Easy', 'Medium', 'Hard') NOT NULL DEFAULT 'Medium',
    `category` VARCHAR(64) NOT NULL,
    `description` TEXT NOT NULL,
    `input_format` TEXT,
    `output_format` TEXT,
    `constraints_text` TEXT,
    `sample_input` TEXT,
    `sample_output` TEXT,
    `max_score` INT NOT NULL DEFAULT 50,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_problems_difficulty` (`difficulty`),
    INDEX `idx_problems_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 3. TEST CASES TABLE (Automated Grading Suites)
-- ============================================================================
CREATE TABLE `test_cases` (
    `id` INT AUTO_INCREMENT NOT NULL,
    `problem_id` VARCHAR(64) NOT NULL,
    `input_data` TEXT NOT NULL,
    `expected_output` TEXT NOT NULL,
    `is_hidden` BOOLEAN NOT NULL DEFAULT FALSE,
    `description` VARCHAR(255),
    PRIMARY KEY (`id`),
    INDEX `idx_testcases_problem` (`problem_id`),
    CONSTRAINT `fk_testcases_problem`
        FOREIGN KEY (`problem_id`) REFERENCES `problems` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 4. STARTER TEMPLATES TABLE (Multi-Language Starter Code)
-- ============================================================================
CREATE TABLE `starter_templates` (
    `id` INT AUTO_INCREMENT NOT NULL,
    `problem_id` VARCHAR(64) NOT NULL,
    `language` VARCHAR(32) NOT NULL,
    `starter_code` MEDIUMTEXT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_problem_language` (`problem_id`, `language`),
    CONSTRAINT `fk_templates_problem`
        FOREIGN KEY (`problem_id`) REFERENCES `problems` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 5. SUBMISSIONS TABLE (Code Evaluation Records & History)
-- ============================================================================
CREATE TABLE `submissions` (
    `id` VARCHAR(64) NOT NULL,
    `user_id` VARCHAR(64) NOT NULL,
    `problem_id` VARCHAR(64) NOT NULL,
    `language` VARCHAR(32) NOT NULL,
    `source_code` MEDIUMTEXT NOT NULL,
    `verdict` VARCHAR(64) NOT NULL,
    `passed_test_cases` INT NOT NULL DEFAULT 0,
    `total_test_cases` INT NOT NULL DEFAULT 0,
    `execution_time_ms` BIGINT NOT NULL DEFAULT 0,
    `submitted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_submissions_user` (`user_id`),
    INDEX `idx_submissions_problem` (`problem_id`),
    CONSTRAINT `fk_submissions_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_submissions_problem`
        FOREIGN KEY (`problem_id`) REFERENCES `problems` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 6. COURSES TABLE
-- ============================================================================
CREATE TABLE `courses` (
    `id` VARCHAR(64) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `category` VARCHAR(64) NOT NULL,
    `description` TEXT,
    `icon` VARCHAR(32),
    `total_chapters` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 7. CHAPTERS TABLE (Interactive Course Curriculum)
-- ============================================================================
CREATE TABLE `chapters` (
    `id` VARCHAR(64) NOT NULL,
    `course_id` VARCHAR(64) NOT NULL,
    `order_index` INT NOT NULL DEFAULT 1,
    `title` VARCHAR(255) NOT NULL,
    `content_markdown` MEDIUMTEXT NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_chapters_course` (`course_id`),
    CONSTRAINT `fk_chapters_course`
        FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 8. MCQ QUESTIONS TABLE (Interactive Quiz Banks)
-- ============================================================================
CREATE TABLE `mcq_questions` (
    `id` VARCHAR(64) NOT NULL,
    `language` VARCHAR(32) NOT NULL,
    `difficulty` ENUM('Easy', 'Medium', 'Hard') NOT NULL DEFAULT 'Medium',
    `question_text` TEXT NOT NULL,
    `code_snippet` TEXT,
    `option_a` VARCHAR(255) NOT NULL,
    `option_b` VARCHAR(255) NOT NULL,
    `option_c` VARCHAR(255) NOT NULL,
    `option_d` VARCHAR(255) NOT NULL,
    `correct_option_index` TINYINT NOT NULL DEFAULT 0,
    `explanation` TEXT,
    PRIMARY KEY (`id`),
    INDEX `idx_mcq_language` (`language`),
    INDEX `idx_mcq_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 9. USER PROGRESS & SOLVED CHALLENGES
-- ============================================================================
CREATE TABLE `user_progress` (
    `id` INT AUTO_INCREMENT NOT NULL,
    `user_id` VARCHAR(64) NOT NULL,
    `problem_id` VARCHAR(64) NOT NULL,
    `is_solved` BOOLEAN NOT NULL DEFAULT FALSE,
    `best_execution_time_ms` BIGINT DEFAULT NULL,
    `earned_score` INT NOT NULL DEFAULT 0,
    `last_attempted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_problem` (`user_id`, `problem_id`),
    CONSTRAINT `fk_progress_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_progress_problem`
        FOREIGN KEY (`problem_id`) REFERENCES `problems` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SEED DATA INSERTION
-- ============================================================================

-- Seed Users (Demo Student & Admin)
INSERT INTO `users` (`id`, `username`, `email`, `full_name`, `password_hash`, `salt`, `streak_days`, `experience_xp`, `role`) VALUES
('usr-demo-01', 'demo_learner', 'demo@codely.dev', 'Demo Learner', '0c0ca0607db7cf79185a6907ee656c123df6248386cceef7455ee98a3c8a994c', 'f0e1d2c3b4a596877896a5b4c3d2e1f0', 5, 250, 'STUDENT'),
('usr-admin-01', 'admin', 'admin@codely.dev', 'Platform Administrator', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'a1b2c3d4e5f60718293a4b5c6d7e8f90', 12, 1200, 'ADMIN');

-- Seed Problems
INSERT INTO `problems` (`id`, `title`, `difficulty`, `category`, `description`, `input_format`, `output_format`, `constraints_text`, `sample_input`, `sample_output`, `max_score`) VALUES
('two-sum', 'Two Sum', 'Medium', 'Arrays', 
'Given an array of integers `nums` and an integer `target`, return the indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nPrint the two indices separated by a space.',
'First line: integer N (array size)\nSecond line: N space-separated integers\nThird line: integer target',
'Print the two indices separated by a space in ascending order.',
'2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nExactly one valid answer exists.',
'4\n2 7 11 15\n9', '0 1', 50),

('palindrome-checker', 'Palindrome String Checker', 'Easy', 'Strings',
'A phrase is a **palindrome** if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.\n\nGiven a string, determine if it is a palindrome. Output `YES` if it is, or `NO` otherwise.',
'A single line of text.',
'Print `YES` if the string is a palindrome, otherwise print `NO`.',
'1 <= string length <= 10^5\nString contains printable ASCII characters.',
'A man, a plan, a canal: Panama', 'YES', 50),

('fizz-buzz', 'FizzBuzz Generator', 'Easy', 'Math',
'Given an integer `n`, output the string representation of numbers from 1 to `n` separated by spaces:\n- For multiples of 3, print `Fizz`\n- For multiples of 5, print `Buzz`\n- For multiples of both 3 and 5, print `FizzBuzz`\n- Otherwise, print the number itself.',
'A single integer n.',
'Space-separated list of tokens from 1 to n.',
'1 <= n <= 10^4',
'15', '1 2 Fizz 4 Buzz Fizz 7 8 Fizz Buzz 11 Fizz 13 14 FizzBuzz', 50),

('fibonacci-number', 'N-th Fibonacci Number', 'Medium', 'Dynamic Programming',
'The Fibonacci numbers, commonly denoted `F(n)`, form a sequence where each number is the sum of the two preceding ones, starting from 0 and 1:\n`F(0) = 0, F(1) = 1`\n`F(n) = F(n - 1) + F(n - 2)`, for `n > 1`.\n\nGiven an integer `n`, calculate and print `F(n)`.',
'A single integer n.',
'Print the value of F(n).',
'0 <= n <= 45',
'10', '55', 50),

('sql-high-earners', 'High Salary Department Employees', 'Medium', 'SQL & Database',
'A company maintains an `employees` table: `id (INT), name (TEXT), department (TEXT), salary (INT)`.\n\nWrite a SQL query to find the employee name, department, and salary for employees earning strictly more than 80,000, ordered by salary descending.',
'Table `employees` already exists with test rows.',
'Columns: name, department, salary',
'Salaries are positive integers.',
'INSERT INTO employees VALUES (1, ''Alice'', ''Engineering'', 95000), (2, ''Bob'', ''Support'', 50000);', 'Alice|Engineering|95000', 50);

-- Seed Test Cases for Problems
INSERT INTO `test_cases` (`problem_id`, `input_data`, `expected_output`, `is_hidden`, `description`) VALUES
('two-sum', '4\n2 7 11 15\n9', '0 1', FALSE, 'Standard test case'),
('two-sum', '3\n3 2 4\n6', '1 2', FALSE, 'Elements not at beginning'),
('two-sum', '2\n3 3\n6', '0 1', TRUE, 'Hidden: duplicate values'),
('two-sum', '5\n1 5 8 12 14\n20', '2 3', TRUE, 'Hidden: larger array'),

('palindrome-checker', 'A man, a plan, a canal: Panama', 'YES', FALSE, 'Classic palindrome with spaces and colons'),
('palindrome-checker', 'race a car', 'NO', FALSE, 'Non-palindrome'),
('palindrome-checker', 'Was it a car or a cat I saw?', 'YES', TRUE, 'Hidden: sentence with punctuation'),
('palindrome-checker', 'Codely Platform', 'NO', TRUE, 'Hidden: standard false case'),

('fizz-buzz', '5', '1 2 Fizz 4 Buzz', FALSE, 'Small n'),
('fizz-buzz', '15', '1 2 Fizz 4 Buzz Fizz 7 8 Fizz Buzz 11 Fizz 13 14 FizzBuzz', FALSE, 'Includes FizzBuzz at 15'),
('fizz-buzz', '1', '1', TRUE, 'Hidden: boundary case 1'),

('fibonacci-number', '0', '0', FALSE, 'Base case 0'),
('fibonacci-number', '1', '1', FALSE, 'Base case 1'),
('fibonacci-number', '10', '55', FALSE, 'Standard case 10'),
('fibonacci-number', '30', '832040', TRUE, 'Hidden: larger n');

-- Seed Courses & Chapters
INSERT INTO `courses` (`id`, `title`, `category`, `description`, `icon`, `total_chapters`) VALUES
('course-java', 'Modern Java 21+ Mastery', 'Java', 'Master OOP, Generics, Lambdas, Streams, Virtual Threads & Concurrency.', '☕', 3),
('course-python', 'Python for AI & Algorithmic Problem Solving', 'Python', 'From fundamentals to data structures, decorators, generators and OOP.', '🐍', 2),
('course-fullstack', 'Full-Stack JavaScript & Modern Web Engineering', 'JavaScript', 'DOM, Async/Await, Microtasks, Node.js runtime and API development.', '🟨', 2);

INSERT INTO `chapters` (`id`, `course_id`, `order_index`, `title`, `content_markdown`) VALUES
('java-ch-1', 'course-java', 1, 'Java Fundamentals & Virtual Machine', 
'# Modern Java Fundamentals\n\nJava is a strongly-typed, class-based, object-oriented language. Source code compiles into bytecode executed on the JVM (Java Virtual Machine).\n\n### The Memory Model\n- **Stack Memory**: Stores primitive local variables and method call frames.\n- **Heap Memory**: Stores objects and dynamic memory managed by Garbage Collection (ZGC / G1GC).\n\n```java\npublic class Main {\n    public static void main(String[] args) {\n        System.out.println("Hello, Codely!");\n    }\n}\n```'),

('java-ch-2', 'course-java', 2, 'Object-Oriented Programming & Collections',
'# OOP Principles in Java\n\n1. **Encapsulation**: Hiding internal representation with private fields.\n2. **Inheritance**: Subclassing with `extends`.\n3. **Polymorphism**: Dynamic method dispatch via vtables.\n4. **Abstraction**: Interfaces and abstract classes.\n\n### Collections Hierarchy\n- `List<T>`: ArrayList, LinkedList\n- `Set<T>`: HashSet, TreeSet\n- `Map<K, V>`: HashMap, ConcurrentHashMap'),

('java-ch-3', 'course-java', 3, 'Virtual Threads (Project Loom)',
'# Virtual Threads in Java 21+\n\nVirtual threads are lightweight threads that dramatically reduce the effort of writing high-throughput concurrent applications.\n\nUnlike traditional OS platform threads (which consume ~1MB of memory each), virtual threads are managed by the JVM and consume only a few hundred bytes.\n\n```java\nThread.startVirtualThread(() -> {\n    System.out.println("Running in virtual thread: " + Thread.currentThread());\n});\n```');

-- Seed MCQ Questions (Across Languages & Difficulties)
INSERT INTO `mcq_questions` (`id`, `language`, `difficulty`, `question_text`, `code_snippet`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_option_index`, `explanation`) VALUES
('java-1', 'java', 'Easy', 'What will be the output of the following Java code?', 'String s1 = "hello";\nString s2 = new String("hello");\nSystem.out.println((s1 == s2) + " " + s1.equals(s2));', 'false true', 'true true', 'false false', 'true false', 0, '`==` compares object references (s1 is in string pool, s2 is on the heap), whereas `.equals()` compares string content.'),
('java-6', 'java', 'Hard', 'What is the result of autoboxing when comparing two Integer objects with values of 100 vs 200?', 'Integer a = 100, b = 100;\nInteger c = 200, d = 200;\nSystem.out.println((a == b) + " " + (c == d));', 'true false', 'true true', 'false false', 'false true', 0, 'Java caches Integer objects between -128 and 127 via IntegerCache. Values outside this range create distinct object instances on the heap.'),
('java-7', 'java', 'Hard', 'In the Java Memory Model (JMM), what does the volatile keyword guarantee regarding shared variables?', NULL, 'Visibility across threads and establishes a happens-before relationship, but does NOT guarantee compound atomicity (like i++)', 'Both mutual exclusion locking and atomic compound updates like compare-and-swap', 'Prevents garbage collection of the referenced instance', 'Forces the variable to be allocated in thread-local storage', 0, 'volatile prevents instruction reordering and forces reads/writes directly to main memory, establishing a happens-before relationship.'),
('py-1', 'python', 'Medium', 'What is the output of the following default argument snippet in Python?', 'def append_to(num, target=[]):\n    target.append(num)\n    return target\n\nprint(append_to(1))\nprint(append_to(2))', '[1] then [1, 2]', '[1] then [2]', '[1] then [1]', '[1] then []', 0, 'Default arguments in Python are evaluated once when the function is defined, not on every call. The list is mutable and shared.'),
('py-4', 'python', 'Hard', 'What is the output of this closure snippet in Python?', 'funcs = [lambda: x for x in range(3)]\nprint([f() for f in funcs])', '[2, 2, 2]', '[0, 1, 2]', '[2, 1, 0]', 'TypeError', 0, 'Python closures bind variables by reference (late binding). When the lambdas execute, they look up the final value of x in the outer scope, which is 2.'),
('js-3', 'javascript', 'Hard', 'What is the exact execution output order of this event loop code snippet?', 'console.log("1");\nsetTimeout(() => console.log("2"), 0);\nPromise.resolve().then(() => console.log("3"));\nconsole.log("4");', '1, 4, 3, 2', '1, 2, 3, 4', '1, 4, 2, 3', '1, 3, 4, 2', 0, 'Synchronous code runs first (1, 4). The microtask queue (Promise.then -> 3) is drained before the macrotask queue (setTimeout -> 2).'),
('c-5', 'c', 'Hard', 'On a standard 64-bit architecture with natural alignment, what is `sizeof(struct S)`?', 'struct S {\n    char a;\n    int b;\n    char c;\n};', '12 bytes', '6 bytes', '8 bytes', '16 bytes', 0, 'char a (1 byte) + 3 bytes padding + int b (4 bytes) + char c (1 byte) + 3 bytes trailing padding = 12 bytes.'),
('sql-5', 'sql', 'Hard', 'Given duplicate scores (100, 100, 90), what ranks are produced by `RANK()` vs `DENSE_RANK()` in SQL window functions?', NULL, 'RANK() produces (1, 1, 3); DENSE_RANK() produces (1, 1, 2)', 'RANK() produces (1, 1, 2); DENSE_RANK() produces (1, 1, 3)', 'Both produce (1, 2, 3)', 'Both produce (1, 1, 1)', 0, '`RANK()` leaves gaps after tied ranks (1, 1, 3), whereas `DENSE_RANK()` assigns consecutive rank numbers without gaps (1, 1, 2).');

-- Seed User Progress
INSERT INTO `user_progress` (`user_id`, `problem_id`, `is_solved`, `best_execution_time_ms`, `earned_score`) VALUES
('usr-demo-01', 'two-sum', TRUE, 154, 50),
('usr-demo-01', 'palindrome-checker', TRUE, 89, 50);

-- ============================================================================
-- VERIFICATION & INSPECTION QUERIES (FOR YOUR PROFESSOR / DEMONSTRATION)
-- ============================================================================

-- 1. Display all tables in codely_db
SHOW TABLES;

-- 2. Overview of user records
SELECT `id`, `username`, `email`, `full_name`, `streak_days`, `experience_xp`, `role` FROM `users`;

-- 3. Overview of catalog problems
SELECT `id`, `title`, `difficulty`, `category`, `max_score` FROM `problems`;

-- 4. Overview of MCQ questions by language and difficulty
SELECT `language`, `difficulty`, COUNT(*) AS `question_count` FROM `mcq_questions` GROUP BY `language`, `difficulty`;

-- 5. Relational Join: User progress with Problem metadata
SELECT 
    u.full_name AS student_name,
    p.title AS problem_title,
    p.difficulty,
    up.is_solved,
    up.earned_score,
    up.last_attempted_at
FROM `user_progress` up
JOIN `users` u ON up.user_id = u.id
JOIN `problems` p ON up.problem_id = p.id;
