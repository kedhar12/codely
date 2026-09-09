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

6. **Student Dashboard & Analytics**
   - Solved problems counter, daily streak tracker, XP points, and total submissions.
   - Language mastery distribution.
   - Submission history log with "View Code" modal.

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
Open your browser at [http://localhost:8080](http://localhost:8080).
