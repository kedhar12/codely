package server;

import java.util.*;
import java.util.stream.Collectors;

public class InterviewService {

    public static class InterviewQuestion {
        public String id;
        public String category; // technical, hr, cognitive, communication, coding
        public String subject;
        public String level; // Easy, Medium, Hard
        public String title;
        public String question;
        public String codeSnippet;
        public String modelAnswer;
        public List<String> keyPoints;
        public String interviewerTips;
        public List<String> options;
        public int correctOptionIndex;

        public InterviewQuestion(
            String id, String category, String subject, String level,
            String title, String question, String codeSnippet,
            String modelAnswer, List<String> keyPoints, String interviewerTips,
            List<String> options, int correctOptionIndex
        ) {
            this.id = id;
            this.category = category;
            this.subject = subject;
            this.level = level;
            this.title = title;
            this.question = question;
            this.codeSnippet = codeSnippet;
            this.modelAnswer = modelAnswer;
            this.keyPoints = keyPoints != null ? keyPoints : Collections.emptyList();
            this.interviewerTips = interviewerTips;
            this.options = options != null ? options : Collections.emptyList();
            this.correctOptionIndex = correctOptionIndex;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", id);
            map.put("category", category);
            map.put("subject", subject);
            map.put("level", level);
            map.put("title", title);
            map.put("question", question);
            if (codeSnippet != null) map.put("codeSnippet", codeSnippet);
            map.put("modelAnswer", modelAnswer);
            map.put("keyPoints", keyPoints);
            map.put("interviewerTips", interviewerTips);
            if (options != null && !options.isEmpty()) {
                map.put("options", options);
                map.put("correctOptionIndex", correctOptionIndex);
            }
            return map;
        }
    }

    private static final List<InterviewQuestion> questionBank = new ArrayList<>();

