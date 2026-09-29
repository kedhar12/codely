package server;

import java.util.*;

public class ProblemService {
    private static final Map<String, Problem> problems = new LinkedHashMap<>();

    static {
        // Problem 1: Two Sum
        Map<String, String> twoSumTemplates = new HashMap<>();
        twoSumTemplates.put("java", 
            "import java.util.Scanner;\n" +
            "import java.util.HashMap;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        if (!sc.hasNextInt()) return;\n" +
            "        int n = sc.nextInt();\n" +
            "        int[] nums = new int[n];\n" +
            "        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();\n" +
            "        int target = sc.nextInt();\n\n" +
            "        // Write your solution here\n" +
            "        HashMap<Integer, Integer> map = new HashMap<>();\n" +
            "        for (int i = 0; i < n; i++) {\n" +
            "            int comp = target - nums[i];\n" +
            "            if (map.containsKey(comp)) {\n" +
            "                System.out.println(map.get(comp) + \" \" + i);\n" +
            "                return;\n" +
            "            }\n" +
            "            map.put(nums[i], i);\n" +
            "        }\n" +
            "    }\n" +
            "}\n");

        twoSumTemplates.put("python",
            "import sys\n\n" +
            "def solve():\n" +
            "    input_data = sys.stdin.read().split()\n" +
            "    if not input_data: return\n" +
            "    n = int(input_data[0])\n" +
            "    nums = [int(x) for x in input_data[1:1+n]]\n" +
            "    target = int(input_data[1+n])\n\n" +
            "    seen = {}\n" +
            "    for i, x in enumerate(nums):\n" +
            "        comp = target - x\n" +
            "        if comp in seen:\n" +
            "            print(f\"{seen[comp]} {i}\")\n" +
            "            return\n" +
            "        seen[x] = i\n\n" +
            "solve()\n");

        twoSumTemplates.put("javascript",
            "const fs = require('fs');\n" +
            "const input = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);\n" +
            "if (input.length >= 3) {\n" +
            "    const n = parseInt(input[0]);\n" +
            "    const nums = input.slice(1, 1 + n).map(Number);\n" +
            "    const target = parseInt(input[1 + n]);\n" +
            "    const map = new Map();\n" +
            "    for (let i = 0; i < n; i++) {\n" +
            "        const comp = target - nums[i];\n" +
            "        if (map.has(comp)) {\n" +
            "            console.log(map.get(comp) + ' ' + i);\n" +
            "            process.exit(0);\n" +
            "        }\n" +
            "        map.set(nums[i], i);\n" +
            "    }\n" +
            "}\n");

        twoSumTemplates.put("c",
            "#include <stdio.h>\n" +
            "#include <stdlib.h>\n\n" +
            "int main() {\n" +
            "    int n;\n" +
            "    if (scanf(\"%d\", &n) != 1) return 0;\n" +
            "    int* nums = (int*)malloc(n * sizeof(int));\n" +
            "    for (int i = 0; i < n; i++) scanf(\"%d\", &nums[i]);\n" +
            "    int target;\n" +
            "    scanf(\"%d\", &target);\n\n" +
            "    for (int i = 0; i < n; i++) {\n" +
            "        for (int j = i + 1; j < n; j++) {\n" +
            "            if (nums[i] + nums[j] == target) {\n" +
            "                printf(\"%d %d\\n\", i, j);\n" +
            "                free(nums);\n" +
            "                return 0;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    free(nums);\n" +
            "    return 0;\n" +
            "}\n");

        twoSumTemplates.put("cpp",
            "#include <iostream>\n" +
            "#include <vector>\n" +
            "#include <unordered_map>\n" +
            "using namespace std;\n\n" +
            "int main() {\n" +
            "    int n;\n" +
            "    if (!(cin >> n)) return 0;\n" +
            "    vector<int> nums(n);\n" +
            "    for (int i = 0; i < n; i++) cin >> nums[i];\n" +
            "    int target;\n" +
            "    cin >> target;\n\n" +
            "    unordered_map<int, int> seen;\n" +
            "    for (int i = 0; i < n; i++) {\n" +
            "        int comp = target - nums[i];\n" +
            "        if (seen.count(comp)) {\n" +
            "            cout << seen[comp] << \" \" << i << endl;\n" +
            "            return 0;\n" +
            "        }\n" +
            "        seen[nums[i]] = i;\n" +
            "    }\n" +
            "    return 0;\n" +
            "}\n");

        twoSumTemplates.put("sql",
            "-- In SQL, we can find pairs matching target sum from a values table\n" +
            "CREATE TABLE numbers (idx INT, val INT);\n" +
            "INSERT INTO numbers VALUES (0, 2), (1, 7), (2, 11), (3, 15);\n\n" +
            "SELECT a.idx AS index1, b.idx AS index2\n" +
            "FROM numbers a\n" +
            "JOIN numbers b ON a.idx < b.idx\n" +
            "WHERE a.val + b.val = 9;\n");

        List<TestCase> twoSumTestCases = List.of(
            new TestCase("4\n2 7 11 15\n9", "0 1", false, "2 + 7 = 9 at indices 0 and 1"),
            new TestCase("3\n3 2 4\n6", "1 2", false, "2 + 4 = 6 at indices 1 and 2"),
            new TestCase("2\n3 3\n6", "0 1", true, "3 + 3 = 6"),
            new TestCase("5\n1 5 8 10 12\n18", "2 3", true, "8 + 10 = 18 at indices 2 and 3")
        );

        problems.put("two-sum", new Problem(
            "two-sum",
            "Two Sum",
            "Easy",
            "Arrays & Hashing",
            "Given an array of integers `nums` and an integer `target`, print the **indices** of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice. Print the indices separated by a space in ascending order.",
            "Line 1: An integer `N` representing the array length.\nLine 2: `N` space-separated integers representing the array.\nLine 3: An integer representing `target`.",
            "Print two space-separated integers `i` and `j` (0-indexed) where `i < j`.",
            "2 <= N <= 10^5\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9",
            "4\n2 7 11 15\n9",
            "0 1",
            twoSumTemplates,
            twoSumTestCases
        ));

        // Problem 2: Palindrome String Checker
        Map<String, String> palTemplates = new HashMap<>();
        palTemplates.put("java",
            "import java.util.Scanner;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        if (!sc.hasNextLine()) return;\n" +
            "        String s = sc.nextLine();\n" +
            "        // Clean string: keep only alphanumeric and lowercase\n" +
            "        StringBuilder sb = new StringBuilder();\n" +
            "        for (char c : s.toCharArray()) {\n" +
            "            if (Character.isLetterOrDigit(c)) {\n" +
            "                sb.append(Character.toLowerCase(c));\n" +
            "            }\n" +
            "        }\n" +
            "        String clean = sb.toString();\n" +
            "        String rev = sb.reverse().toString();\n" +
            "        System.out.println(clean.equals(rev) ? \"YES\" : \"NO\");\n" +
            "    }\n" +
            "}\n");

        palTemplates.put("python",
            "import sys\n" +
            "s = sys.stdin.read().strip()\n" +
            "clean = ''.join(c.lower() for c in s if c.isalnum())\n" +
            "print('YES' if clean == clean[::-1] else 'NO')\n");

        palTemplates.put("javascript",
            "const fs = require('fs');\n" +
            "const s = fs.readFileSync(0, 'utf-8');\n" +
            "const clean = s.toLowerCase().replace(/[^a-z0-9]/g, '');\n" +
            "const rev = clean.split('').reverse().join('');\n" +
            "console.log(clean === rev ? 'YES' : 'NO');\n");

        palTemplates.put("c",
            "#include <stdio.h>\n" +
            "#include <ctype.h>\n" +
            "#include <string.h>\n\n" +
            "int main() {\n" +
            "    char s[2000];\n" +
            "    if (!fgets(s, sizeof(s), stdin)) return 0;\n" +
            "    char clean[2000];\n" +
            "    int len = 0;\n" +
            "    for (int i = 0; s[i] != '\\0'; i++) {\n" +
            "        if (isalnum(s[i])) {\n" +
            "            clean[len++] = tolower(s[i]);\n" +
            "        }\n" +
            "    }\n" +
            "    clean[len] = '\\0';\n" +
            "    int is_pal = 1;\n" +
            "    for (int i = 0; i < len / 2; i++) {\n" +
            "        if (clean[i] != clean[len - 1 - i]) {\n" +
            "            is_pal = 0;\n" +
            "            break;\n" +
            "        }\n" +
            "    }\n" +
            "    printf(\"%s\\n\", is_pal ? \"YES\" : \"NO\");\n" +
            "    return 0;\n" +
            "}\n");

        palTemplates.put("cpp",
            "#include <iostream>\n" +
            "#include <string>\n" +
            "#include <cctype>\n" +
            "#include <algorithm>\n" +
            "using namespace std;\n\n" +
            "int main() {\n" +
            "    string s;\n" +
            "    getline(cin, s);\n" +
            "    string clean = \"\";\n" +
            "    for (char c : s) {\n" +
            "        if (isalnum(c)) clean += tolower(c);\n" +
            "    }\n" +
            "    string rev = clean;\n" +
            "    reverse(rev.begin(), rev.end());\n" +
            "    cout << (clean == rev ? \"YES\" : \"NO\") << endl;\n" +
            "    return 0;\n" +
            "}\n");

        palTemplates.put("sql",
            "CREATE TABLE text_samples (val TEXT);\n" +
            "INSERT INTO text_samples VALUES ('madam'), ('hello');\n" +
            "SELECT val, CASE WHEN val = '' THEN 'YES' ELSE 'EVALUATED' END AS is_palindrome FROM text_samples;\n");

        List<TestCase> palTestCases = List.of(
            new TestCase("A man, a plan, a canal: Panama", "YES", false, "Standard palindrome ignoring punctuation"),
            new TestCase("race a car", "NO", false, "Not a palindrome"),
            new TestCase("Was it a car or a cat I saw?", "YES", true, "Hidden case with spaces and question mark"),
            new TestCase("CodeTantra Platform", "NO", true, "Not a palindrome")
        );

        problems.put("palindrome-checker", new Problem(
            "palindrome-checker",
            "Palindrome String Checker",
            "Easy",
            "Strings",
            "A phrase is a **palindrome** if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.\n\nGiven a string, determine if it is a palindrome. Output `YES` if it is, or `NO` otherwise.",
            "A single line of text.",
            "Print `YES` if the string is a palindrome, otherwise print `NO`.",
            "1 <= string length <= 10^5\nString contains printable ASCII characters.",
            "A man, a plan, a canal: Panama",
            "YES",
            palTemplates,
            palTestCases
        ));

        // Problem 3: Fibonacci Sequence
        Map<String, String> fibTemplates = new HashMap<>();
        fibTemplates.put("java",
            "import java.util.Scanner;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        if (!sc.hasNextInt()) return;\n" +
            "        int n = sc.nextInt();\n" +
            "        if (n == 0) { System.out.println(0); return; }\n" +
            "        if (n == 1) { System.out.println(1); return; }\n" +
            "        long a = 0, b = 1;\n" +
            "        for (int i = 2; i <= n; i++) {\n" +
            "            long c = a + b;\n" +
            "            a = b;\n" +
            "            b = c;\n" +
            "        }\n" +
            "        System.out.println(b);\n" +
            "    }\n" +
            "}\n");

        fibTemplates.put("python",
            "import sys\n" +
            "data = sys.stdin.read().strip()\n" +
            "if data:\n" +
            "    n = int(data)\n" +
            "    if n == 0: print(0)\n" +
            "    elif n == 1: print(1)\n" +
            "    else:\n" +
            "        a, b = 0, 1\n" +
            "        for _ in range(2, n + 1):\n" +
            "            a, b = b, a + b\n" +
            "        print(b)\n");

        fibTemplates.put("javascript",
            "const fs = require('fs');\n" +
            "const n = parseInt(fs.readFileSync(0, 'utf-8').trim());\n" +
            "if (!isNaN(n)) {\n" +
            "    if (n === 0) { console.log(0); process.exit(0); }\n" +
            "    if (n === 1) { console.log(1); process.exit(0); }\n" +
            "    let a = 0n, b = 1n;\n" +
            "    for (let i = 2; i <= n; i++) {\n" +
            "        const c = a + b;\n" +
            "        a = b;\n" +
            "        b = c;\n" +
            "    }\n" +
            "    console.log(b.toString());\n" +
            "}\n");

        fibTemplates.put("c",
            "#include <stdio.h>\n\n" +
            "int main() {\n" +
            "    int n;\n" +
            "    if (scanf(\"%d\", &n) != 1) return 0;\n" +
            "    if (n == 0) { printf(\"0\\n\"); return 0; }\n" +
            "    if (n == 1) { printf(\"1\\n\"); return 0; }\n" +
            "    long long a = 0, b = 1;\n" +
            "    for (int i = 2; i <= n; i++) {\n" +
            "        long long c = a + b;\n" +
            "        a = b;\n" +
            "        b = c;\n" +
            "    }\n" +
            "    printf(\"%lld\\n\", b);\n" +
            "    return 0;\n" +
            "}\n");

        fibTemplates.put("cpp",
            "#include <iostream>\n" +
            "using namespace std;\n\n" +
            "int main() {\n" +
            "    int n;\n" +
            "    if (!(cin >> n)) return 0;\n" +
            "    if (n == 0) { cout << 0 << endl; return 0; }\n" +
            "    if (n == 1) { cout << 1 << endl; return 0; }\n" +
            "    long long a = 0, b = 1;\n" +
            "    for (int i = 2; i <= n; i++) {\n" +
            "        long long c = a + b;\n" +
            "        a = b;\n" +
            "        b = c;\n" +
            "    }\n" +
            "    cout << b << endl;\n" +
            "    return 0;\n" +
            "}\n");

        fibTemplates.put("sql",
            "WITH RECURSIVE fib(n, a, b) AS (\n" +
            "    VALUES(0, 0, 1)\n" +
            "    UNION ALL\n" +
            "    SELECT n + 1, b, a + b FROM fib WHERE n < 10\n" +
            ")\n" +
            "SELECT n, a AS fib_number FROM fib;\n");

        List<TestCase> fibTestCases = List.of(
            new TestCase("2", "1", false, "F(2) = 1"),
            new TestCase("10", "55", false, "F(10) = 55"),
            new TestCase("0", "0", true, "Base case F(0) = 0"),
            new TestCase("20", "6765", true, "F(20) = 6765")
        );

        problems.put("fibonacci-dp", new Problem(
            "fibonacci-dp",
            "Fibonacci Sequence",
            "Easy",
            "Dynamic Programming",
            "The **Fibonacci numbers**, commonly denoted `F(n)` form a sequence such that each number is the sum of the two preceding ones, starting from `0` and `1`:\n\n- `F(0) = 0`\n- `F(1) = 1`\n- `F(n) = F(n - 1) + F(n - 2)`, for `n > 1`.\n\nGiven `n`, calculate `F(n)` efficiently in O(n) time.",
            "A single integer `n`.",
            "Print the value of `F(n)`.",
            "0 <= n <= 45",
            "10",
            "55",
            fibTemplates,
            fibTestCases
        ));

        // Problem 4: Valid Parentheses
        Map<String, String> parenTemplates = new HashMap<>();
        parenTemplates.put("java",
            "import java.util.Scanner;\n" +
            "import java.util.Stack;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        if (!sc.hasNext()) return;\n" +
            "        String s = sc.next();\n" +
            "        Stack<Character> stack = new Stack<>();\n" +
            "        for (char c : s.toCharArray()) {\n" +
            "            if (c == '(') stack.push(')');\n" +
            "            else if (c == '{') stack.push('}');\n" +
            "            else if (c == '[') stack.push(']');\n" +
            "            else if (stack.isEmpty() || stack.pop() != c) {\n" +
            "                System.out.println(\"INVALID\");\n" +
            "                return;\n" +
            "            }\n" +
            "        }\n" +
            "        System.out.println(stack.isEmpty() ? \"VALID\" : \"INVALID\");\n" +
            "    }\n" +
            "}\n");

        parenTemplates.put("python",
            "import sys\n" +
            "s = sys.stdin.read().strip()\n" +
            "mapping = {')': '(', '}': '{', ']': '['}\n" +
            "stack = []\n" +
            "valid = True\n" +
            "for c in s:\n" +
            "    if c in '({[':\n" +
            "        stack.append(c)\n" +
            "    elif c in mapping:\n" +
            "        if not stack or stack.pop() != mapping[c]:\n" +
            "            valid = False\n" +
            "            break\n" +
            "if valid and not stack:\n" +
            "    print('VALID')\n" +
            "else:\n" +
            "    print('INVALID')\n");

        parenTemplates.put("javascript",
            "const fs = require('fs');\n" +
            "const s = fs.readFileSync(0, 'utf-8').trim();\n" +
            "const map = { ')': '(', '}': '{', ']': '[' };\n" +
            "const stack = [];\n" +
            "let valid = true;\n" +
            "for (const c of s) {\n" +
            "    if (c === '(' || c === '{' || c === '[') {\n" +
            "        stack.push(c);\n" +
            "    } else if (map[c]) {\n" +
            "        if (!stack.length || stack.pop() !== map[c]) {\n" +
            "            valid = false;\n" +
            "            break;\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "console.log(valid && stack.length === 0 ? 'VALID' : 'INVALID');\n");

        parenTemplates.put("c",
            "#include <stdio.h>\n" +
            "#include <string.h>\n\n" +
            "int main() {\n" +
            "    char s[10000];\n" +
            "    if (scanf(\"%s\", s) != 1) return 0;\n" +
            "    char stack[10000];\n" +
            "    int top = -1;\n" +
            "    for (int i = 0; s[i] != '\\0'; i++) {\n" +
            "        char c = s[i];\n" +
            "        if (c == '(') stack[++top] = ')';\n" +
            "        else if (c == '{') stack[++top] = '}';\n" +
            "        else if (c == '[') stack[++top] = ']';\n" +
            "        else if (top == -1 || stack[top--] != c) {\n" +
            "            printf(\"INVALID\\n\");\n" +
            "            return 0;\n" +
            "        }\n" +
            "    }\n" +
            "    printf(\"%s\\n\", top == -1 ? \"VALID\" : \"INVALID\");\n" +
            "    return 0;\n" +
            "}\n");

        parenTemplates.put("cpp",
            "#include <iostream>\n" +
            "#include <stack>\n" +
            "#include <string>\n" +
            "using namespace std;\n\n" +
            "int main() {\n" +
            "    string s;\n" +
            "    if (!(cin >> s)) return 0;\n" +
            "    stack<char> st;\n" +
            "    for (char c : s) {\n" +
            "        if (c == '(') st.push(')');\n" +
            "        else if (c == '{') st.push('}');\n" +
            "        else if (c == '[') st.push(']');\n" +
            "        else if (st.empty() || st.top() != c) {\n" +
            "            cout << \"INVALID\" << endl;\n" +
            "            return 0;\n" +
            "        } else {\n" +
            "            st.pop();\n" +
            "        }\n" +
            "    }\n" +
            "    cout << (st.empty() ? \"VALID\" : \"INVALID\") << endl;\n" +
            "    return 0;\n" +
            "}\n");

        parenTemplates.put("sql",
            "-- Bracket matching simulation table\n" +
            "CREATE TABLE brackets (expr TEXT, is_valid TEXT);\n" +
            "INSERT INTO brackets VALUES ('()[]{}', 'VALID'), ('(]', 'INVALID');\n" +
            "SELECT * FROM brackets;\n");

        List<TestCase> parenTestCases = List.of(
            new TestCase("()[]{}", "VALID", false, "Direct matching pairs"),
            new TestCase("(]", "INVALID", false, "Mismatched bracket"),
            new TestCase("([{}])", "VALID", true, "Nested matching brackets"),
            new TestCase("[(])", "INVALID", true, "Cross-interleaved brackets")
        );

        problems.put("balanced-parentheses", new Problem(
            "balanced-parentheses",
            "Valid Parentheses",
            "Medium",
            "Stacks",
            "Given a string `s` containing just the characters `'('`, `')'`, `'{'`, `'}'`, `'['` and `']'`, determine if the input string is valid.\n\nAn input string is valid if:\n1. Open brackets must be closed by the same type of brackets.\n2. Open brackets must be closed in the correct order.\n3. Every close bracket has a corresponding open bracket of the same type.\n\nPrint `VALID` or `INVALID`.",
            "A single string consisting of parentheses characters.",
            "Print `VALID` if brackets are properly balanced, else print `INVALID`.",
            "1 <= s.length <= 10^4",
            "()[]{}",
            "VALID",
            parenTemplates,
            parenTestCases
        ));

        // Problem 5: SQL Department Highest Salary
        Map<String, String> sqlTemplates = new HashMap<>();
        sqlTemplates.put("sql",
            "-- Employee & Department schema setup\n" +
            "CREATE TABLE Department (\n" +
            "    id INT PRIMARY KEY,\n" +
            "    name TEXT\n" +
            ");\n\n" +
            "CREATE TABLE Employee (\n" +
            "    id INT PRIMARY KEY,\n" +
            "    name TEXT,\n" +
            "    salary INT,\n" +
            "    departmentId INT,\n" +
            "    FOREIGN KEY (departmentId) REFERENCES Department(id)\n" +
            ");\n\n" +
            "INSERT INTO Department VALUES (1, 'IT'), (2, 'Sales');\n" +
            "INSERT INTO Employee VALUES \n" +
            "    (1, 'Joe', 85000, 1),\n" +
            "    (2, 'Henry', 80000, 2),\n" +
            "    (3, 'Sam', 60000, 2),\n" +
            "    (4, 'Max', 90000, 1);\n\n" +
            "-- Write a SQL query to find employees with the highest salary in each department:\n" +
            "SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary\n" +
            "FROM Employee e\n" +
            "JOIN Department d ON e.departmentId = d.id\n" +
            "WHERE (e.departmentId, e.salary) IN (\n" +
            "    SELECT departmentId, MAX(salary) FROM Employee GROUP BY departmentId\n" +
            ")\n" +
            "ORDER BY d.name;\n");

        sqlTemplates.put("java",
            "// SQL problem: switch to SQL tab or run queries via JDBC simulator\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Department | Employee | Salary\");\n" +
            "        System.out.println(\"IT         | Max      | 90000\");\n" +
            "        System.out.println(\"Sales      | Henry    | 80000\");\n" +
            "    }\n" +
            "}\n");

        List<TestCase> sqlTestCases = List.of(
            new TestCase("", "+------------+----------+--------+\n| Department | Employee | Salary |\n+------------+----------+--------+\n| IT         | Max      | 90000  |\n| Sales      | Henry    | 80000  |\n+------------+----------+--------+\n(2 row(s) returned)", false, "Employees with max salary in IT and Sales")
        );

        problems.put("sql-highest-salary", new Problem(
            "sql-highest-salary",
            "Department Highest Salary",
            "Medium",
            "SQL & Databases",
            "Write a solution to find employees who have the highest salary in each of the departments.\n\nOutput columns: `Department`, `Employee`, `Salary` ordered by department name.",
            "Standard relational schema with tables `Employee` and `Department`.",
            "Tabular result showing each department, the highest paid employee, and their salary.",
            "Each employee belongs to one department.",
            "",
            "+------------+----------+--------+\n| Department | Employee | Salary |\n+------------+----------+--------+\n| IT         | Max      | 90000  |\n| Sales      | Henry    | 80000  |\n+------------+----------+--------+\n(2 row(s) returned)",
            sqlTemplates,
            sqlTestCases
        ));

        // Problem 6: Trapping Rain Water (Hard)
        Map<String, String> trapTemplates = new HashMap<>();
        trapTemplates.put("java",
            "import java.util.Scanner;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        if (!sc.hasNextInt()) return;\n" +
            "        int n = sc.nextInt();\n" +
            "        int[] height = new int[n];\n" +
            "        for (int i = 0; i < n; i++) height[i] = sc.nextInt();\n\n" +
            "        int left = 0, right = n - 1;\n" +
            "        int leftMax = 0, rightMax = 0;\n" +
            "        long water = 0;\n" +
            "        while (left < right) {\n" +
            "            if (height[left] < height[right]) {\n" +
            "                if (height[left] >= leftMax) leftMax = height[left];\n" +
            "                else water += leftMax - height[left];\n" +
            "                left++;\n" +
            "            } else {\n" +
            "                if (height[right] >= rightMax) rightMax = height[right];\n" +
            "                else water += rightMax - height[right];\n" +
            "                right--;\n" +
            "            }\n" +
            "        }\n" +
            "        System.out.println(water);\n" +
            "    }\n" +
            "}\n");

        trapTemplates.put("python",
            "import sys\n\n" +
            "def solve():\n" +
            "    tokens = sys.stdin.read().split()\n" +
            "    if not tokens: return\n" +
            "    n = int(tokens[0])\n" +
            "    height = [int(x) for x in tokens[1:1+n]]\n" +
            "    left, right = 0, n - 1\n" +
            "    left_max, right_max = 0, 0\n" +
            "    water = 0\n" +
            "    while left < right:\n" +
            "        if height[left] < height[right]:\n" +
            "            if height[left] >= left_max: left_max = height[left]\n" +
            "            else: water += left_max - height[left]\n" +
            "            left += 1\n" +
            "        else:\n" +
            "            if height[right] >= right_max: right_max = height[right]\n" +
            "            else: water += right_max - height[right]\n" +
            "            right -= 1\n" +
            "    print(water)\n\n" +
            "solve()\n");

        trapTemplates.put("javascript",
            "const fs = require('fs');\n" +
            "const tokens = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);\n" +
            "if (tokens.length >= 1 && tokens[0] !== '') {\n" +
            "    const n = parseInt(tokens[0]);\n" +
            "    const height = tokens.slice(1, 1 + n).map(Number);\n" +
            "    let left = 0, right = n - 1;\n" +
            "    let leftMax = 0, rightMax = 0, water = 0;\n" +
            "    while (left < right) {\n" +
            "        if (height[left] < height[right]) {\n" +
            "            if (height[left] >= leftMax) leftMax = height[left];\n" +
            "            else water += leftMax - height[left];\n" +
            "            left++;\n" +
            "        } else {\n" +
            "            if (height[right] >= rightMax) rightMax = height[right];\n" +
            "            else water += rightMax - height[right];\n" +
            "            right--;\n" +
            "        }\n" +
            "    }\n" +
            "    console.log(water);\n" +
            "}\n");

        trapTemplates.put("c",
            "#include <stdio.h>\n" +
            "#include <stdlib.h>\n\n" +
            "int main() {\n" +
            "    int n;\n" +
            "    if (scanf(\"%d\", &n) != 1) return 0;\n" +
            "    int* height = (int*)malloc(n * sizeof(int));\n" +
            "    for (int i = 0; i < n; i++) scanf(\"%d\", &height[i]);\n" +
            "    int left = 0, right = n - 1;\n" +
            "    int leftMax = 0, rightMax = 0;\n" +
            "    long long water = 0;\n" +
            "    while (left < right) {\n" +
            "        if (height[left] < height[right]) {\n" +
            "            if (height[left] >= leftMax) leftMax = height[left];\n" +
            "            else water += leftMax - height[left];\n" +
            "            left++;\n" +
            "        } else {\n" +
            "            if (height[right] >= rightMax) rightMax = height[right];\n" +
            "            else water += rightMax - height[right];\n" +
            "            right--;\n" +
            "        }\n" +
            "    }\n" +
            "    printf(\"%lld\\n\", water);\n" +
            "    free(height);\n" +
            "    return 0;\n" +
            "}\n");

        trapTemplates.put("cpp",
            "#include <iostream>\n" +
            "#include <vector>\n" +
            "using namespace std;\n\n" +
            "int main() {\n" +
            "    int n;\n" +
            "    if (!(cin >> n)) return 0;\n" +
            "    vector<int> height(n);\n" +
            "    for (int i = 0; i < n; i++) cin >> height[i];\n" +
            "    int left = 0, right = n - 1;\n" +
            "    int leftMax = 0, rightMax = 0;\n" +
            "    long long water = 0;\n" +
            "    while (left < right) {\n" +
            "        if (height[left] < height[right]) {\n" +
            "            if (height[left] >= leftMax) leftMax = height[left];\n" +
            "            else water += leftMax - height[left];\n" +
            "            left++;\n" +
            "        } else {\n" +
            "            if (height[right] >= rightMax) rightMax = height[right];\n" +
            "            else water += rightMax - height[right];\n" +
            "            right--;\n" +
            "        }\n" +
            "    }\n" +
            "    cout << water << endl;\n" +
            "    return 0;\n" +
            "}\n");

        trapTemplates.put("sql",
            "CREATE TABLE elevation (bar_index INT, height INT);\n" +
            "INSERT INTO elevation VALUES (0,0),(1,1),(2,0),(3,2),(4,1),(5,0),(6,1),(7,3),(8,2),(9,1),(10,2),(11,1);\n" +
            "SELECT 6 AS trapped_water_units;\n");

        List<TestCase> trapTestCases = List.of(
            new TestCase("12\n0 1 0 2 1 0 1 3 2 1 2 1", "6", false, "Standard elevation map with peaks and valleys"),
            new TestCase("6\n4 2 0 3 2 5", "9", false, "Deep basin trapping 9 units between 4 and 5"),
            new TestCase("5\n3 2 1 0 0", "0", true, "Decreasing heights trap 0 units of water"),
            new TestCase("7\n0 2 0 2 0 2 0", "4", true, "Two separate basins trapping 2 units each")
        );

        problems.put("trapping-rain-water", new Problem(
            "trapping-rain-water",
            "Trapping Rain Water",
            "Hard",
            "Arrays & Dynamic Programming",
            "Given `n` non-negative integers representing an elevation map where the width of each bar is `1`, compute how much water it can trap after raining.\n\nOptimal solution runs in O(n) time and O(1) auxiliary space using two pointers.",
            "Line 1: An integer `n` representing the number of bars.\nLine 2: `n` space-separated integers representing bar heights.",
            "Print a single integer representing total units of trapped rain water.",
            "1 <= n <= 10^5\n0 <= height[i] <= 10^5",
            "12\n0 1 0 2 1 0 1 3 2 1 2 1",
            "6",
            trapTemplates,
            trapTestCases
        ));

        // Problem 7: Sliding Window Maximum (Hard)
        Map<String, String> slideTemplates = new HashMap<>();
        slideTemplates.put("java",
            "import java.util.*;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        if (!sc.hasNextInt()) return;\n" +
            "        int n = sc.nextInt();\n" +
            "        int k = sc.nextInt();\n" +
            "        int[] nums = new int[n];\n" +
            "        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();\n\n" +
            "        Deque<Integer> dq = new ArrayDeque<>();\n" +
            "        StringBuilder sb = new StringBuilder();\n" +
            "        for (int i = 0; i < n; i++) {\n" +
            "            while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) dq.pollFirst();\n" +
            "            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();\n" +
            "            dq.offerLast(i);\n" +
            "            if (i >= k - 1) {\n" +
            "                if (sb.length() > 0) sb.append(\" \");\n" +
            "                sb.append(nums[dq.peekFirst()]);\n" +
            "            }\n" +
            "        }\n" +
            "        System.out.println(sb.toString());\n" +
            "    }\n" +
            "}\n");

        slideTemplates.put("python",
            "import sys\n" +
            "from collections import deque\n\n" +
            "def solve():\n" +
            "    tokens = sys.stdin.read().split()\n" +
            "    if not tokens: return\n" +
            "    n = int(tokens[0])\n" +
            "    k = int(tokens[1])\n" +
            "    nums = [int(x) for x in tokens[2:2+n]]\n" +
            "    dq = deque()\n" +
            "    res = []\n" +
            "    for i, x in enumerate(nums):\n" +
            "        while dq and dq[0] < i - k + 1: dq.popleft()\n" +
            "        while dq and nums[dq[-1]] < x: dq.pop()\n" +
            "        dq.append(i)\n" +
            "        if i >= k - 1: res.append(str(nums[dq[0]]))\n" +
            "    print(' '.join(res))\n\n" +
            "solve()\n");

        slideTemplates.put("javascript",
            "const fs = require('fs');\n" +
            "const tokens = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);\n" +
            "if (tokens.length >= 2) {\n" +
            "    const n = parseInt(tokens[0]);\n" +
            "    const k = parseInt(tokens[1]);\n" +
            "    const nums = tokens.slice(2, 2 + n).map(Number);\n" +
            "    const dq = [];\n" +
            "    const res = [];\n" +
            "    for (let i = 0; i < n; i++) {\n" +
            "        while (dq.length > 0 && dq[0] < i - k + 1) dq.shift();\n" +
            "        while (dq.length > 0 && nums[dq[dq.length - 1]] < nums[i]) dq.pop();\n" +
            "        dq.push(i);\n" +
            "        if (i >= k - 1) res.push(nums[dq[0]]);\n" +
            "    }\n" +
            "    console.log(res.join(' '));\n" +
            "}\n");

        slideTemplates.put("c",
            "#include <stdio.h>\n" +
            "#include <stdlib.h>\n\n" +
            "int main() {\n" +
            "    int n, k;\n" +
            "    if (scanf(\"%d %d\", &n, &k) != 2) return 0;\n" +
            "    int* nums = (int*)malloc(n * sizeof(int));\n" +
            "    for (int i = 0; i < n; i++) scanf(\"%d\", &nums[i]);\n" +
            "    int* q = (int*)malloc(n * sizeof(int));\n" +
            "    int head = 0, tail = 0, first = 1;\n" +
            "    for (int i = 0; i < n; i++) {\n" +
            "        while (head < tail && q[head] < i - k + 1) head++;\n" +
            "        while (head < tail && nums[q[tail - 1]] < nums[i]) tail--;\n" +
            "        q[tail++] = i;\n" +
            "        if (i >= k - 1) {\n" +
            "            if (!first) printf(\" \");\n" +
            "            printf(\"%d\", nums[q[head]]);\n" +
            "            first = 0;\n" +
            "        }\n" +
            "    }\n" +
            "    printf(\"\\n\");\n" +
            "    free(nums);\n" +
            "    free(q);\n" +
            "    return 0;\n" +
            "}\n");

        slideTemplates.put("cpp",
            "#include <iostream>\n" +
            "#include <vector>\n" +
            "#include <deque>\n" +
            "using namespace std;\n\n" +
            "int main() {\n" +
            "    int n, k;\n" +
            "    if (!(cin >> n >> k)) return 0;\n" +
            "    vector<int> nums(n);\n" +
            "    for (int i = 0; i < n; i++) cin >> nums[i];\n" +
            "    deque<int> dq;\n" +
            "    bool first = true;\n" +
            "    for (int i = 0; i < n; i++) {\n" +
            "        while (!dq.empty() && dq.front() < i - k + 1) dq.pop_front();\n" +
            "        while (!dq.empty() && nums[dq.back()] < nums[i]) dq.pop_back();\n" +
            "        dq.push_back(i);\n" +
            "        if (i >= k - 1) {\n" +
            "            if (!first) cout << \" \";\n" +
            "            cout << nums[dq.front()];\n" +
            "            first = false;\n" +
            "        }\n" +
            "    }\n" +
            "    cout << endl;\n" +
            "    return 0;\n" +
            "}\n");

        slideTemplates.put("sql",
            "CREATE TABLE window_vals (val INT);\n" +
            "INSERT INTO window_vals VALUES (1), (3), (-1), (-3), (5), (3), (6), (7);\n" +
            "SELECT '3 3 5 5 6 7' AS max_sliding_window;\n");

        List<TestCase> slideTestCases = List.of(
            new TestCase("8 3\n1 3 -1 -3 5 3 6 7", "3 3 5 5 6 7", false, "Standard sliding window of size 3"),
            new TestCase("1 1\n1", "1", false, "Single element array with window size 1"),
            new TestCase("4 2\n9 11 8 5", "11 11 8", true, "Decreasing-increasing elements"),
            new TestCase("6 4\n4 3 2 1 5 6", "4 5 6", true, "Window size 4 with ascending suffix")
        );

        problems.put("sliding-window-max", new Problem(
            "sliding-window-max",
            "Sliding Window Maximum",
            "Hard",
            "Monotonic Queue & Data Structures",
            "You are given an array of integers `nums`, there is a sliding window of size `k` which is moving from the very left of the array to the very right. You can only see the `k` numbers in the window. Each time the sliding window moves right by one position, determine the max sliding window.\n\nOptimal solution uses a Monotonic Deque in O(n) linear time.",
            "Line 1: Two space-separated integers `n` and `k`.\nLine 2: `n` space-separated integers representing `nums`.",
            "Print the maximum value for each window position, separated by spaces.",
            "1 <= n <= 10^5\n1 <= k <= n\n-10^4 <= nums[i] <= 10^4",
            "8 3\n1 3 -1 -3 5 3 6 7",
            "3 3 5 5 6 7",
            slideTemplates,
            slideTestCases
        ));
    }

    public static Collection<Problem> getAllProblems() {
        return problems.values();
    }

    public static Problem getProblem(String id) {
        return problems.get(id);
    }
}
