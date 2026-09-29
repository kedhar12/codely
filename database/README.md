# 🗄️ Codely Relational Database Documentation (MySQL)

This directory contains the production-ready MySQL relational database schema, tables, foreign key relationships, and pre-populated seed data for the **Codely** platform.

---

## 📂 File Overview

- **[`codely_schema.sql`](./codely_schema.sql)**: Complete SQL script containing database creation (`codely_db`), all 9 relational tables, constraints, foreign keys, and seed data.
- **[`import_mysql.bat`](./import_mysql.bat)**: Automated Windows batch script to import the database with 1 click.

---

## 🏛️ Database Architecture & Entity Relationships

```text
       +------------------+
       |      users       |
       +------------------+
         |              |
         | 1:N          | 1:N
         v              v
+-------------+   +---------------+
| submissions |   | user_progress |
+-------------+   +---------------+
         ^              ^
         | N:1          | N:1
       +------------------+
       |     problems     |
       +------------------+
         |              |
         | 1:N          | 1:N
         v              v
+-------------+   +-------------------+
| test_cases  |   | starter_templates |
+-------------+   +-------------------+

       +------------------+
       |     courses      |
       +------------------+
         | 1:N
         v
+-------------+
|  chapters   |
+-------------+

+------------------+
|  mcq_questions   |
+------------------+
```

---

## 📋 Table Summary

| Table Name | Description | Key Fields / Relations |
| :--- | :--- | :--- |
| `users` | Registered students and administrators | `id` (PK), `username` (UQ), `email` (UQ), `password_hash`, `salt`, `role` |
| `problems` | Algorithmic coding lab challenges | `id` (PK), `title`, `difficulty`, `category`, `max_score` |
| `test_cases` | Automated grading test suites (public & hidden) | `id` (PK), `problem_id` (FK -> problems.id), `expected_output` |
| `starter_templates` | Multi-language boilerplate templates | `id` (PK), `problem_id` (FK), `language`, `starter_code` |
| `submissions` | Real-time code execution & grading records | `id` (PK), `user_id` (FK), `problem_id` (FK), `verdict`, `score` |
| `courses` | Interactive multi-language tracks | `id` (PK), `title`, `category`, `total_chapters` |
| `chapters` | Markdown curriculum lessons per course | `id` (PK), `course_id` (FK -> courses.id), `content_markdown` |
| `mcq_questions` | Dynamic quizzes across 6 coding languages | `id` (PK), `language`, `difficulty`, `correct_option_index` |
| `user_progress` | Per-user problem completion and XP points | `user_id` (FK), `problem_id` (FK), `is_solved`, `earned_score` |

---

## 🎯 How to Show the Database to Your Professor ("Sir")

### Option 1: MySQL Command Line Client (Terminal)

1. Open your terminal or **MySQL 8.0 Command Line Client**.
2. Log in to MySQL:
   ```bash
   mysql -u root -p
   ```
3. Load and execute the schema:
   ```sql
   SOURCE C:/Users/user/.gemini/antigravity/scratch/codetantra-platform/database/codely_schema.sql;
   ```
4. Verify the database:
   ```sql
   USE codely_db;
   SHOW TABLES;
   ```

### Option 2: Using MySQL Workbench

1. Open **MySQL Workbench** and connect to your local connection (`localhost:3306`).
2. Go to **File -> Open SQL Script...** and select `codely_schema.sql`.
3. Click the **⚡ Execute** button (or press `Ctrl + Shift + Enter`).
4. In the left **Navigator** pane, right-click under **SCHEMAS** and click **Refresh All**.
5. Expand `codely_db` -> **Tables** to visually inspect all 9 tables, columns, and data!

### Option 3: Using phpMyAdmin (XAMPP / WAMP)

1. Open browser to **`http://localhost/phpmyadmin`**.
2. Click on the **Import** tab in the top navigation bar.
3. Click **Choose File** and select `codely_schema.sql`.
4. Click **Go** at the bottom.
5. `codely_db` will appear in the left sidebar with all tables and rows!

---

## 🔍 Key Demonstration Queries to Show Your Professor

Run these commands in MySQL to showcase the structure and data:

```sql
-- 1. Show all relational tables in the database:
USE codely_db;
SHOW TABLES;

-- 2. Inspect the Users table schema and registered users:
DESCRIBE users;
SELECT id, username, email, full_name, streak_days, experience_xp, role FROM users;

-- 3. Show coding challenges in the Practice Lab:
SELECT id, title, difficulty, category, max_score FROM problems;

-- 4. Show distribution of MCQ questions across languages and difficulties:
SELECT language, difficulty, COUNT(*) AS total_questions 
FROM mcq_questions 
GROUP BY language, difficulty 
ORDER BY language, difficulty;

-- 5. Demonstrate Relational JOIN between Users, Progress, and Problems:
SELECT 
    u.full_name AS student_name,
    p.title AS challenge_title,
    p.difficulty,
    up.is_solved,
    up.earned_score,
    up.best_execution_time_ms
FROM user_progress up
JOIN users u ON up.user_id = u.id
JOIN problems p ON up.problem_id = p.id;
```