    static {
        // ==========================================
        // 1. TECHNICAL QUESTIONS (30+ Questions across all subjects)
        // ==========================================

        // --- Java (Easy, Medium, Hard) ---
        questionBank.add(new InterviewQuestion(
            "tech-java-1", "technical", "java", "Easy",
            "Difference between == and .equals() in Java",
            "How does the `==` operator differ from the `.equals()` method in Java, especially when dealing with String objects?",
            "String s1 = \"hello\";\nString s2 = new String(\"hello\");\nSystem.out.println(s1 == s2);\nSystem.out.println(s1.equals(s2));",
            "In Java, `==` compares object references (memory addresses) for non-primitive types, whereas `.equals()` evaluates value equality based on the class's overridden implementation.\nIn the example above:\n- `s1` points to a string literal in the String Constant Pool.\n- `s2` is explicitly allocated on the Heap with `new`.\nTherefore, `s1 == s2` evaluates to `false` because they reference distinct heap allocations. In contrast, `s1.equals(s2)` evaluates to `true` because the String class overrides `.equals()` to compare characters sequentially.",
            List.of("Reference equality vs value equality", "String constant pool vs Heap allocation", "Overriding hashCode() when overriding equals()"),
            "Highlight why failing to override hashCode() when overriding equals() breaks HashSet and HashMap keys.",
            List.of("false true", "true true", "false false", "true false"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-java-2", "technical", "java", "Medium",
            "JVM Memory Model & Generational Garbage Collection",
            "Explain the JVM memory architecture (Heap, Metaspace, Stack) and how generational garbage collection optimizes memory deallocation.",
            null,
            "The JVM memory is segmented into:\n1. JVM Stack: Stores local primitive variables and method call frames per thread (LIFO, thread-safe, fast).\n2. Metaspace: Replaced PermGen in Java 8; stores class metadata in native memory, expanding dynamically.\n3. Heap Memory: Shared across all threads for dynamic object allocations. It is partitioned generationally:\n   - Young Generation (Eden + 2 Survivor spaces S0/S1): Objects are created in Eden. Minor GC uses stop-the-world copying collectors to promote surviving objects after reaching an aging threshold (tenuring threshold).\n   - Old (Tenured) Generation: Holds long-lived objects. Major/Full GC uses mark-sweep-compact algorithms (e.g. G1, ZGC) which have higher latency but run less frequently based on the weak generational hypothesis (most objects die young).",
            List.of("Stack vs Heap thread-safety", "Eden, Survivor (S0/S1), and Tenured spaces", "Weak Generational Hypothesis", "Modern collectors (G1, ZGC, Shenandoah)"),
            "Mention the Weak Generational Hypothesis: 95%+ of allocated objects become unreachable shortly after allocation.",
            List.of("Most objects die young, so short-lived collections avoid full heap scans", "Old gen collects faster than Young gen", "Stack memory is collected by Mark-Sweep", "Metaspace holds thread stack frames"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-java-3", "technical", "java", "Hard",
            "Virtual Threads vs Platform Threads & Java Memory Model (JMM)",
            "How do Java 21 Virtual Threads work under Project Loom, and how do `volatile`, `happens-before`, and CPU cache coherence relate to them?",
            "Thread.ofVirtual().start(() -> {\n    // Blocking I/O unmounts virtual thread from carrier\n    fetchRemoteData();\n});",
            "Platform threads in Java 1.0-20 wrap 1:1 OS kernel threads, incurring ~1MB stack memory and costly kernel context switches. Project Loom introduces Virtual Threads (M:N scheduling):\n- Millions of lightweight virtual threads run over a small pool of ForkJoinPool OS carrier threads.\n- When a virtual thread executes a blocking I/O operation, the JVM intercepts it via internal park() calls, unmounts the virtual thread's call frame to heap memory, and frees the carrier thread to execute other virtual threads.\nRegarding the Java Memory Model:\n- `volatile` prevents CPU instruction reordering and establishes a `happens-before` relationship by inserting memory fences (LoadLoad, LoadStore, StoreStore, StoreLoad).\n- Every write to a volatile variable is flushed to main memory, invalidating L1/L2 CPU caches across all carrier CPU cores.",
            List.of("M:N scheduling model with ForkJoinPool carriers", "Non-blocking continuations on heap during I/O", "Memory barriers/fences and happens-before relationship", "Pinning issues with synchronized blocks (use ReentrantLock)"),
            "Mention that synchronized blocks currently pin the virtual thread to its OS carrier; recommend java.util.concurrent.locks.ReentrantLock instead.",
            List.of("Virtual threads unmount from carrier threads during blocking I/O", "Virtual threads replace OS threads completely without carrier threads", "Volatile variables prevent garbage collection", "Synchronized blocks never pin virtual threads"), 0
        ));

        // --- Python ---
        questionBank.add(new InterviewQuestion(
            "tech-py-1", "technical", "python", "Easy",
            "Mutable vs Immutable Objects & Default Argument Traps",
            "Why is defining a mutable object (like a list or dict) as a default parameter in Python considered dangerous?",
            "def append_to(item, target=[]):\n    target.append(item)\n    return target\n\nprint(append_to(1))\nprint(append_to(2))",
            "In Python, default parameter expressions are evaluated once when the function definition is executed, NOT each time the function is invoked.\nBecause lists are mutable, the single list instance created at definition time is reused across subsequent invocations where the argument is omitted. Thus:\n`append_to(1)` returns `[1]`\n`append_to(2)` returns `[1, 2]` instead of `[2]`.\nThe standard idiomatic fix is to use `None` as the default value:\n```python\ndef append_to(item, target=None):\n    if target is None:\n        target = []\n    target.append(item)\n    return target\n```",
            List.of("Default arguments evaluated once at function definition", "Mutable object persistence across calls", "Idiomatic None sentinel pattern"),
            "Show the None sentinel pattern cleanly; interviewers love seeing defensive coding.",
            List.of("[1] then [1, 2]", "[1] then [2]", "SyntaxError: mutable default", "TypeError: list unhashable"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-py-2", "technical", "python", "Medium",
            "Python GIL, Multiprocessing vs Multithreading, and Asyncio",
            "What is the Global Interpreter Lock (GIL) in CPython, and when should you choose `multiprocessing`, `threading`, or `asyncio`?",
            null,
            "The Global Interpreter Lock (GIL) is a mutex in CPython that ensures only one native thread executes Python bytecode at any given moment, protecting CPython memory management and reference counts from race conditions.\nWhen to use each concurrency model:\n1. `multiprocessing`: Spawns separate OS processes with independent memory and GILs. Best for CPU-bound tasks (e.g. data crunching, image processing, matrix multiplication).\n2. `threading`: Best for I/O-bound tasks where threads spend most time blocked on system calls (network, disk, socket), where the GIL is released during OS operations.\n3. `asyncio`: Single-threaded cooperative multitasking utilizing an event loop and non-blocking sockets (`async`/`await`). Best for thousands of concurrent high-throughput I/O connections (e.g. web servers, chat services).",
            List.of("CPython memory management and reference counting", "CPU-bound vs I/O-bound trade-offs", "Process IPC overhead vs thread shared memory", "Event loop cooperative scheduling in asyncio"),
            "Clarify that Python 3.13 introduces experimental free-threaded mode (PEP 703) to disable the GIL.",
            List.of("Use multiprocessing for CPU-bound tasks and asyncio/threading for I/O-bound tasks", "Threading bypasses the GIL for CPU computations", "Asyncio spawns one OS thread per coroutine", "GIL is present in all Python implementations including Jython"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-py-3", "technical", "python", "Hard",
            "Python Metaclasses, `__new__` vs `__init__`, and C3 Linearization",
            "How does Python determine method resolution order (MRO) with multiple inheritance using C3 Linearization, and what role do metaclasses play?",
            "class Meta(type):\n    def __new__(cls, name, bases, dct):\n        dct['category'] = 'registered'\n        return super().__new__(cls, name, bases, dct)",
            "1. Metaclasses:\nIn Python, classes are themselves instances of metaclasses (`type`). `__new__` is called to allocate and create the class object itself before `__init__` initializes it. Metaclasses allow inspecting, altering, or validating class definitions at import time (used in ORMs like Django and Pydantic models).\n2. C3 Linearization MRO:\nWhen resolving attributes across multiple inheritance DAGs, Python applies C3 Linearization to guarantee:\n- Children precede their parents.\n- Order of parent listing in the class definition is strictly preserved.\n- Monotonicity: if class A precedes class B in a parent's MRO, it will precede B in any subclass's MRO.\nIf a consistent linearization cannot be created, Python raises a `TypeError: Cannot create a consistent method resolution order (MRO)`.",
            List.of("type as the default metaclass", "__new__ class creation vs __init__ initialization", "C3 Linearization 3 core invariants", "Resolving diamond problem deterministically"),
            "Show how `ClassName.__mro__` lets developers inspect the resolution chain directly.",
            List.of("Preserves local precedence and monotonicity while preventing cycles", "Performs depth-first search ignoring duplicate base classes", "Randomly selects the first matching method", "Always prioritizes object base class"), 0
        ));

        // --- C & C++ ---
        questionBank.add(new InterviewQuestion(
            "tech-c-1", "technical", "c", "Easy",
            "Pointers, Addresses, and Dereferencing in C",
            "Explain the difference between `*ptr`, `&var`, and pointer arithmetic in C. What causes a segmentation fault?",
            "int arr[3] = {10, 20, 30};\nint* p = arr;\np++;\nprintf(\"%d\", *p);",
            "In C:\n- `&var` yields the hexadecimal memory address of `var`.\n- `int* ptr = &var` declares a pointer storing that memory address.\n- `*ptr` (dereference operator) accesses or modifies the value stored at that address.\n- Pointer arithmetic: `p++` increments the memory address by `sizeof(type)` bytes (4 bytes for standard 32-bit int), moving `p` from `arr[0]` to `arr[1]`. Thus `*p` prints `20`.\nA segmentation fault occurs when a program attempts to read or write memory that the OS has not mapped or permitted (e.g. dereferencing NULL, accessing freed memory, or writing to read-only string literals).",
            List.of("& (address-of) vs * (dereference)", "Pointer arithmetic scales with sizeof(type)", "Null pointer dereference and out-of-bounds access causing SIGSEGV"),
            "Emphasize that pointer arithmetic moves by sizeof(data_type) bytes, not raw 1-byte increments.",
            List.of("20", "10", "30", "Segmentation Fault"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-c-2", "technical", "c", "Medium",
            "Stack vs Heap Allocation and Memory Leaks in C",
            "Contrast stack allocation with heap allocation using `malloc`/`free`. How do memory leaks and dangling pointers arise?",
            "int* bad_function() {\n    int x = 42;\n    return &x; // WARNING\n}",
            "1. Stack Allocation:\nLocal variables are pushed onto the thread stack frame automatically on function entry and popped on return. Fast, fixed size, but lifetime is restricted to the local scope. Returning `&x` yields a dangling pointer because `x`'s stack frame is reclaimed upon function exit.\n2. Heap Allocation (`malloc`/`calloc`/`realloc`/`free`):\nAllocated dynamically from the process heap at runtime. Lifetime persists until explicitly released with `free()`. \n- Memory leak: Occurs when allocated heap memory is never freed and all pointers to it are lost.\n- Dangling pointer: Occurs when a pointer continues to reference memory after it has been freed (`free(ptr);` without setting `ptr = NULL;`).",
            List.of("Automatic stack frame unwinding vs manual heap management", "Dangling pointers caused by returning local addresses or double freeing", "Tools: Valgrind and AddressSanitizer (ASan)"),
            "Mention setting `ptr = NULL` immediately after `free(ptr)` to prevent accidental double-free errors.",
            List.of("Returning address of stack variable produces dangling pointer", "Heap memory frees automatically on function exit", "Stack memory persists across thread execution", "Malloc zero-initializes memory"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-cpp-1", "technical", "cpp", "Medium",
            "RAII and Modern Smart Pointers in C++",
            "What is Resource Acquisition Is Initialization (RAII), and how do `std::unique_ptr` and `std::shared_ptr` implement it?",
            "#include <memory>\nauto p1 = std::make_unique<Resource>();\n// Ownership cannot be copied, only moved\nauto p2 = std::move(p1);",
            "RAII (Resource Acquisition Is Initialization) binds the lifecycle of a resource (heap memory, file handles, database sockets, mutex locks) to the lifetime of a stack-allocated object. When the wrapper leaves scope, its destructor is automatically called, guaranteeing deterministic cleanup even when exceptions are thrown.\nModern Smart Pointers (`<memory>`):\n1. `std::unique_ptr`: Exclusive ownership model with zero runtime overhead over a raw pointer. Disallows copy construction/assignment; only supports transfer of ownership via move semantics (`std::move`).\n2. `std::shared_ptr`: Shared ownership using atomic reference counting. When the internal reference count drops to 0, the managed object is destroyed.\n3. `std::weak_ptr`: Non-owning observer that breaks circular references between `shared_ptr` instances.",
            List.of("RAII deterministic destructor execution on stack unwind", "Exclusive ownership with std::unique_ptr and std::move", "Atomic reference counting with std::shared_ptr", "Breaking circular reference deadlocks with std::weak_ptr"),
            "Highlight why `make_unique` and `make_shared` are preferred over raw `new`: single memory allocation and exception safety.",
            List.of("RAII automatically releases resources in destructors when scope ends", "Smart pointers completely disable stack allocations", "unique_ptr can be duplicated with copy constructor", "shared_ptr is susceptible to memory leaks in single ownership"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-cpp-2", "technical", "cpp", "Hard",
            "Virtual Destructors & Object Slicing in C++",
            "Why must a base class with virtual functions always declare a `virtual ~Base()` destructor? What is object slicing?",
            "Base* b = new Derived();\ndelete b; // What happens if ~Base() is not virtual?",
            "1. Virtual Destructor:\nIf `Base` does not declare a `virtual ~Base()`, executing `delete b;` performs compile-time static binding based on the pointer type (`Base*`). This invokes only `~Base()` and skips `~Derived()`. Any heap memory, file handles, or network sockets held by `Derived` will leak.\nDeclaring `virtual ~Base()` ensures dynamic dispatch via the vtable, executing `~Derived()` first, followed by `~Base()`.\n2. Object Slicing:\nOccurs when an object of a derived class is assigned or passed by value to a base class object (`Base b = derivedObj;`). The derived portion containing member variables and overridden methods is literally 'sliced off', retaining only the base subobject.",
            List.of("vtable dynamic dispatch for correct destructor chain", "Memory leaks in derived classes without virtual destructors", "Passing by reference/pointer (const Base&) to prevent slicing"),
            "Mention the Rule of Five in modern C++: if you define destructor, copy constructor, copy assignment, move constructor, or move assignment, define all five.",
            List.of("Without virtual destructor, delete on base pointer skips derived destructor causing leaks", "Virtual destructors prevent object instantiation", "Object slicing accelerates polymorphism", "Derived destructors run before base destructors automatically without vtable"), 0
        ));

        // --- SQL & Databases ---
        questionBank.add(new InterviewQuestion(
            "tech-sql-1", "technical", "sql", "Easy",
            "INNER JOIN vs LEFT JOIN vs RIGHT JOIN",
            "Explain the difference between `INNER JOIN`, `LEFT JOIN`, and `FULL OUTER JOIN` with real-world table examples.",
            "SELECT e.name, d.dept_name\nFROM employees e\nLEFT JOIN departments d ON e.dept_id = d.id;",
            "In relational SQL:\n1. `INNER JOIN`: Returns only the rows where there is a matching key in both tables. If an employee has no department or a department has no employees, they are excluded.\n2. `LEFT JOIN` (LEFT OUTER JOIN): Returns all rows from the left table (`employees`), matching with rows from the right table (`departments`). If no match exists, columns from the right table are populated with `NULL`.\n3. `RIGHT JOIN`: Returns all rows from the right table, filling missing left table matches with `NULL`.\n4. `FULL OUTER JOIN`: Combines both LEFT and RIGHT joins, returning all records from both tables with `NULL` wherever matches are absent.",
            List.of("Matching keys vs NULL padding for unmatched rows", "Handling orphaned records", "Cartesian product prevention by ensuring ON condition"),
            "Clarify how to filter for orphaned rows using `WHERE right_table.id IS NULL`.",
            List.of("LEFT JOIN returns all left table rows and matched right table rows with NULLs for misses", "INNER JOIN returns all rows from both tables", "RIGHT JOIN deletes missing rows", "LEFT JOIN requires foreign key constraints to function"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-sql-2", "technical", "sql", "Medium",
            "Database Indexing (B-Trees) and Query Optimization",
            "How do B-Tree and Hash indexes speed up SQL queries, and why can indexing degrade write performance?",
            "EXPLAIN ANALYZE\nSELECT * FROM orders\nWHERE customer_id = 1042 AND status = 'COMPLETED';",
            "1. B-Tree Indexes:\nOrganize keys into balanced multi-way search trees. They support equality lookups (`=`), range queries (`BETWEEN`, `<`, `>`), and sorting (`ORDER BY`) in O(log N) time.\n2. Composite Indexes & Leftmost Prefix Rule:\nAn index on `(customer_id, status)` can satisfy queries filtering on `customer_id` alone, or both `customer_id` and `status`, but cannot be used for `status` alone.\n3. Trade-offs:\n- Read Performance: Drastically reduces disk I/O from full table scans (O(N)) to index lookups.\n- Write Degradation: Every `INSERT`, `UPDATE`, or `DELETE` requires updating both the raw table pages and rebalancing the associated B-Tree index pages.",
            List.of("B-Tree balanced search structure (O(log N))", "Leftmost prefix rule for composite indexes", "Write amplification on INSERT/UPDATE/DELETE", "Using EXPLAIN ANALYZE to identify sequential scans"),
            "Show familiarity with covering indexes (index containing all queried columns, eliminating table lookups).",
            List.of("B-Trees provide O(log N) lookups for equality and ranges, but every insert requires index rebalancing", "Indexes speed up inserts by pre-sorting data", "Hash indexes support range scans better than B-Trees", "Composite indexes ignore column ordering in queries"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-sql-3", "technical", "sql", "Hard",
            "ACID Properties & Transaction Isolation Levels",
            "Explain ACID properties in relational DBMS and detail the 4 SQL standard transaction isolation levels.",
            "-- Transaction 1\nSET TRANSACTION ISOLATION LEVEL REPEATABLE READ;\nBEGIN TRANSACTION;\nSELECT COUNT(*) FROM inventory WHERE price > 100;",
            "ACID Properties:\n- Atomicity: All operations in a transaction succeed or all rollback (all-or-nothing).\n- Consistency: Transactions transition database from one valid state to another, upholding constraints.\n- Isolation: Concurrent transactions do not interfere with one another.\n- Durability: Once committed, changes survive system crashes (via Write-Ahead Logging / WAL).\n\nIsolation Levels (from least to most strict):\n1. Read Uncommitted: Allows Dirty Reads (reading uncommitted writes from other transactions).\n2. Read Committed: Prevents dirty reads. Allows Non-Repeatable Reads (row re-read yields modified values).\n3. Repeatable Read: Uses snapshot MVCC or shared locks; ensures rows read once do not change. In MySQL InnoDB, next-key locks also prevent Phantom Reads (new rows inserted matching range).\n4. Serializable: Full serial execution order via range locks or optimistic concurrency; highest consistency, lowest throughput.",
            List.of("Atomicity via rollback logs", "Durability via Write-Ahead Log (WAL)", "Dirty reads vs Non-repeatable reads vs Phantom reads", "MVCC (Multi-Version Concurrency Control) implementation"),
            "Mention how MVCC avoids read-write locking by maintaining versioned tuples in InnoDB/Postgres.",
            List.of("Read Committed prevents dirty reads; Repeatable Read prevents non-repeatable reads; Serializable prevents phantoms", "Read Uncommitted is the default for banking systems", "Serializable executes queries with zero locking", "Durability means queries never fail"), 0
        ));

        // --- Data Structures & Algorithms ---
        questionBank.add(new InterviewQuestion(
            "tech-dsa-1", "technical", "dsa", "Easy",
            "Array vs Linked List: Memory, Access, and Insertion",
            "Compare Arrays and Linked Lists regarding memory layout, cache locality, and algorithmic time complexity.",
            null,
            "1. Memory Layout & Cache Locality:\n- Arrays: Contiguous memory blocks. High spatial cache locality because sequential CPU cache lines prefetch adjacent elements.\n- Linked Lists: Non-contiguous heap nodes connected by pointers. Poor cache locality due to pointer chasing across scattered memory.\n2. Time Complexity:\n- Random Access: Array is O(1) via index arithmetic `base + i * size`. Linked List is O(N) traversing pointers.\n- Insertion/Deletion at Beginning: Array is O(N) due to element shifting. Linked List is O(1) updating head pointer.\n- Insertion at Arbitrary Index: Array is O(N) for shifting. Linked List is O(N) to traverse + O(1) to update pointers.\n- Memory Overhead: Array has no pointer overhead. Linked List requires 8 bytes (64-bit) extra per node for pointer.",
            List.of("Contiguous memory and CPU cache line prefetching", "O(1) random access in arrays", "O(1) prepend in linked lists", "Memory overhead of pointers"),
            "Emphasize CPU cache line prefetching: modern processors make arrays faster than linked lists even for some insertions due to hardware cache hits.",
            List.of("Arrays offer O(1) random access and superior CPU cache locality; Linked Lists offer O(1) head insertion", "Linked lists provide O(1) random access", "Arrays use more memory per element due to node pointers", "Linked lists store elements in contiguous memory"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-dsa-2", "technical", "dsa", "Medium",
            "Hash Table Collision Resolution: Chaining vs Open Addressing",
            "How do Hash Maps achieve O(1) average lookup, and what happens when hash collisions occur?",
            null,
            "A Hash Map maps keys to bucket indices using a hash function: `index = hash(key) % capacity`.\nAverage time complexity is O(1) for insert, search, and delete.\nCollision Resolution Techniques:\n1. Separate Chaining:\nEach bucket holds a linked list or balanced red-black tree (as in Java 8+ HashMap when bucket size exceeds 8). Colliding elements are appended to the bucket chain. In the worst case (all keys hash to the same bucket), lookup degrades to O(log N) or O(N).\n2. Open Addressing (Linear Probing, Quadratic Probing, Double Hashing):\nAll elements reside directly in the table array. If a collision occurs at index `i`, probing investigates `i + 1`, `i + 2`, etc., until an empty slot is located.\n- Load Factor (alpha = N / capacity): When the load factor exceeds a threshold (typically 0.75), the map dynamically rehashes into an array of double capacity to maintain O(1) performance.",
            List.of("Hash function distribution and index calculation", "Separate chaining with linked lists / red-black trees", "Open addressing probing techniques", "Load factor threshold and rehashing overhead"),
            "Note that Java 8 transforms linked lists into Red-Black Trees (TreeNode) when bucket length > 8, improving worst-case search from O(N) to O(log N).",
            List.of("Separate chaining uses linked lists/trees in buckets, while open addressing probes for open array slots", "Hash collisions cause fatal runtime exceptions", "Open addressing creates infinite arrays", "Load factor 1.0 is required before any hash map allocates memory"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-dsa-3", "technical", "dsa", "Hard",
            "Dynamic Programming: Memoization vs Tabulation & Optimal Substructure",
            "What distinguishes Top-Down DP (Memoization) from Bottom-Up DP (Tabulation)? How do you identify whether a problem can be solved with DP?",
            "// Top-Down: Recursion + Cache\n// Bottom-Up: Iteration + DP Table",
            "A problem is solvable via Dynamic Programming if it possesses two primary characteristics:\n1. Overlapping Subproblems: The same smaller subproblems are computed repeatedly (e.g. Fibonacci, 0/1 Knapsack, Longest Common Subsequence).\n2. Optimal Substructure: An optimal solution to the overall problem incorporates optimal solutions to its constituent subproblems.\n\nTwo Paradigms:\n- Top-Down (Memoization):\nStarts with the original problem and recursively breaks it down. Results of solved subproblems are cached in a hash map or array. Easy to write; uses call stack memory (potential recursion depth overflow).\n- Bottom-Up (Tabulation):\nIteratively solves base subproblems first, populating a DP table from smallest subproblem upwards to the target. Eliminates recursion call stack overhead and frequently allows space optimization (e.g. reducing O(N) space to O(1) by storing only previous states).",
            List.of("Overlapping subproblems + Optimal substructure criteria", "Top-down recursion stack vs bottom-up iterative table", "State space optimization (e.g. 2 variables instead of full array)", "Time-space trade-offs"),
            "Give a concrete example: Knapsack or Fibonacci where space optimization reduces O(N) array to two scalar variables.",
            List.of("Top-down uses recursion with caching; bottom-up builds iteratively from base cases avoiding stack overflow", "Bottom-up requires exponential recursion call stack", "Memoization cannot be used for overlapping subproblems", "Greedy algorithms always solve DP problems faster"), 0
        ));

        // --- DBMS ---
        questionBank.add(new InterviewQuestion(
            "tech-dbms-1", "technical", "dbms", "Easy",
            "Database Normalization: 1NF, 2NF, 3NF, and BCNF",
            "What is the objective of database normalization, and how do you achieve 1NF, 2NF, and 3NF?",
            null,
            "The objective of normalization is to minimize data redundancy and eliminate insert, update, and deletion anomalies while maintaining data integrity.\n- 1NF (First Normal Form):\nEach column must contain atomic (indivisible) values. No repeating groups or arrays stored in a single field.\n- 2NF (Second Normal Form):\nMust be in 1NF, and all non-key attributes must be fully functionally dependent on the entire primary key (no partial dependencies on a composite primary key).\n- 3NF (Third Normal Form):\nMust be in 2NF, and there must be no transitive dependencies (non-key attributes must not depend on other non-key attributes: 'X -> Y' where neither is candidate key).\n- BCNF (Boyce-Codd Normal Form):\nStricter 3NF where for every functional dependency `X -> Y`, `X` must be a super key.",
            List.of("Anomalies: Insertion, Update, Deletion", "1NF atomic values", "2NF removing partial dependencies", "3NF removing transitive dependencies"),
            "Explain that over-normalizing requires excessive JOINs; denormalization is often deliberately used in read-heavy data warehouses.",
            List.of("1NF requires atomic values; 2NF removes partial key dependencies; 3NF removes transitive dependencies", "3NF requires all tables to be merged into one single schema", "Normalization increases data duplication to accelerate write operations", "BCNF allows non-key attributes to determine primary keys"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-dbms-2", "technical", "dbms", "Medium",
            "SQL vs NoSQL: CAP Theorem and Data Modeling",
            "When should an architecture use a Relational Database (PostgreSQL/MySQL) versus a NoSQL database (MongoDB/Cassandra)?",
            null,
            "1. Relational Databases (RDBMS):\n- Model: Structured tables, schemas, strict foreign key constraints, ACID compliance.\n- Best for: Financial ledger systems, e-commerce checkouts, complex relational queries, multi-table transactions.\n- Scaling: Traditionally vertical (bigger CPU/RAM), read replicas.\n2. NoSQL Databases:\n- Document (MongoDB): Flexible JSON-like schemas, fast iteration for unstructured/semi-structured data.\n- Key-Value (Redis): High-throughput sub-millisecond in-memory caching and session state.\n- Column-Family (Cassandra): High write throughput, horizontal partition scaling across multiple data centers.\n3. CAP Theorem:\nA distributed system can guarantee at most two of three properties:\n- Consistency: Every read receives the most recent write.\n- Availability: Every non-failing node returns a response.\n- Partition Tolerance: System functions despite arbitrary network message loss.",
            List.of("ACID vs BASE (Basically Available, Soft state, Eventual consistency)", "CAP theorem trade-offs (CP vs AP)", "Horizontal sharding vs vertical scaling"),
            "Point out that modern systems employ Polyglot Persistence: PostgreSQL for transactions + Redis for caching + Elasticsearch for text search.",
            List.of("RDBMS prioritizes ACID and strict relations; NoSQL provides flexible schemas and horizontal partitioning for massive scale", "NoSQL databases guarantee 100% ACID consistency across distributed clusters without latency", "Relational databases cannot scale to multiple read replicas", "MongoDB replaces SQL because relational queries are obsolete"), 0
        ));

        // --- Object-Oriented Programming (OOP) ---
        questionBank.add(new InterviewQuestion(
            "tech-oop-1", "technical", "oop", "Easy",
            "The 4 Pillars of OOP and Real-World Examples",
            "Define the four fundamental pillars of Object-Oriented Programming (Encapsulation, Abstraction, Inheritance, Polymorphism) with real-world design examples.",
            null,
            "1. Encapsulation: Bundling data (state) and methods (behavior) within a single unit (class), restricting direct access to internal components using private access modifiers and providing controlled public getters/setters.\n   Example: A BankAccount class shielding `balance` from direct modification, exposing `deposit()` and `withdraw()` with validation checks.\n2. Abstraction: Hiding complex implementation details and showing only essential interfaces to the user.\n   Example: Calling `car.drive()` without needing to manage spark plugs, fuel injection, or transmission gears.\n3. Inheritance: Mechanism where a child class acquires fields and behaviors from a parent class, promoting code reuse.\n   Example: `ElectricCar` extends `Vehicle`.\n4. Polymorphism: Ability of an object or method to take many forms. Includes compile-time (method overloading) and runtime (method overriding via virtual dispatch).\n   Example: `Shape.draw()` produces a circle, rectangle, or triangle at runtime depending on the dynamic instance.",
            List.of("Encapsulation: data hiding & access control", "Abstraction: interface vs implementation separation", "Inheritance: IS-A relationship & reuse", "Polymorphism: static overloading vs dynamic overriding"),
            "Highlight why composition is often preferred over deep inheritance hierarchies ('Favor composition over inheritance').",
            List.of("Encapsulation, Abstraction, Inheritance, Polymorphism", "Compilation, Execution, Linking, Loading", "Classes, Objects, Structs, Pointers", "Model, View, Controller, Router"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-oop-2", "technical", "oop", "Medium",
            "SOLID Design Principles Explained",
            "Walk through the SOLID principles with concrete examples of how they make software maintainable and scalable.",
            null,
            "SOLID stands for:\n1. Single Responsibility Principle (SRP): A class should have one, and only one, reason to change.\n   Violation: User class managing both business logic and writing SQL database queries.\n2. Open/Closed Principle (OCP): Software entities should be open for extension, but closed for modification.\n   Pattern: Use interfaces or strategy patterns so adding a new payment gateway doesn't alter existing checkout code.\n3. Liskov Substitution Principle (LSP): Subtypes must be substitutable for their base types without altering program correctness.\n   Classic violation: `Square` extending `Rectangle` and breaking width/height setters.\n4. Interface Segregation Principle (ISP): Clients should not be forced to depend upon interfaces they do not use (prefer small, role-specific interfaces).\n5. Dependency Inversion Principle (DIP): High-level modules should not depend on low-level modules; both should depend on abstractions (interfaces).",
            List.of("Single Responsibility: one reason to change", "Open/Closed: extend via interfaces without modifying code", "Liskov Substitution: subclass substitutability", "Interface Segregation: focused, lean interfaces", "Dependency Inversion: depend on abstractions, not concretions"),
            "Mention how Dependency Injection (like Spring IoC or Guice) directly implements the Dependency Inversion Principle.",
            List.of("Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion", "Simple, Optimized, Linear, Integrated, Dynamic", "Synchronized, Object-oriented, Linked, Isolated, Distributed", "Speed, Overhead, Latency, Integrity, Durability"), 0
        ));

        // --- Computer Networks ---
        questionBank.add(new InterviewQuestion(
            "tech-net-1", "technical", "networks", "Easy",
            "TCP vs UDP: Handshake, Reliability, and Use Cases",
            "Contrast TCP and UDP protocols. How does TCP establish a connection, and when would you pick UDP over TCP?",
            null,
            "1. Transmission Control Protocol (TCP):\n- Connection-Oriented: Establishes a reliable connection via the 3-Way Handshake (SYN -> SYN-ACK -> ACK).\n- Reliability: Guarantees delivery via sequence numbers, checksums, retransmissions of lost packets, and acknowledgments.\n- Flow & Congestion Control: Regulates transmission speed to prevent buffer overflow (Sliding Window, Congestion Window).\n- Overhead: Higher latency, 20-byte minimum header.\n- Use Cases: HTTP/HTTPS, SSH, FTP, Email (SMTP), database transactions.\n2. User Datagram Protocol (UDP):\n- Connectionless: Sends datagrams without prior handshake ('fire-and-forget').\n- Unreliable: No packet ordering, acknowledgments, or automatic retransmissions.\n- Overhead: Minimal latency, lightweight 8-byte header.\n- Use Cases: Real-time gaming, VoIP, DNS queries, live video streaming (WebRTC), where low latency supersedes packet loss.",
            List.of("3-Way Handshake: SYN, SYN-ACK, ACK", "Flow control vs Congestion control in TCP", "Ordered delivery vs fire-and-forget", "Low-latency UDP for real-time media"),
            "Mention QUIC / HTTP/3: built on UDP in user-space to achieve TCP reliability with zero head-of-line blocking.",
            List.of("TCP provides reliable, ordered transmission via 3-way handshake; UDP provides low-latency, connectionless transmission", "UDP guarantees in-order packet delivery without retransmissions", "TCP is connectionless and has lower latency than UDP", "HTTP/1.1 runs natively over UDP"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "tech-net-2", "technical", "networks", "Medium",
            "What Happens When You Type a URL in Your Browser?",
            "Detail the complete sequence of events from typing 'https://codely.dev' into your browser address bar to the page rendering on screen.",
            null,
            "1. URL Parsing & HSTS: Browser parses scheme (HTTPS), domain, and checks local cache/HSTS list.\n2. DNS Resolution: Resolves domain to IP via browser cache -> OS cache -> router cache -> ISP recursive resolver -> Root DNS -> TLD (.dev) -> Authoritative Nameserver.\n3. TCP 3-Way Handshake: SYN -> SYN-ACK -> ACK establishes reliable socket on port 443.\n4. TLS Handshake (TLS 1.3): Client Hello -> Server Hello + Certificate -> Key Exchange (Diffie-Hellman) -> Session keys encrypted.\n5. HTTP Request & Server Processing: Browser sends `GET / HTTP/2`; load balancer routes to reverse proxy / web server; application generates response.\n6. HTTP Response: Returns status 200 OK with HTML content and security headers.\n7. Browser Rendering Engine:\n   - Parses HTML to construct DOM Tree.\n   - Parses CSS to construct CSSOM Tree.\n   - Combines DOM and CSSOM to build Render Tree.\n   - Computes Layout (reflow) and Paints pixels on screen (rasterization).\n   - Executes JavaScript asynchronously.",
            List.of("DNS recursive hierarchy", "TCP 3-way handshake + TLS 1.3 cryptographic negotiation", "HTTP request/response headers", "DOM, CSSOM, Render tree, Layout & Paint steps"),
            "Structure the response sequentially from network layer to browser rendering engine; interviewers look for systems clarity.",
            List.of("DNS resolution -> TCP Handshake -> TLS Negotiation -> HTTP Request/Response -> DOM/CSSOM Render Pipeline", "Browser immediately paints pixels before resolving DNS", "TLS handshake occurs before TCP handshake", "DNS queries bypass OS and router caches directly to Authoritative servers"), 0
        ));

        // ==========================================
        // 2. HR & BEHAVIORAL QUESTIONS (STAR Method)
        // ==========================================

        // --- 1. Tell Me About Yourself ---
        questionBank.add(new InterviewQuestion(
            "hr-about-1", "hr", "about-me", "Medium",
            "Tell Me About Yourself (The Present-Past-Future Formula)",
            "How should you structure your response to 'Tell me about yourself' to hook the interviewer in under 90 seconds?",
            null,
            "The ideal answer uses the Present-Past-Future Formula:\n\n1. Present (30s): Who you are today, your current role or university specialization, and your primary technical domain.\n\"I'm a full-stack software engineer with a strong focus on high-performance Java backends and modern web interfaces. In my recent work, I designed and built scalable coding platforms that handle concurrent code executions in virtual thread environments.\"\n\n2. Past (30s): 1-2 major past achievements, relevant projects, or experiences demonstrating technical grit.\n\"Prior to this, I developed algorithmic grading engines and distributed question banks, where I optimized relational database queries to cut latency by 40% and implemented automated grading pipelines across 6 programming languages.\"\n\n3. Future (30s): Why you are sitting in this interview and why this role is the natural next step.\n\"I'm passionate about building robust systems that impact developers and learners at scale. When I saw this role at your company focusing on reliable distributed architecture, I knew my technical background and problem-solving drive made this an ideal match.\"",
            List.of("Present-Past-Future structure (under 90 seconds)", "Highlight quantifiable technical achievements", "Tie your ending directly to why you want THIS specific company", "Avoid reciting your resume line by line"),
            "Keep it under 90-120 seconds. Avoid personal life history; focus on professional competence, enthusiasm, and cultural alignment.",
            List.of("Structure response using Present-Past-Future formula focusing on key achievements and alignment with the role", "Recite your entire life story and childhood hobbies", "Read your resume word-for-word from top to bottom", "Ask the interviewer to read your LinkedIn profile instead"), 0
        ));

        // --- 2. Strengths and Weaknesses ---
        questionBank.add(new InterviewQuestion(
            "hr-strength-1", "hr", "strengths-weaknesses", "Medium",
            "What Are Your Greatest Strengths and Weaknesses?",
            "How do you authentically discuss a real professional weakness without raising red flags or giving cliché non-answers like 'I'm a perfectionist'?",
            null,
            "1. Greatest Strength:\nChoose a strength relevant to software engineering backed by proof.\n\"My greatest strength is systematic debugging and root-cause analysis under pressure. When a critical production service degraded during an assessment exam, instead of applying temporary patches, I used profiling tools to isolate a thread pool starvation issue in our executor service, resolved it, and added telemetry alerts to prevent recurrence.\"\n\n2. Real Weakness with Mitigation Plan:\nNever say 'I work too hard' or 'I am a perfectionist'. Pick a genuine, non-disqualifying technical or managerial trait and explain how you are proactively mitigating it.\n\"In the past, I tended to dive directly into code implementation before fully fleshing out edge-case architecture specifications with my team. I realized this could lead to refactoring late in the sprint. To overcome this, I adopted a habit of writing short design docs (RFCs) and presenting them for peer review before writing a single line of code. This has dramatically streamlined our sprint velocity.\"",
            List.of("Back strengths with concrete metrics or specific stories", "Never use cliché humble-brags ('perfectionist')", "Pick a genuine weakness and show active steps you take to overcome it", "Focus on professional self-awareness and coachability"),
            "Interviewers are evaluating self-awareness and emotional maturity. Showing actionable growth makes weaknesses positive.",
            List.of("State a real, non-fatal weakness coupled with the proactive system or habit you adopted to improve it", "Claim you have no weaknesses because you are flawless", "Say 'I work too hard and care too much about perfection'", "Confess that you struggle to wake up on time for meetings"), 0
        ));

        // --- 3. Why Should We Hire You? ---
        questionBank.add(new InterviewQuestion(
            "hr-hire-1", "hr", "why-hire-you", "Medium",
            "Why Should We Hire You?",
            "How do you position your unique value proposition to convince the hiring team that you are the best candidate for this role?",
            null,
            "Structure your response around the 3-Pillar Value Proposition:\n\n1. Technical Competence (You can do the work):\n\"You need someone who can immediately contribute to your backend infrastructure. I have hands-on experience designing multi-language compilers, relational schemas, and microservice APIs with 99.9% uptime.\"\n\n2. Work Ethic & Fast Learning (You will deliver consistently):\n\"Beyond my current tech stack, I take pride in learning rapidly. When our project required transitioning to modern Java Virtual Threads and WebSocket messaging, I mastered the APIs in two weeks and deployed the production solution without service interruption.\"\n\n3. Cultural Fit & Collaborative Energy (You make the team better):\n\"I thrive in collaborative agile environments, whether pair-programming on complex debugging sessions or mentoring junior developers. I'm excited about your mission and eager to bring positive energy and ownership from Day 1.\"",
            List.of("Match your skills directly to their job requirements", "Highlight fast learning curve and adaptability", "Demonstrate collaborative ownership and problem-solving", "Express genuine enthusiasm for their product"),
            "Show that hiring you solves an immediate business problem for the team.",
            List.of("Demonstrate how your skills solve their specific technical needs, backed by fast adaptability and collaborative culture fit", "Explain that you need a job to pay your rent", "State that you are smarter than other candidates", "Argue that they have already spent 45 minutes interviewing you"), 0
        ));

        // --- 4. Why Do You Want to Join This Company? ---
        questionBank.add(new InterviewQuestion(
            "hr-company-1", "hr", "why-company", "Medium",
            "Why Do You Want to Work at Our Company?",
            "How do you demonstrate genuine company research and alignment rather than generic praise?",
            null,
            "Avoid generic answers like 'You are a market leader' or 'Your brand is famous'. Follow this 3-step blueprint:\n\n1. Reference Specific Products or Engineering Feats:\n\"I've been following your recent launch of the real-time collaborative workspace. The way your engineering team solved operational transformation and reduced peer-to-peer sync latency to under 50ms is genuinely impressive.\"\n\n2. Align with Company Engineering Culture & Scale:\n\"I value engineering environments that prioritize clean code, test-driven development, and autonomy. Your tech blogs on observability and distributed reliability mirror the architectural principles I strive to practice daily.\"\n\n3. Connect to Your Personal Career Trajectory:\n\"I want to contribute to systems where performance bottlenecks demand deep algorithmic thinking. Working on your core platforms provides the scale and engineering challenges where I can create substantial value while growing as an engineer.\"",
            List.of("Mention specific products, tech stack, or engineering blog posts", "Align your personal values with their engineering culture", "Explain how working here fits your long-term engineering ambitions", "Avoid generic statements applicable to any company"),
            "Show that you did your homework: cite their tech stack, public engineering posts, open-source projects, or recent news.",
            List.of("Cite specific products, engineering blog posts, architectural challenges, and cultural values that align with your career goals", "Mention that the company office is located close to your house", "State that you applied to 50 companies and this was the first to respond", "Compliment the recruiter's polite email style"), 0
        ));

        // --- 5. Where Do You See Yourself in 5 Years? ---
        questionBank.add(new InterviewQuestion(
            "hr-fiveyears-1", "hr", "five-years", "Easy",
            "Where Do You See Yourself in 5 Years?",
            "How do you show ambition and commitment without sounding unrealistic or planning an early exit to start your own company?",
            null,
            "Show realistic progression, commitment, and technical mastery:\n\n\"In five years, I see myself as a senior technical leader or staff engineer who has developed deep domain expertise within your ecosystem.\n\n- In the first 1-2 years: My goal is to master your codebase, deliver reliable features, and become a go-to engineer for core architectural components.\n- In years 3-5: I want to take on end-to-end technical ownership of complex distributed systems, mentor junior engineers, and help drive architectural roadmap decisions.\n\nUltimately, I want to be recognized as someone who not only writes clean, scalable code, but elevates the engineering standards and velocity of the entire team.\"",
            List.of("Balance technical growth with leadership and mentorship", "Show commitment to growing within the company", "Break down the timeline (Years 1-2 vs Years 3-5)", "Avoid mentioning non-related ventures or sudden management pivots"),
            "Interviewers want to see that you are ambitious yet grounded, and that hiring you is a sound long-term investment.",
            List.of("Outline a realistic progression from mastering the codebase to leading architecture and mentoring engineers within the company", "Say you plan to start a competing startup in two years", "Say you want to take the interviewer's job", "State that you have no idea what you will do next week"), 0
        ));

        // --- 6. Teamwork and Communication Questions ---
        questionBank.add(new InterviewQuestion(
            "hr-team-1", "hr", "teamwork-communication", "Hard",
            "Handling Team Conflict & Disagreements (STAR Technique)",
            "Tell me about a time you had a technical disagreement with a teammate or lead. How did you handle it?",
            null,
            "Use the STAR Technique:\n\n1. Situation:\n\"During our sprint planning for a new code execution service, my colleague advocated for a synchronized in-memory hash map to cache user submissions, whereas I believed we should use a distributed Redis cache.\"\n\n2. Task:\n\"We had a tight 2-week deadline, and escalating into an argument would stall the sprint. My goal was to objectively evaluate both approaches without personal friction.\"\n\n3. Action:\n\"Instead of debating opinions, I proposed a 30-minute time-boxed benchmark. I wrote a quick load test simulating 500 concurrent connections across multiple app server instances. The results demonstrated that while the in-memory map was fast on a single node, multi-instance horizontal scaling resulted in cache desynchronization. We reviewed the telemetry together and agreed that Redis was necessary for our cluster deployment.\"\n\n4. Result:\n\"We deployed Redis on schedule, achieved 99.98% cache consistency, and our respectful data-driven discussion actually strengthened our working relationship. We documented the decision in an Architecture Decision Record (ADR).\"",
            List.of("STAR framework: Situation, Task, Action, Result", "Focus on data-driven, objective decisions rather than personal ego", "Show empathy, active listening, and professionalism", "Conclude with positive business results and team cohesion"),
            "Never badmouth coworkers. Show that you prioritize team success and data-driven solutions over being 'right'.",
            List.of("Use the STAR framework demonstrating data-driven resolution, mutual respect, and positive team outcomes", "Explain how you proved your teammate completely wrong in front of management", "Say you always agree with everything to avoid conflict", "Refuse to talk to teammates who disagree with you"), 0
        ));

        // ==========================================
        // 3. COGNITIVE ASSESSMENT
        // ==========================================
        questionBank.add(new InterviewQuestion(
            "cog-1", "cognitive", "quant-aptitude", "Medium",
            "Speed, Distance, and Relative Velocity",
            "Two trains traveling in opposite directions at 60 km/h and 90 km/h pass each other. If the length of the trains are 150m and 100m, how many seconds does it take for them to completely clear each other?",
            null,
            "Step-by-step cognitive deduction:\n1. Total distance to be covered when two trains completely clear each other:\n   Distance = Length of Train 1 + Length of Train 2 = 150m + 100m = 250 meters.\n\n2. Relative Speed:\n   Since they travel in opposite directions, relative speed is the sum of their individual speeds:\n   Relative Speed = 60 + 90 = 150 km/h.\n\n3. Convert km/h to m/s:\n   Speed (m/s) = 150 * (5 / 18) = 750 / 18 = 41.67 m/s (or 125 / 3 m/s).\n\n4. Calculate Time:\n   Time = Distance / Speed = 250 / (125 / 3) = 250 * (3 / 125) = 2 * 3 = 6 seconds.\n\nConclusion: The two trains completely clear each other in exactly 6 seconds.",
            List.of("Summing lengths for total passing distance (250m)", "Adding velocities for opposite directions (150 km/h)", "Converting km/h to m/s using (5/18) factor", "Time = Distance / Relative Speed = 6s"),
            "Show systematic conversion steps and mental arithmetic clarity.",
            List.of("6 seconds", "10 seconds", "4.5 seconds", "8 seconds"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "cog-2", "cognitive", "logical-reasoning", "Hard",
            "Logical Deduction: The Five Engineers & Seating Puzzle",
            "Five software engineers (Alice, Bob, Charlie, David, and Elena) are sitting in a row. Bob is not at either end. Charlie is immediately to the right of Alice. Elena is sitting next to David, but Elena is not next to Charlie. If Alice is at the far left, who is sitting in the middle (3rd position)?",
            null,
            "Step-by-step logical deduction:\n1. We have 5 positions: [1, 2, 3, 4, 5].\n2. 'Alice is at the far left':\n   Position 1 = Alice.\n   Current state: [Alice, _, _, _, _].\n3. 'Charlie is immediately to the right of Alice':\n   Position 2 = Charlie.\n   Current state: [Alice, Charlie, _, _, _].\n4. Remaining engineers: Bob, David, Elena for positions [3, 4, 5].\n5. 'Elena is sitting next to David, but Elena is not next to Charlie':\n   Since Charlie is at position 2, Elena cannot be at position 3.\n   Therefore, Elena must be at position 4 or 5.\n   Since Elena and David must sit next to each other, they occupy positions 4 and 5.\n6. This leaves position 3 for Bob.\n   Check condition: 'Bob is not at either end'. Position 3 is the middle, which satisfies this condition perfectly!\n7. Final configuration: [Alice, Charlie, Bob, David/Elena, Elena/David].\n\nTherefore, the engineer in the middle (3rd position) is Bob.",
            List.of("Pinning known constraints (Alice at 1, Charlie at 2)", "Deducing adjacent pair constraints for David and Elena", "Confirming negative conditions (Elena not next to Charlie, Bob not on ends)", "Deducing Bob must occupy position 3"),
            "Demonstrate methodical constraint satisfaction without guesswork.",
            List.of("Bob", "Charlie", "David", "Elena"), 0
        ));

        // ==========================================
        // 4. COMMUNICATION ASSESSMENT
        // ==========================================
        questionBank.add(new InterviewQuestion(
            "comm-1", "communication", "verbal-comm", "Easy",
            "Professional Email Communication During Production Incidents",
            "A critical bug in your code caused a 15-minute checkout outage for your client. How should you structure your professional incident notification email?",
            null,
            "Model Communication Framework:\n\nSubject: [RESOLVED] Root Cause & Resolution - Checkout Service Interruption (14:15 - 14:30 UTC)\n\nDear Leadership / Client Team,\n\nWe would like to inform you that the issue affecting the checkout service between 14:15 and 14:30 UTC today has been fully resolved, and normal transaction processing has resumed.\n\nSummary of Incident:\n- Impact: Users attempting checkout experienced timeout errors during a 15-minute window.\n- Root Cause: A connection pool exhaustion occurred following an unexpected surge in database queries during our automated reconciliation job.\n- Immediate Action Taken: At 14:22 UTC, the engineering team expanded the pool capacity and restarted the affected gateway pods, restoring service at 14:30 UTC.\n\nPreventative Measures:\n1. We have decoupled batch reconciliation jobs from the live transactional database pool.\n2. We added proactive latency alerts at the 70% connection threshold.\n\nWe sincerely apologize for the inconvenience this caused and remain committed to 99.99% service availability. A full post-mortem document will be shared by tomorrow morning.\n\nBest regards,\nEngineering Team",
            List.of("Immediate reassurance that the incident is resolved", "Clear, concise timeline of impact and root cause", "Transparent accountability without defensive excuses", "Concrete preventative actions to restore client trust"),
            "Look for executive clarity, calm tone, transparent accountability, and emphasis on future prevention.",
            List.of("Acknowledge impact, state root cause clearly, explain immediate fix, and outline preventative steps with professional composure", "Blame the database vendor for failing", "Send a one-line email saying 'fixed now'", "Avoid emailing until someone notices and complains"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "comm-2", "communication", "workplace-comm", "Medium",
            "Communicating Project Delays to Stakeholders",
            "You realize your 3-week feature sprint will be delayed by 4 days due to third-party API integration issues. When and how do you communicate this to your Product Manager?",
            null,
            "Effective Stakeholder Communication Protocol:\n\n1. Early Communication (Never wait until deadline day):\nAlert the Product Manager as soon as the risk becomes clear, ideally 5-7 days before the promised delivery date.\n\n2. Framework (Problem, Impact, Options, Recommendation):\n\"Hi Sarah, I want to give you an early heads-up on the Payment Integration sprint. During our sandbox integration testing with the vendor's API, we discovered that their webhook delivery experiences unannounced 30-second delays, which breaks our instant order confirmation flow.\n\nImpact:\nIf we proceed with the current schedule, we risk releasing an unstable checkout experience to users.\n\nProposed Options:\n- Option A: Delay the full release by 4 business days to implement a reliable polling fallback, ensuring zero failed checkouts.\n- Option B: Launch on schedule with credit card payments only, deferring the new vendor integration to Sprint 2.\n\nRecommendation:\nI recommend Option A because launch quality directly impacts user trust, and 4 days gives us time for automated regression testing.\n\nLet me know your thoughts or if we can jump on a brief 10-minute sync.\"",
            List.of("Communicate early — never surprise stakeholders on launch day", "State the technical obstacle in clear business terms", "Provide multiple viable options with trade-offs", "Give a well-reasoned recommendation"),
            "Stakeholders value predictability and solutions over surprises. Demonstrating ownership builds executive trust.",
            List.of("Communicate proactively in advance, explain business impact, and provide clear options with a solid recommendation", "Wait until the launch date and announce the delay in the retrospective", "Cut all testing to ship on time regardless of crashes", "Work 24-hour shifts secretly without informing the manager"), 0
        ));

        // ==========================================
        // 5. CODING INTERVIEW QUESTIONS (2 Challenges)
        // ==========================================
        questionBank.add(new InterviewQuestion(
            "code-trap-rain", "coding", "trapping-rain-water", "Hard",
            "Interview Coding: Trapping Rain Water (Two Pointers O(n))",
            "Given `n` non-negative integers representing an elevation map where the width of each bar is `1`, compute how much water it can trap after raining.\n\nPractice in Code Lab: [Trapping Rain Water](problem://trapping-rain-water)",
            "public int trap(int[] height) {\n    int left = 0, right = height.length - 1;\n    int leftMax = 0, rightMax = 0, water = 0;\n    while (left < right) {\n        if (height[left] < height[right]) {\n            if (height[left] >= leftMax) leftMax = height[left];\n            else water += leftMax - height[left];\n            left++;\n        } else {\n            if (height[right] >= rightMax) rightMax = height[right];\n            else water += rightMax - height[right];\n            right--;\n        }\n    }\n    return water;\n}",
            "Technical Walkthrough:\n- Optimal Approach: Two Pointers\n- Time Complexity: O(n) single pass\n- Space Complexity: O(1) auxiliary space\n\nLogic:\nWater trapped at any index `i` is determined by `min(maxLeft, maxRight) - height[i]`.\nBy placing two pointers at `0` and `n - 1`, if `height[left] < height[right]`, we are guaranteed that the bottleneck is determined by `leftMax`. Thus, if `height[left] < leftMax`, it traps `leftMax - height[left]` units of water. When `height[left] >= leftMax`, we simply update `leftMax`. The same symmetric logic applies to the right side.\n\nYou can code, run, and grade this problem live in the Practice Code Lab!",
            List.of("Two Pointers technique O(n) time and O(1) space", "Bottleneck principle: min(maxLeft, maxRight)", "Avoiding O(n) space dynamic programming array", "Handling edge cases (n < 3 traps 0 water)"),
            "Interviewers often ask candidates to optimize from O(n) space DP to O(1) space two pointers.",
            List.of("O(n) time, O(1) auxiliary space using two pointers", "O(n^2) brute force nested loops", "O(n log n) divide and conquer", "O(2^n) recursion"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "code-sliding-window", "coding", "sliding-window-max", "Hard",
            "Interview Coding: Sliding Window Maximum (Monotonic Deque O(n))",
            "Given an integer array `nums` and a sliding window of size `k` moving from left to right, return the max sliding window.\n\nPractice in Code Lab: [Sliding Window Maximum](problem://sliding-window-max)",
            "public int[] maxSlidingWindow(int[] nums, int k) {\n    int n = nums.length;\n    int[] res = new int[n - k + 1];\n    Deque<Integer> dq = new ArrayDeque<>(); // stores indices\n    for (int i = 0; i < n; i++) {\n        while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) dq.pollFirst();\n        while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();\n        dq.offerLast(i);\n        if (i >= k - 1) res[i - k + 1] = nums[dq.peekFirst()];\n    }\n    return res;\n}",
            "Technical Walkthrough:\n- Optimal Approach: Monotonic Decreasing Deque\n- Time Complexity: O(n) because each index is added and removed from the deque at most once.\n- Space Complexity: O(k) for the deque window.\n\nLogic:\n1. Maintain a double-ended queue storing array indices in monotonically decreasing order of their values.\n2. Before adding index `i`, remove indices from the front that have fallen out of the current window (`dq.peek() < i - k + 1`).\n3. Remove smaller elements from the back because they can never be the maximum in any subsequent window that includes `nums[i]`.\n4. Add current index `i` to the back.\n5. When `i >= k - 1`, the front of the deque is guaranteed to be the maximum element in the current window.\n\nYou can code, run, and grade this problem live in the Practice Code Lab!",
            List.of("Monotonic decreasing deque preserving candidate maximums", "O(n) amortized linear time complexity", "Indices stored rather than values to track window expiration", "O(k) auxiliary memory"),
            "Interviewers look for candidates who explain why smaller previous elements can be discarded immediately.",
            List.of("O(n) amortized time using a monotonic deque", "O(n * k) scanning each window", "O(n log k) using a max heap with lazy removal", "O(n^2) bubble scan"), 0
        ));
    }

    public static List<InterviewQuestion> getQuestions(String category, String subject, String level, int limit) {
        return questionBank.stream()
            .filter(q -> {
                if (category != null && !category.equalsIgnoreCase("all") && !q.category.equalsIgnoreCase(category)) {
                    return false;
                }
                if (subject != null && !subject.equalsIgnoreCase("all") && !q.subject.equalsIgnoreCase(subject)) {
                    return false;
                }
                if (level != null && !level.equalsIgnoreCase("all") && !q.level.equalsIgnoreCase(level)) {
                    return false;
                }
                return true;
            })
            .limit(limit > 0 ? limit : 30)
            .collect(Collectors.toList());
    }

    public static List<InterviewQuestion> getAllQuestions() {
        return Collections.unmodifiableList(questionBank);
    }

    public static Map<String, Object> getMetadata() {
        Map<String, Object> meta = new LinkedHashMap<>();
        
        // Categories and subjects
        Map<String, List<Map<String, String>>> catSubjects = new LinkedHashMap<>();
        
        catSubjects.put("technical", List.of(
            Map.of("id", "all", "name", "🌐 All Technical Subjects"),
            Map.of("id", "java", "name", "☕ Java"),
            Map.of("id", "python", "name", "🐍 Python"),
            Map.of("id", "c", "name", "⚙️ C"),
            Map.of("id", "cpp", "name", "⚡ C++"),
            Map.of("id", "sql", "name", "🗄️ SQL & Databases"),
            Map.of("id", "dsa", "name", "🌳 Data Structures & Algorithms"),
            Map.of("id", "dbms", "name", "📊 Database Management Systems (DBMS)"),
            Map.of("id", "oop", "name", "🧩 Object-Oriented Programming (OOP)"),
            Map.of("id", "networks", "name", "🌐 Computer Networks")
        ));

        catSubjects.put("hr", List.of(
            Map.of("id", "all", "name", "👔 All HR & Behavioral"),
            Map.of("id", "about-me", "name", "👋 Tell Me About Yourself"),
            Map.of("id", "strengths-weaknesses", "name", "💪 Strengths & Weaknesses"),
            Map.of("id", "why-hire-you", "name", "🎯 Why Should We Hire You?"),
            Map.of("id", "why-company", "name", "🏢 Why Join This Company?"),
            Map.of("id", "five-years", "name", "🚀 Where Do You See Yourself in 5 Years?"),
            Map.of("id", "teamwork-communication", "name", "🤝 Teamwork & Conflict Resolution")
        ));

        catSubjects.put("cognitive", List.of(
            Map.of("id", "all", "name", "🧠 All Cognitive Assessments"),
            Map.of("id", "quant-aptitude", "name", "📐 Quantitative Aptitude & Speed"),
            Map.of("id", "logical-reasoning", "name", "🔍 Logical Reasoning & Puzzles")
        ));

        catSubjects.put("communication", List.of(
            Map.of("id", "all", "name", "💬 All Communication Assessments"),
            Map.of("id", "verbal-comm", "name", "✉️ Professional & Executive Writing"),
            Map.of("id", "workplace-comm", "name", "👥 Stakeholder & Workplace Communication")
        ));

        catSubjects.put("coding", List.of(
            Map.of("id", "all", "name", "💻 All Coding Challenges"),
            Map.of("id", "trapping-rain-water", "name", "🌧️ Trapping Rain Water (Hard)"),
            Map.of("id", "sliding-window-max", "name", "🪟 Sliding Window Maximum (Hard)")
        ));

        meta.put("categories", catSubjects);
        meta.put("totalQuestions", questionBank.size());
        return meta;
    }
}
