# Codely (Java Backend + Multi-Language Compiler, Assessment & Dynamic MCQ Engine)

A complete interactive coding, learning, and automated grading platform called **Codely**, engineered entirely in **Java 23** and modern web standards.

---

## 🌟 Key Features

1. **🧠 Dynamic API-Connected MCQ Quizzes for All Coding Languages**
   - Live, dynamic multiple-choice question engine covering:
     - ☕ **Java** (Memory model, String pool, Virtual Threads, Collections, OOP)
     - 🐍 **Python** (GIL, mutability, generators, closures, comprehensions)
     - 🟨 **JavaScript** (Event loop, microtasks vs macrotasks, strict equality, prototypes)
     - ⚙️ **C** (Pointer arithmetic, memory leaks, malloc/free, static linkage)
     - ⚡ **C++** (RAII, smart pointers, std::move, virtual destructors)
     - 🗄️ **SQL** (JOIN types, WHERE vs HAVING, ACID transactions, NULL handling)
   - Connected to **Open Trivia DB Computer Science API** + dynamic language question banks.
   - **Randomized & Shuffled Every Time**: Questions and answer options shuffle dynamically on every attempt, providing an endless variety of test rounds.
   - Real-time feedback, correct/incorrect highlighting, and in-depth conceptual explanations.

2. **Multi-Language Online Compiler & Playground**
   - Execute code in **Java 23 (OpenJDK)**, **Python 3.14**, **JavaScript (Node.js 24)**, **C (GCC 16.2)**, **C++ (G++ 16.2)**, and **SQL (Relational Engine)**.
   - Monaco Editor with autocompletion, syntax highlighting, bracket matching, line numbers, and theme switching.
   - Interactive stdin support for dynamic programs.
   - Execution performance metrics (execution time in milliseconds, exit status, stderr diagnostics).
   - Code download and template reset buttons.

3. **Practice Lab (Problem-Solving Workbench)**
   - Codely split-pane workbench:
     - Left: Problem description, input/output specifications, constraints, sample cases with copy buttons.
     - Right: Multi-language Monaco code editor, starter templates for every language, reset button.
     - Bottom: Test case evaluation drawer with pass/fail indicators, execution time, and side-by-side diffs.
   - Automated grading against both **Public** and **Hidden** test suites.

4. **Interactive Courses & Curriculum**
   - Structured tracks:
     - ☕ *Java Programming Masterclass*
     - 🐍 *Python for Algorithms & Problem Solving*
     - ⚙️ *C Systems & Pointer Engineering*
     - 🗄️ *Database Systems & SQL Mastery*
   - Step-by-step interactive lessons with "Try it in Lab" one-click runner.

5. **Timed Lab Assessments & Exams**
   - University lab exam simulator with live countdown timer (`MM:SS`).
   - Question palette with status indicators (Attempted, Solved).
   - Instant scoring report with grade breakdown and certificates.

7. **🔐 User Authentication & Session Security**
   - Secure student registration (**Sign Up**) and login (**Sign In**) with salted SHA-256 encryption.
   - Session authentication with secure bearer tokens and persistent profile state.
   - Pre-configured demo student profile (`demo@codely.dev` / `codely123`).

8. **🗄️ MySQL Relational Database (`database/codely_schema.sql`)**
   - Normalized relational database schema (`codely_db`) designed for **MySQL 8.0+ / MariaDB / XAMPP**.
   - **9 Relational Tables**:
     - `users` — Students and administrators with credentials, streaks, and XP points.
     - `problems` — Algorithmic challenge catalog with difficulty, constraints, and scoring.
     - `test_cases` — Public and hidden test cases for automated test runners.
     - `starter_templates` — Multi-language boilerplate starter code.
     - `submissions` — Historical code execution logs, verdicts, and runtimes.
     - `courses` & `chapters` — Interactive curriculum tracks and lesson content.
     - `mcq_questions` — Quiz question banks across all languages and difficulty modes.
     - `user_progress` — Relational tracking of solved problems and earned scores.
   - Includes **[`database/codely_schema.sql`](./database/codely_schema.sql)** and **[`database/import_mysql.bat`](./database/import_mysql.bat)** for 1-click import.

---

## 🗄️ Showing the MySQL Database to Your Instructor

### 1. Import the Database into MySQL
In MySQL CLI or terminal, run:
```sql
SOURCE database/codely_schema.sql;
```
*(Or double-click `database/import_mysql.bat` on Windows).*

### 2. Key Demonstration Queries
```sql
-- Select database
USE codely_db;

-- Show all 9 tables
SHOW TABLES;

-- Inspect registered users
SELECT id, username, email, full_name, streak_days, experience_xp, role FROM users;

-- View coding challenge catalog
SELECT id, title, category, difficulty, max_score FROM problems;

-- Inspect MCQ question breakdown by language and difficulty
SELECT language, difficulty, COUNT(*) AS count FROM mcq_questions GROUP BY language, difficulty;

-- Relational JOIN: Students, Solved Problems & Earned XP
SELECT 
    u.full_name AS student_name,
    p.title AS problem_title,
    p.difficulty,
    up.is_solved,
    up.earned_score,
    up.best_execution_time_ms
FROM user_progress up
JOIN users u ON up.user_id = u.id
JOIN problems p ON up.problem_id = p.id;
```

---

## 🚀 How to Run

### Windows (One-Click)
Double-click `start.bat` or run:
```powershell
.\start.ps1
```

### Manual Run
```bash
# 1. Compile Java backend
javac -encoding UTF-8 -d bin src/server/*.java

# 2. Run the server
java -cp bin server.CodeTantraServer 8080
```
Open your browser at **[http://localhost:8080](http://localhost:8080)**.
