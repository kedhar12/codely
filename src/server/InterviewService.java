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
        // 2. HR & BEHAVIORAL QUESTIONS (5 Questions per topic, STAR Method)
        // ==========================================

        // --- 1. Tell Me About Yourself (5 Questions) ---
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

        questionBank.add(new InterviewQuestion(
            "hr-about-2", "hr", "about-me", "Easy",
            "Walk Me Through Your Resume & Technical Evolution",
            "Walk me through the milestones of your background and the key decisions that shaped your engineering trajectory.",
            null,
            "Walkthrough Blueprint:\n\n1. The Foundation: \"I started in computer science fascinated by algorithmic problem-solving and software architecture. Early on, I focused on core fundamentals—data structures, memory management in C/C++, and object-oriented design in Java.\"\n\n2. The Shift to Real Systems: \"As I built larger applications, I realized that writing working code is only half the battle; the real challenge is resilience, concurrency, and user experience. That led me to architect end-to-end full-stack platforms, integrating secure authentication and automated grading engines.\"\n\n3. What Drives Me Today: \"Today, I specialize in building performant developer tools and distributed services. I look for roles where code quality, automated testing, and thoughtful system design are celebrated.\"",
            List.of("Logical narrative arc linking past choices to current skills", "Focus on technical evolution rather than job titles alone", "Demonstrate passion for engineering craft and scalable design"),
            "Interviewers want to see that your career choices were deliberate, not accidental.",
            List.of("Present a coherent narrative showing intentional growth and passion for solving complex engineering challenges", "List every company and project in reverse chronological order without context", "Say you only chose software engineering because of high salaries", "Criticize former employers for giving you boring tasks"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-about-3", "hr", "about-me", "Hard",
            "Describe a Complex Technical Project You Are Most Proud Of",
            "Of all the systems or applications you've architected, which project are you most proud of and what was your specific contribution?",
            null,
            "STAR Framework Response:\n\n- Situation: \"I led the development of Codely, an interactive multi-language coding and automated assessment platform designed to handle live concurrent code executions across 6 programming languages.\"\n- Task: \"Our primary architectural bottleneck was code isolation and execution latency: running untrusted user submissions concurrently without crashing the host server or exhausting thread pools.\"\n- Action: \"I leveraged Java 21 Virtual Threads (Project Loom) to handle incoming requests asynchronously without thread pinning. I designed a sandbox execution engine with strict process timeouts (2500ms) and built a normalized 9-table MySQL relational database schema with indexed progress tracking.\"\n- Result: \"The platform achieved sub-200ms submission grading, scaled to 500+ concurrent simulated users with zero crashes, and passed 100% automated integration test suites.\"",
            List.of("Clear technical problem statement and personal ownership", "Specific architectural choices explained (Virtual Threads, process sandboxing)", "Quantifiable outcomes (latency, concurrency, test pass rate)"),
            "Take personal credit where due ('I designed', 'I implemented'), but acknowledge team dependencies and tools honestly.",
            List.of("Use the STAR framework highlighting specific technical obstacles, your architectural decisions, and measurable performance results", "Describe a generic school project you barely touched", "Say the project succeeded because you worked 18 hours every day", "Refuse to explain technical details because it's proprietary"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-about-4", "hr", "about-me", "Easy",
            "Continuous Learning: How Do You Keep Up with Fast-Moving Tech?",
            "With frameworks, languages, and AI tooling advancing rapidly, what is your systematic continuous learning routine?",
            null,
            "Continuous Learning Routine:\n\n1. Official Sources & RFCs: \"Rather than relying solely on tutorial videos, I prioritize reading official language specifications, release notes (like Java JEPs or Python PEPs), and RFC proposals to understand the 'why' behind new features.\"\n2. Hands-on Proofs of Concept: \"When a new paradigm emerges—such as Java Virtual Threads or Node.js native test runners—I build a small, isolated prototype benchmark to measure real-world performance differences firsthand.\"\n3. Tech Community & Engineering Blogs: \"I regularly read engineering blogs from high-scale tech organizations (like Uber, Netflix, and Discord) to learn how production distributed systems handle real scale and outages.\"",
            List.of("Systematic habits over sporadic reading", "Hands-on building rather than passive reading", "Following official specs and industry architecture blogs"),
            "Show curiosity and intellectual rigor. Interviewers love candidates who build prototypes to test claims.",
            List.of("Combine official language specifications, hands-on proof-of-concept benchmarks, and production engineering blogs", "Say you only learn when your manager forces you to take a course", "Rely entirely on 30-second TikTok coding videos", "Claim you already know everything so you never need to learn anything new"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-about-5", "hr", "about-me", "Medium",
            "What Core Values Guide Your Daily Engineering Work?",
            "Beyond writing code that compiles, what engineering values guide how you design, test, and collaborate?",
            null,
            "Core Engineering Values:\n\n1. Empathy for the Next Engineer: \"I write clean, readable code and maintain clear documentation because code is read ten times more often than it is written. My goal is that any teammate touching my code six months from now understands the design rationale immediately.\"\n2. Automated Verification Over Hope: \"I never assume code works without automated tests. Test suites are not an afterthought; they are living documentation that enables teams to refactor with confidence.\"\n3. Pragmatic Simplicity: \"I follow the KISS and YAGNI principles. I resist the temptation to over-engineer complex abstractions until real user requirements and data justify them.\"",
            List.of("Maintainability and code empathy", "Test automation and reliability", "Pragmatism over premature optimization"),
            "Demonstrates maturity: junior developers chase cleverness; senior developers value clarity and reliability.",
            List.of("Empathy for teammates through clean code, rigorous automated testing, and pragmatic simplicity over premature cleverness", "Writing the most compressed, unreadable one-liners to show intelligence", "Shipping directly to production on Friday without testing", "Refusing to document code to preserve job security"), 0
        ));

        // --- 2. Strengths and Weaknesses (5 Questions) ---
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

        questionBank.add(new InterviewQuestion(
            "hr-strength-2", "hr", "strengths-weaknesses", "Hard",
            "Tell Me About a Time You Failed or Made a Serious Mistake",
            "Describe a real situation where a code deployment or design decision went wrong. How did you remediate the issue and prevent future occurrences?",
            null,
            "STAR Remediation Formula:\n\n- Situation: \"During an early deployment of a student submission scoring pipeline, I updated a database constraint without accounting for concurrent transactions during peak quiz submissions.\"\n- Task: \"Within 10 minutes of release, several submission writes failed with deadlock exceptions, locking out roughly 40 active students.\"\n- Action: \"I immediately rolled back to the previous stable release, mitigating user impact within 4 minutes. Once stable, I initiated a blameless post-mortem. I reproduced the deadlock in our staging environment using simulated concurrent loads, adjusted transaction isolation from Serializable to Repeatable Read with row-level locks, and added a regression test simulating 200 concurrent writes.\"\n- Result: \"We re-deployed the fix with zero downtime, and our new automated concurrency test suite prevented similar lock contention issues in all subsequent releases.\"",
            List.of("Take immediate accountability without blaming others", "Rapid mitigation (rollback/hotfix) followed by root-cause analysis", "Instituting automated safeguards so the mistake cannot happen again"),
            "Blameless accountability and systemic prevention are what separate mature engineers from novices.",
            List.of("Own the mistake openly, describe the immediate rollback, conduct a blameless post-mortem, and implement automated regression tests", "Blame the QA team for failing to catch your bug", "Hide the error logs and hope no users noticed", "Quit the project immediately to avoid embarrassment"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-strength-3", "hr", "strengths-weaknesses", "Medium",
            "Handling Critical Feedback in Code Reviews",
            "How do you respond when a colleague or senior architect leaves critical comments on your pull request questioning your design?",
            null,
            "Professional Code Review Mindset:\n\n1. Separate Ego from Code: \"I view code reviews as a collaborative quality gate, not a personal critique. The goal is the health and longevity of the codebase.\"\n2. Assume Positive Intent: \"If a reviewer questions my design, my first step is to genuinely understand their perspective. I ask clarifying questions like: 'Could you elaborate on the failure mode you foresee here?'\"\n3. Data-Driven Evaluation: \"If there is a legitimate trade-off—such as memory consumption versus readability—I write a quick benchmark or reference documentation rather than arguing personal aesthetic preferences.\"\n4. Gratitude & Growth: \"Once aligned, I thank the reviewer for the catch and document the design decision in the code comments or PR description for future reference.\"",
            List.of("Separate personal identity from pull requests", "Curious, non-defensive communication", "Using data and benchmarks to resolve technical trade-offs"),
            "Interviewers look for emotional maturity and coachability. Defensive developers slow down team velocity.",
            List.of("Assume positive intent, ask clarifying questions, evaluate trade-offs with data, and embrace reviews as collaborative learning", "Ignore the review comments and merge the code directly to main", "Argue angrily with the reviewer on Slack", "Delete the pull request in frustration"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-strength-4", "hr", "strengths-weaknesses", "Easy",
            "Learning an Unfamiliar Technology Under Extreme Time Pressure",
            "Tell me about a time you had to deliver a feature using a programming language, library, or tool you had never touched before.",
            null,
            "STAR Fast-Learning Framework:\n\n- Situation: \"Our project required implementing real-time browser code editing, but none of our team had worked with the Monaco Editor internals or its complex WebWorker architecture.\"\n- Task: \"I had 5 days to integrate the Monaco Editor with syntax highlighting, custom theme support, and a fallback mechanism for slow network connections.\"\n- Action: \"Instead of feeling overwhelmed, I broke the challenge down: Day 1: read official Microsoft Monaco documentation and cloned minimal samples. Day 2: isolated CDN script loading and implemented a 2.5-second watchdog timer to fall back to native textareas if CDN was blocked. Day 3-4: wired language switching and tab indentation. Day 5: cross-browser testing.\"\n- Result: \"Delivered the editor integration on schedule. It handled 6 languages smoothly and seamlessly fell back without locking out users on restricted corporate networks.\"",
            List.of("Deconstruct complex technologies into structured milestones", "Proactive defensive engineering (e.g. fallback watchdogs)", "Delivering reliable functionality on a tight timeline"),
            "Demonstrates resourcefulness and structured execution under pressure.",
            List.of("Deconstruct the unknown technology into incremental milestones, study core documentation, and build a working defensive prototype", "Complain to management that the technology is too difficult", "Copy unverified code from random forums without reading it", "Delay the project by two months while trying to read every source file"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-strength-5", "hr", "strengths-weaknesses", "Hard",
            "Managing Multiple Competing Priorities & Avoiding Burnout",
            "When production bugs, sprint feature deadlines, and team inquiries all compete for your attention simultaneously, how do you manage your time?",
            null,
            "Prioritization & Triage Strategy:\n\n1. Severity Triage: \"I classify tasks using an impact vs urgency matrix. A production outage or security vulnerability affecting active users always takes immediate precedence over non-blocking sprint tasks.\"\n2. Transparent Stakeholder Communication: \"If resolving a critical incident will delay a promised sprint feature, I communicate with my Product Manager immediately. I explain the trade-offs and help adjust expectations rather than quietly burning out.\"\n3. Time-Boxing Deep Work: \"I block 2-3 hour uninterrupted focus windows on my calendar for complex algorithmic work and batch communication (emails, Slack messages, PR reviews) into dedicated windows.\"\n4. Sustainable Pace: \"I recognize that sustained engineering excellence requires clarity and focus. Working 16-hour days produces bug-ridden code that costs double to fix later.\"",
            List.of("Severity triage based on user impact", "Proactive communication with product managers", "Time-boxing deep work vs communication", "Commitment to sustainable engineering practices"),
            "Shows executive maturity. Great engineers know how to say 'no' constructively to protect quality.",
            List.of("Triage by severity and user impact, communicate schedule trade-offs transparently, and protect focused deep work windows", "Attempt to do all tasks simultaneously resulting in half-baked code", "Work all night secretly until burning out", "Ignore production bugs to finish the fun feature first"), 0
        ));

        // --- 3. Why Should We Hire You? (5 Questions) ---
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

        questionBank.add(new InterviewQuestion(
            "hr-hire-2", "hr", "why-hire-you", "Easy",
            "What Is Your Core Differentiator as an Engineer?",
            "What specific quality or approach sets you apart from other qualified software engineers with similar resumes?",
            null,
            "The Core Differentiator (End-to-End Product Ownership):\n\n\"What sets me apart is my commitment to end-to-end product ownership. Many engineers stop caring once code compiles and passes local tests. For me, a feature is only complete when it is:\n1. Resilient under peak load in production.\n2. Monitored with clear telemetry and error metrics.\n3. Genuinely intuitive and delightfully responsive for the end user.\n\nBecause I think like both a systems engineer and a product user, I anticipate edge cases early in the design cycle, saving the team weeks of downstream refactoring and customer support tickets.\"",
            List.of("End-to-end ownership mindset", "Bridging systems engineering with product user empathy", "Preventing bugs before code is written through edge-case anticipation"),
            "Companies look for engineers who care about business outcomes, not just tickets closed.",
            List.of("Commitment to end-to-end product ownership, combining systems resilience with user empathy to solve business problems", "Ability to write code without ever compiling or testing it", "Willingness to take all credit for team accomplishments", "Claiming to type faster than any other engineer"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-hire-3", "hr", "why-hire-you", "Hard",
            "Balancing Engineering Velocity with Technical Debt",
            "How do you decide between shipping a quick workaround to meet an urgent business deadline vs taking the time to build a robust architecture?",
            null,
            "Pragmatic Technical Debt Framework:\n\n1. Intentionality: \"Technical debt is like financial debt: taking on a small amount intentionally to capture a time-sensitive business opportunity is smart, as long as you plan for the interest payments.\"\n2. Identifying the Red Line: \"I will never compromise on security, data integrity, or core transactional reliability for the sake of speed. However, I am willing to simplify non-critical UI features or hardcode configuration for a fast MVP.\"\n3. Documentation & Debt Repayment: \"Whenever we take on a tactical shortcut, I ensure two things: First, we add a `// TODO(tech-debt)` note with context and ticket number. Second, we file a backlog ticket in Jira so the debt is scheduled for refactoring in the next stabilization sprint.\"",
            List.of("Technical debt as a conscious financial trade-off", "Zero compromise on security and data integrity", "Formal tracking and scheduling of debt repayment"),
            "Shows that you are a business-minded engineer who balances speed with architectural responsibility.",
            List.of("Treat technical debt as an intentional, tracked trade-off—never compromising data integrity and scheduling prompt repayment", "Always refuse to ship unless the architecture is 100% theoretically flawless", "Never worry about clean code because shipping fast is all that matters", "Rewrite the entire codebase from scratch every three months"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-hire-4", "hr", "why-hire-you", "Medium",
            "Dealing with Ambiguous Requirements and Incomplete Specs",
            "If your product manager hands you a vague feature request like 'improve search performance', how do you proceed?",
            null,
            "Ambiguity Resolution Protocol:\n\n1. Quantify the Problem: \"First, I clarify the baseline. What is the current search latency? Is it 95th percentile or median? Is the bottleneck network round-trip, database querying, or frontend rendering?\"\n2. Define Success Metrics: \"I partner with the Product Manager to establish clear acceptance criteria: e.g. 'Reduce p95 search latency from 1.2s to under 300ms for 100,000 records.'\"\n3. User Journey & Edge Cases: \"I map out user scenarios: What happens on empty queries? Special characters? Network timeouts?\"\n4. Propose Options with Trade-Offs: \"I draft a 1-page design doc with two approaches (e.g. database B-Tree index optimization vs introducing in-memory Redis caching) and present recommendations.\"",
            List.of("Transforming vague goals into measurable SLA metrics", "Root-cause bottleneck identification before writing code", "Writing concise design docs with options and trade-offs"),
            "Proves autonomy: you don't require hand-holding; you drive clarity from chaos.",
            List.of("Quantify current metrics, define clear acceptance SLAs, map edge cases, and present structured architectural trade-offs", "Start coding randomly without asking any questions", "Complain that product managers never write good specifications", "Wait silently for weeks until someone gives you more instructions"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-hire-5", "hr", "why-hire-you", "Hard",
            "Your 30-60-90 Day Impact Plan",
            "If hired for this engineering position today, what does your roadmap for success look like over your first 3 months?",
            null,
            "Structured 30-60-90 Day Blueprint:\n\n- Days 1-30 (Learn & Integrate): \"Master the codebase, development workflows, CI/CD pipelines, and coding standards. Deliver my first production bug fix or small feature within Week 2. Build strong relationships with teammates and understand the business domain.\"\n- Days 31-60 (Deliver & Collaborate): \"Take on medium-to-large feature tickets independently. Actively participate in PR reviews, providing thoughtful feedback. Identify small friction points in local dev setup and propose lightweight documentation improvements.\"\n- Days 61-90 (Own & Elevate): \"Lead the end-to-end design and delivery of an impactful system component. Propose optimizations to automated test suites or latency bottlenecks. Begin mentoring newer team members and contributing to sprint planning.\"",
            List.of("Structured progression: Learn -> Deliver -> Own", "Early win in the first 2 weeks", "Balancing technical contribution with team collaboration"),
            "Hiring managers love this because it proves you hit the ground running with zero onboarding friction.",
            List.of("Progress systematically: Days 1-30 learning and shipping early fixes; Days 31-60 delivering independent features; Days 61-90 driving system ownership", "Spend the first 90 days silently observing without touching any code", "Demand that the team rewrite their codebase using your preferred framework on Day 1", "Focus exclusively on office perks and vacation scheduling"), 0
        ));

        // --- 4. Why Do You Want to Join This Company? (5 Questions) ---
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

        questionBank.add(new InterviewQuestion(
            "hr-company-2", "hr", "why-company", "Easy",
            "What Impresses You Most About Our Engineering & Product Architecture?",
            "What specific features, user experiences, or engineering aspects of our platform caught your attention?",
            null,
            "Product & Architecture Analysis:\n\n\"What impresses me most is how seamlessly your platform balances high performance with user simplicity. Specifically:\n1. Low-Latency Execution: Managing high-throughput interactive workloads with sub-second feedback demonstrates that your backend concurrency and caching layers are exceptionally well-architected.\n2. Developer Experience: The clean design of your APIs and developer dashboard shows that your engineering culture respects the end-user's time.\n3. Resilience: Delivering reliable services with high availability despite fluctuating traffic bursts proves that your team takes observability and automated testing seriously.\n\nI want to work with engineers who set such high technical standards.\"",
            List.of("Highlight concrete technical attributes (latency, API design, resilience)", "Demonstrate genuine familiarity with the product", "Compliment the engineering team's high technical bar"),
            "Interviewers want to see that you tested their product and appreciate the engineering beneath the hood.",
            List.of("Analyze specific technical strengths like low latency, intuitive API design, and resilient architecture under scale", "Admit that you have never tried using the company's product", "Say all software platforms look the same to you anyway", "State that you only like the company logo colors"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-company-3", "hr", "why-company", "Medium",
            "Which of Our Company Core Values Resonates Most With You?",
            "Our company champions core principles like 'Customer Obsession', 'Radical Ownership', and 'Learn & Be Curious'. Which one reflects your work style?",
            null,
            "Connecting Personal Practice to Company Values:\n\n\"The value that resonates most deeply with me is **'Radical Ownership'**.\nTo me, ownership means that when I encounter a bug, a slow query, or poorly documented code, I never say 'that's not my job'. When building our platform's grading pipeline, even though our issue tracker only specified core compiler execution, I took ownership of implementing sandbox memory limits and fallback textareas because I knew users on slow networks would otherwise suffer.\n\nOwnership means caring about the entire system from database row to user browser click, and taking proactive responsibility for its health.\"",
            List.of("Select one value and tie it directly to a personal engineering story", "Define what the value means in practical day-to-day terms", "Demonstrate impact beyond the narrow scope of ticket descriptions"),
            "Never recite their website definitions back to them; tell a personal story illustrating the value in action.",
            List.of("Choose a specific value and share a concrete engineering project where you embodied that principle in practice", "Claim that you love all 20 values equally without providing any examples", "Say core values are just corporate marketing slogans", "Ask the interviewer to remind you what their company values are"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-company-4", "hr", "why-company", "Easy",
            "The Reverse Interview: What Questions Do You Have for Us?",
            "At the end of your interview, the hiring manager asks 'Do you have any questions for me?'. What strategic questions should you ask?",
            null,
            "High-Impact Reverse Interview Questions:\n\n1. On Team Engineering Culture:\n\"How does your team handle the balance between shipping new product features and addressing architectural technical debt?\"\n2. On Deployment & Reliability:\n\"What does your current deployment pipeline look like, and how frequently does the team deploy code to production?\"\n3. On Team Success & Metrics:\n\"What would a successful first 6 months look like for the engineer stepping into this role? What would make you say 'we made the absolute right hire'?\"\n4. On Engineering Challenges:\n\"What is the single biggest architectural or scalability bottleneck the team is currently working to solve over the next two quarters?\"",
            List.of("Never say 'No, I have no questions'", "Ask about engineering trade-offs, CI/CD frequency, and onboarding metrics", "Demonstrate that you evaluate the company as much as they evaluate you"),
            "Candidates who ask insightful questions stand out immediately as thoughtful, senior-minded engineers.",
            List.of("Ask strategic questions about deployment frequency, technical debt balance, and what success looks like in the first 6 months", "Say 'Nope, you answered everything' and leave immediately", "Ask how quickly you can get a promotion and a salary raise", "Ask if the company monitors your screen when working from home"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-company-5", "hr", "why-company", "Hard",
            "Where Do You See Our Industry & Product Heading in 3 Years?",
            "How do you view upcoming technological trends (AI agents, edge computing, serverless) impacting our market, and how should we adapt?",
            null,
            "Strategic Industry Synthesis:\n\n1. Shift Toward Intelligent Developer Tooling:\n\"Over the next 3 years, developer and learning platforms will shift from static code editors to AI-augmented interactive pair programmers that analyze semantic errors in real-time.\"\n2. Low-Latency Edge Sandboxing:\n\"As WebAssembly and microVM runtimes mature, client-side and edge execution will dramatically lower hosting costs and deliver zero-latency execution to global learners.\"\n3. Why This Company is Well Positioned:\n\"Because your platform already has strong curriculum structures and a robust core execution engine, integrating intelligent evaluation and adaptive learning will cement your position as the market leader.\"",
            List.of("Demonstrate forward-looking architectural vision", "Understand business economics (edge hosting cost reduction)", "Tie industry trends directly back to the company's competitive advantage"),
            "Proves you think beyond individual pull requests and can contribute to strategic architectural roadmaps.",
            List.of("Articulate relevant industry shifts (AI pair-programming, microVM sandboxes) and explain how the company can capitalize", "Say that AI will replace all software engineers next month", "Claim that technology will never change from how it is today", "State that business strategy is only for executives to care about"), 0
        ));

        // --- 5. Where Do You See Yourself in 5 Years? (5 Questions) ---
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

        questionBank.add(new InterviewQuestion(
            "hr-fiveyears-2", "hr", "five-years", "Medium",
            "Individual Contributor (IC) Track vs Engineering Management",
            "As your career evolves, do you see yourself pursuing the Staff/Principal Engineer technical track or transitioning into Engineering Management?",
            null,
            "Dual-Track Career Perspective:\n\n\"My immediate focus over the next few years is deep technical mastery on the Individual Contributor (IC) track—designing scalable distributed systems, tackling performance bottlenecks, and writing clean, mission-critical code.\n\nHowever, I also enjoy mentoring junior developers, facilitating technical retrospectives, and helping unblock teammates. As I reach Senior and Staff levels, I see myself taking on technical leadership—guiding architecture and engineering standards.\n\nWhether I ultimately choose the Principal Engineer track or an Engineering Manager role will depend on where I can create the greatest leverage for the team, but my technical foundation will always be my core strength.\"",
            List.of("Clear preference grounded in current technical mastery", "Recognition that leadership exists on both IC and management tracks", "Focus on organizational leverage and team empowerment"),
            "Shows self-awareness. Companies value strong senior ICs just as much as managers.",
            List.of("Focus on deep technical mastery on the IC track while embracing mentorship, remaining open to whichever path offers maximum team leverage", "Say you only want to be a manager so you can stop writing code", "Claim management is useless and engineers should have no leaders", "State that you have never thought about your career path"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-fiveyears-3", "hr", "five-years", "Medium",
            "What High-Impact Technical Skills Are on Your 3-Year Roadmap?",
            "What specific technologies, paradigms, or architectural domains are currently at the top of your learning roadmap?",
            null,
            "Strategic Skill Roadmap:\n\n1. Distributed Consensus & Concurrency: \"I am deepening my knowledge of distributed consistency algorithms (Raft, Paxos) and lock-free data structures to master how large-scale databases maintain consistency across regions.\"\n2. Advanced Cloud Observability: \"I want to master OpenTelemetry and distributed tracing to diagnose microsecond latency spikes in complex microservice architectures.\"\n3. Resilient System Design: \"I am studying fault-tolerance design patterns—circuit breakers, bulkheads, and chaos engineering—to build systems that gracefully survive partial cloud outages.\"",
            List.of("Specific, foundational computer science concepts (consensus, telemetry, resilience)", "Focus on durable architectural skills rather than fleeting hype frameworks", "Direct application to enterprise-scale systems"),
            "Shows that your learning goals align with building reliable, enterprise-grade distributed systems.",
            List.of("Target durable architectural domains: distributed consensus, OpenTelemetry observability, and fault-tolerant cloud design", "Say you want to learn every new JavaScript framework released this month", "Claim you already know all technical skills you will ever need", "Say you have no learning roadmap because you prefer to wing it"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-fiveyears-4", "hr", "five-years", "Hard",
            "How Will You Define and Measure Your Own Success Over the Next 3 Years?",
            "Beyond job titles and promotions, what tangible benchmarks will signify to you that you are succeeding in your role?",
            null,
            "Multi-Dimensional Success Benchmarks:\n\n1. System Reliability & Business Impact: \"I measure success by the durability of the systems I build: Did they scale gracefully? Did they achieve 99.99% uptime? Did they reduce operational latency for our users?\"\n2. Engineering Velocity of the Team: \"Success means that teammates can deploy and build on top of my components quickly without confusion or fear of regressions.\"\n3. Mentorship & Multiplier Effect: \"I measure my value not just by how much code I write, but by how much easier I make it for others to succeed—mentoring new hires and authoring clear Architecture Decision Records (ADRs).\"\n4. Professional Autonomy: \"Becoming someone leadership trusts to lead critical technical initiatives with minimal supervision.\"",
            List.of("Tie personal success directly to system reliability and user metrics", "The 'multiplier effect': making the whole team more productive", "Earning organizational trust and autonomous ownership"),
            "Highlights that you define success through team enablement and customer impact rather than personal vanity.",
            List.of("Measure success by system reliability, team velocity multiplication, impactful mentorship, and earned architectural autonomy", "Measure success exclusively by how many lines of code you typed", "Measure success by how few meetings you attended", "Define success as never having anyone question your ideas"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-fiveyears-5", "hr", "five-years", "Hard",
            "If Given Autonomy, What Innovation Would You Spearhead Here?",
            "If our engineering organization gave you 20% hackathon time, what technical challenge or initiative would you tackle?",
            null,
            "Proactive Engineering Initiative:\n\n\"If given 20% innovation time, I would build an **Automated Test Flakiness Detector and Telemetry Dashboard**.\n\n- Problem: Flaky integration tests erode developer trust, slow down CI/CD pipelines, and cause engineers to ignore genuine failures.\n- Solution: I would implement an automated retry-analysis worker that flags non-deterministic tests, captures heap and thread dumps during failures, and alerts the owning team with exact stack traces.\n- Value: This directly protects engineering velocity, cuts CI queue times, and ensures every merged PR is truly rock solid.\"",
            List.of("Identify a universal engineering pain point (flaky tests, build times)", "Propose a concrete technical solution with measurable business value", "Demonstrate care for developer productivity and engineering happiness"),
            "Proves you are proactive and care about solving systemic organizational bottlenecks.",
            List.of("Propose an automated test flakiness analyzer and CI telemetry tool to directly protect team deployment velocity", "Suggest rebuilding the entire company website in a niche obscure language", "Say you would spend the time browsing the web", "Claim that the company has no room for improvement"), 0
        ));

        // --- 6. Teamwork and Communication Questions (5 Questions) ---
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

        questionBank.add(new InterviewQuestion(
            "hr-team-2", "hr", "teamwork-communication", "Medium",
            "Collaborating with Difficult Stakeholders or Cross-Functional Leads",
            "Describe a time you had to work with a designer, product manager, or engineer who had conflicting expectations. How did you find alignment?",
            null,
            "STAR Alignment Framework:\n\n- Situation: \"During the redesign of our assessment submission interface, our UI designer wanted rich animation transitions on every code run, while I was concerned that heavy DOM manipulations would introduce noticeable input lag on lower-end devices.\"\n- Task: \"Find a solution that satisfied the designer's desire for modern polish while upholding my commitment to sub-50ms editor responsiveness.\"\n- Action: \"I scheduled a 20-minute working session. Instead of rejecting the design outright, I built a prototype demonstrating both versions. I showed how CSS GPU-accelerated transforms (`transform: translateY` and `opacity`) could deliver sleek visual feedback with zero layout reflows or CPU spikes. We agreed to implement the lightweight GPU transitions.\"\n- Result: \"The feature shipped with rave reviews from users for its snappy responsiveness and polished aesthetics, and the designer and I established a strong collaborative partnership for future sprints.\"",
            List.of("Focus on shared user goals rather than turf battles", "Demonstrate collaborative prototyping to find win-win solutions", "Technical creativity: using GPU transforms to solve performance trade-offs"),
            "Shows cross-functional empathy. Great engineers build bridges with design and product teams.",
            List.of("Build collaborative prototypes, align on shared user experience goals, and find creative win-win technical compromises", "Ignore the designer's mockups and build whatever you want", "Tell the product manager that design doesn't matter for developers", "Escalate to the CEO immediately over minor CSS transitions"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-team-3", "hr", "teamwork-communication", "Easy",
            "Mentoring & Helping a Struggling Colleague",
            "Tell me about a time you went out of your way to help a classmate or junior teammate who was stuck on a difficult problem.",
            null,
            "STAR Mentorship Blueprint:\n\n- Situation: \"A junior teammate was struggling for two days to implement pagination and filtering on a relational database table, repeatedly hitting N+1 query performance warnings in their logs.\"\n- Task: \"Help them resolve the performance bug without simply writing the code for them or making them feel incompetent.\"\n- Action: \"I invited them to a 30-minute pair-programming session. Rather than taking over their keyboard, I asked guiding questions: 'What queries do you see executing in the terminal when you load page 1?' Once they observed the 50 repeated SELECT statements, I explained the difference between eager loading and lazy loading with a simple diagram on a digital whiteboard, and guided them as they refactored the query with a single JOIN.\"\n- Result: \"They solved the issue themselves, cutting database queries from 51 to 1. More importantly, they gained the confidence to handle relational query optimizations independently on future tickets.\"",
            List.of("Mentorship through guiding questions rather than taking over", "Patience and psychological safety", "Empowering the learner with durable understanding"),
            "Proves you are a team multiplier who elevates those around you.",
            List.of("Empower the teammate using Socratic guiding questions and conceptual diagrams so they understand and solve the problem themselves", "Grab their keyboard and write the code for them while laughing", "Tell them to figure it out on their own because everyone is busy", "Report them to management for being slow"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-team-4", "hr", "teamwork-communication", "Hard",
            "Pushing Back Respectfully on Unrealistic Deadlines",
            "How do you handle a situation where leadership or a client demands an extensive feature in a timeline that would severely compromise code quality or stability?",
            null,
            "STAR Constructive Pushback Protocol:\n\n- Situation: \"Two weeks before an end-of-semester exam period, our platform sponsor requested adding automated plagiarism detection across 1,000+ student code repositories in just 7 days.\"\n- Task: \"Building a full AST-based semantic plagiarism engine in 7 days was technically infeasible without skipping security testing and risking false positives that could wrongly penalize students.\"\n- Action: \"Instead of a blunt 'no', I scheduled a meeting with data. I presented three options:\n  1. Option A (MVP): Ship a lightweight tokenized string-matching algorithm for the upcoming exam window, which could be built and tested safely in 5 days.\n  2. Option B (Full Scope): Deliver the deep AST semantic engine on a realistic 4-week timeline for the following quarter.\n  3. I explained the risks of false positives to institutional reputation if we rushed.\"\n- Result: \"The sponsor appreciated the transparent risk analysis and chose Option A. The exam proceeded smoothly with zero false positive disputes, and we delivered Option B on schedule the following month.\"",
            List.of("Replace emotional pushback with data and risk analysis", "Offer constructive alternative options (MVP vs full scope)", "Protect customer trust and product stability"),
            "Shows executive poise. Leaders respect engineers who protect the business from reckless shortcuts.",
            List.of("Present structured data-backed options with trade-offs (MVP vs full scope), clearly explaining business risks to reach an aligned decision", "Blindly agree to the deadline and ship broken, untested code", "Yell at management that they don't understand software engineering", "Pretend to work on the feature and claim your computer crashed on deadline day"), 0
        ));

        questionBank.add(new InterviewQuestion(
            "hr-team-5", "hr", "teamwork-communication", "Medium",
            "Fostering Inclusivity and Psychological Safety on an Engineering Team",
            "How do you ensure that all teammates—including junior developers and quieter colleagues—feel safe contributing ideas in team meetings?",
            null,
            "Fostering Psychological Safety:\n\n1. Active Solicitation of Quiet Voices: \"In design reviews, I deliberately invite input from teammates who haven't spoken yet: 'Alex, you've worked on similar caching layers before, what are your thoughts on this approach?'\"\n2. Destigmatizing Questions: \"I openly ask questions when I don't know something. When senior engineers admit what they don't know, it gives permission to junior teammates to ask questions without fear of judgment.\"\n3. Celebrate Blameless Learnings: \"During sprint retrospectives or post-mortems, I focus exclusively on systems and processes rather than attributing blame to individuals. We ask 'What failed in our testing guardrails?' rather than 'Who wrote this bug?'\"",
            List.of("Active inclusion of quieter teammates", "Humility and vulnerability in technical discussions", "Blameless culture focused on improving guardrails"),
            "Essential for senior roles: true leaders build environments where every engineer does their best work.",
            List.of("Actively invite input from quieter members, openly admit what you don't know to destigmatize learning, and maintain blameless retrospectives", "Dominate every meeting and talk over junior colleagues", "Mock teammates when they ask basic questions", "Insist that only senior engineers are allowed to propose ideas"), 0
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
