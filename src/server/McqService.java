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
            "Java caches Integer objects between -128 and 127 via IntegerCache. Values outside this range create distinct object instances on the heap.",
            "Hard"
        ));
        javaList.add(new McqQuestion(
            "java-7", "java",
            "In the Java Memory Model (JMM), what does the volatile keyword guarantee regarding shared variables?",
            null,
            List.of(
                "Visibility across threads and establishes a happens-before relationship, but does NOT guarantee compound atomicity (like i++)",
                "Both mutual exclusion locking and atomic compound updates like compare-and-swap",
                "Prevents garbage collection of the referenced instance",
                "Forces the variable to be allocated in thread-local storage"
            ),
            0,
            "volatile prevents instruction reordering and forces reads/writes directly to main memory, establishing a happens-before relationship without lock-based mutual exclusion.",
            "Hard"
        ));
        javaList.add(new McqQuestion(
            "java-8", "java",
            "What will be printed when calling testMethod()?",
            "public static int testMethod() {\n    try {\n        return 1;\n    } catch (Exception e) {\n        return 2;\n    } finally {\n        return 3;\n    }\n}",
            List.of("3", "1", "2", "Compilation error: unreachable statement"),
            0,
            "A return statement inside a finally block silently overrides and suppresses any preceding return value or uncaught exception from try or catch blocks.",
            "Hard"
        ));
        javaList.add(new McqQuestion(
            "java-9", "java",
            "Due to Java Generic Type Erasure, how does the compiler maintain polymorphism when a class implements a parameterized interface?",
            null,
            List.of(
                "By generating synthetic bridge methods in the bytecode",
                "By reifying types at class loading time",
                "By creating duplicate classes for every type parameter",
                "By using dynamic proxy reflection wrappers"
            ),
            0,
            "The compiler generates synthetic bridge methods with Object (or upper bound) signatures to satisfy virtual method dispatch after type erasure.",
            "Hard"
        ));
        javaList.add(new McqQuestion(
            "java-10", "java",
            "What happens if two threads simultaneously attempt to initialize mutually dependent classes with static initializer blocks?",
            null,
            List.of(
                "A ClassLoader deadlock can occur where both threads hang indefinitely waiting on class initialization locks",
                "The JVM throws a ClassCircularityError immediately at compile time",
                "The JVM creates two separate classloaders to break the cycle",
                "The class declared second is discarded"
            ),
            0,
            "The JVM acquires a per-class initialization lock. Circular references between static initializers across multiple threads can cause an unrecoverable deadlock.",
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
        pyList.add(new McqQuestion(
            "py-6", "python",
            "What is the output of this closure snippet in Python?",
            "funcs = [lambda: x for x in range(3)]\nprint([f() for f in funcs])",
            List.of("[2, 2, 2]", "[0, 1, 2]", "[2, 1, 0]", "TypeError"),
            0,
            "Python closures bind variables by reference (late binding). When the lambdas execute, they look up the final value of x in the outer scope, which is 2.",
            "Hard"
        ));
        pyList.add(new McqQuestion(
            "py-7", "python",
            "Which algorithm does Python use to determine the Method Resolution Order (MRO) in multiple inheritance?",
            null,
            List.of(
                "C3 Linearization algorithm",
                "Depth-First Left-to-Right Search",
                "Breadth-First Topological Sort",
                "Dijkstra's shortest path"
            ),
            0,
            "Python uses the C3 Linearization algorithm to compute the MRO, guaranteeing monotonicity and preserving local precedence order.",
            "Hard"
        ));
        pyList.add(new McqQuestion(
            "py-8", "python",
            "What is the output of this integer comparison in standard CPython interactive shell?",
            "a = 256\nb = 256\nc = 257\nd = 257\nprint((a is b), (c is d))",
            List.of("True False", "True True", "False False", "False True"),
            0,
            "CPython pre-allocates and caches an array of small integer objects in the range [-5, 256]. Values outside this range instantiate distinct objects.",
            "Hard"
        ));
        pyList.add(new McqQuestion(
            "py-9", "python",
            "In Python metaclass programming, what is the key difference between __new__ and __init__ on a metaclass?",
            null,
            List.of(
                "__new__ allocates and returns the new class object; __init__ initializes attributes of the already-created class object",
                "__new__ initializes instances of the class; __init__ creates the class definition",
                "__new__ is called only on subclassing; __init__ is called on instantiation",
                "__new__ is a private method; __init__ is public"
            ),
            0,
            "In a metaclass, `__new__` constructs the class object before it exists in memory, while `__init__` populates attributes after creation.",
            "Hard"
        ));
        pyList.add(new McqQuestion(
            "py-10", "python",
            "What does `yield from` in a Python generator do when delegating to a sub-generator that returns a value?",
            "def sub():\n    yield 1\n    return 'done'\ndef parent():\n    val = yield from sub()\n    yield val\nprint(list(parent()))",
            List.of("[1, 'done']", "[1]", "['done']", "SyntaxError: return with argument inside generator"),
            0,
            "`yield from` transparently yields all values from the sub-generator and evaluates to the return value of that sub-generator.",
            "Hard"
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
        jsList.add(new McqQuestion(
            "js-5", "javascript",
            "What occurs when accessing a `let` or `const` variable before its line of declaration?",
            "console.log(myVar);\nlet myVar = 10;",
            List.of(
                "ReferenceError due to the Temporal Dead Zone (TDZ)",
                "Outputs undefined due to variable hoisting",
                "Outputs null",
                "SyntaxError: variable not defined"
            ),
            0,
            "`let` and `const` variables are hoisted but uninitialized. Accessing them before initialization throws a ReferenceError due to the TDZ.",
            "Hard"
        ));
        jsList.add(new McqQuestion(
            "js-6", "javascript",
            "What will this code output regarding arrow function `this` binding?",
            "const obj = {\n  num: 42,\n  getVal: () => this.num\n};\nconsole.log(obj.getVal());",
            List.of("undefined", "42", "TypeError", "null"),
            0,
            "Arrow functions do not bind their own `this`. They inherit `this` lexically from the enclosing scope (which in global context is window/global).",
            "Hard"
        ));
        jsList.add(new McqQuestion(
            "js-7", "javascript",
            "What does the expression `[] == ![]` evaluate to in JavaScript, and why?",
            null,
            List.of(
                "true, because ![] is false (0), and [] converts to empty string and then 0",
                "false, because arrays are non-primitive objects with different references",
                "TypeError, cannot negate an array instance",
                "undefined"
            ),
            0,
            "`![]` coerces to false. In `[] == false`, both sides convert to numbers: `Number([]) -> 0` and `Number(false) -> 0`, resulting in `0 == 0 -> true`.",
            "Hard"
        ));
        jsList.add(new McqQuestion(
            "js-8", "javascript",
            "What will happen when accessing `protoObj.toString` on `const protoObj = Object.create(null)`?",
            null,
            List.of(
                "protoObj.toString is undefined because the object does not inherit from Object.prototype",
                "It prints '[object Object]'",
                "Throws a ReferenceError",
                "Returns an empty string"
            ),
            0,
            "`Object.create(null)` creates a truly dictionary-like object with no prototype chain (`__proto__` is null), having no standard Object methods.",
            "Hard"
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
        cList.add(new McqQuestion(
            "c-5", "c",
            "What does the C standard state regarding an expression like `i = i++ + ++i;`?",
            null,
            List.of(
                "It produces Undefined Behavior because a scalar object is modified more than once between sequence points",
                "It produces Implementation-Defined behavior evaluated strictly left to right",
                "It is a syntax error in C99/C11",
                "It reliably increments i by 3"
            ),
            0,
            "Modifying a scalar object more than once without an intervening sequence point violates the C standard and invokes Undefined Behavior.",
            "Hard"
        ));
        cList.add(new McqQuestion(
            "c-6", "c",
            "What is the difference between `int (*p)[5]` and `int *p[5]` in C declaration syntax?",
            null,
            List.of(
                "`int (*p)[5]` is a pointer to an array of 5 ints; `int *p[5]` is an array of 5 pointers to int",
                "`int (*p)[5]` is an array of pointers; `int *p[5]` is a pointer to an array",
                "Both are identical pointers to 2D arrays",
                "`int (*p)[5]` is invalid syntax"
            ),
            0,
            "Parentheses bind `*` to `p` first in `int (*p)[5]`, making it a pointer to an array. In `int *p[5]`, array brackets `[]` have higher precedence than `*`.",
            "Hard"
        ));
        cList.add(new McqQuestion(
            "c-7", "c",
            "On a standard 64-bit architecture with natural alignment, what is `sizeof(struct S)`?",
            "struct S {\n    char a;\n    int b;\n    char c;\n};",
            List.of("12 bytes", "6 bytes", "8 bytes", "16 bytes"),
            0,
            "`char a` (1 byte) + 3 bytes padding + `int b` (4 bytes) + `char c` (1 byte) + 3 bytes trailing padding to align to 4-byte multiple = 12 bytes.",
            "Hard"
        ));
        cList.add(new McqQuestion(
            "c-8", "c",
            "Which of the following declarations creates a pointer whose target address CANNOT be changed, but the integer data it points to CAN be modified?",
            null,
            List.of(
                "int * const ptr",
                "const int * ptr",
                "const int * const ptr",
                "int const * ptr"
            ),
            0,
            "`int * const ptr` is a constant pointer to a mutable integer. `const int *` or `int const *` is a mutable pointer to a constant integer.",
            "Hard"
        ));
        cList.add(new McqQuestion(
            "c-9", "c",
            "What catastrophic issue exists in the following C function?",
            "char* get_greeting() {\n    char msg[] = \"Hello World\";\n    return msg;\n}",
            List.of(
                "Returns the address of a local stack array that is deallocated when the function frame pops (dangling pointer)",
                "String literals in C cannot be copied to char arrays",
                "char arrays cannot have pointers pointing to them",
                "Memory leak due to missing free"
            ),
            0,
            "`msg` is allocated on the local stack frame. When `get_greeting()` returns, the frame is destroyed and accessing the pointer causes undefined behavior.",
            "Hard"
        ));
        cList.add(new McqQuestion(
            "c-10", "c",
            "What will be printed by this macro invocation in C?",
            "#define SQUARE(x) x * x\nint res = SQUARE(2 + 3);\nprintf(\"%d\\n\", res);",
            List.of("11", "25", "10", "Compilation error"),
            0,
            "Macro replacement expands `SQUARE(2 + 3)` textually into `2 + 3 * 2 + 3`. Because multiplication has higher precedence: 2 + 6 + 3 = 11.",
            "Hard"
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
        cppList.add(new McqQuestion(
            "cpp-5", "cpp",
            "What is 'Object Slicing' in C++?",
            null,
            List.of(
                "When a derived class object is assigned or passed by value to a base class object, stripping away derived-specific member variables",
                "Splitting a large vector across multiple threads",
                "Splitting memory allocation into cache line chunks",
                "Dividing template arguments during specialization"
            ),
            0,
            "Passing or assigning a derived class instance by value into a base class variable copies only the base subobject, slicing off derived members.",
            "Hard"
        ));
        cppList.add(new McqQuestion(
            "cpp-6", "cpp",
            "What happens when a derived class declares a function with the same name as an overloaded function in its base class?",
            null,
            List.of(
                "All overloads of that function name in the base class are hidden unless unhidden via `using Base::func;`",
                "All overloads from both classes are automatically merged into one overload set",
                "Compilation error: cannot reuse member function names",
                "The base class implementation always takes precedence"
            ),
            0,
            "In C++, name lookup happens before overload resolution. A function declaration in derived scope hides all functions with that same name in base classes.",
            "Hard"
        ));
        cppList.add(new McqQuestion(
            "cpp-7", "cpp",
            "What is the 'Static Initialization Order Fiasco' in C++?",
            null,
            List.of(
                "The undefined relative order of initialization of static objects defined across different translation units",
                "A compiler failure when initializing static constexpr members",
                "Stack overflow caused by recursively nested static classes",
                "Failure of the thread_local storage destructor"
            ),
            0,
            "The C++ standard provides no guarantee on the initialization order of non-local static objects across different `.cpp` translation units.",
            "Hard"
        ));
        cppList.add(new McqQuestion(
            "cpp-8", "cpp",
            "In C++11, what does the Rule of Five dictate when a class manages a raw resource?",
            null,
            List.of(
                "If you define any of: destructor, copy constructor, copy assignment, move constructor, or move assignment, you should define or delete all five",
                "Classes must not have more than five private member variables",
                "Virtual tables can have at most five levels of inheritance",
                "Templates cannot take more than five type parameters"
            ),
            0,
            "Resource-managing classes must implement or delete the 5 special member functions to maintain RAII invariants during copy and move operations.",
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
        sqlList.add(new McqQuestion(
            "sql-5", "sql",
            "Given duplicate scores (100, 100, 90), what ranks are produced by `RANK()` vs `DENSE_RANK()` in SQL window functions?",
            null,
            List.of(
                "RANK() produces (1, 1, 3); DENSE_RANK() produces (1, 1, 2)",
                "RANK() produces (1, 1, 2); DENSE_RANK() produces (1, 1, 3)",
                "Both produce (1, 2, 3)",
                "Both produce (1, 1, 1)"
            ),
            0,
            "`RANK()` leaves gaps after tied ranks (1, 1, 3), whereas `DENSE_RANK()` assigns consecutive rank numbers without gaps (1, 1, 2).",
            "Hard"
        ));
        sqlList.add(new McqQuestion(
            "sql-6", "sql",
            "Why will `SELECT * FROM tbl WHERE id NOT IN (SELECT foreign_id FROM other_tbl)` return ZERO rows if other_tbl contains even a single NULL foreign_id?",
            null,
            List.of(
                "Due to Three-Valued Logic: `id <> NULL` evaluates to UNKNOWN, making the entire AND-ed NOT IN predicate UNKNOWN (never true)",
                "The SQL engine throws a NULL pointer exception",
                "NOT IN only operates on positive integer values",
                "NULL foreign keys automatically delete matching rows"
            ),
            0,
            "In SQL Three-Valued Logic, `val NOT IN (1, 2, NULL)` expands to `val <> 1 AND val <> 2 AND val <> NULL`. Since `<> NULL` is UNKNOWN, the entire condition evaluates to UNKNOWN.",
            "Hard"
        ));
        sqlList.add(new McqQuestion(
            "sql-7", "sql",
            "Which transaction isolation anomaly is prevented by `REPEATABLE READ`, but can still occur without `SERIALIZABLE` isolation in standard SQL?",
            null,
            List.of(
                "Phantom Read (new rows inserted by concurrent committed transactions appearing in repeated range queries)",
                "Dirty Read (reading uncommitted changes from another transaction)",
                "Non-Repeatable Read (rereading the same row yields updated data)",
                "Lost Update"
            ),
            0,
            "REPEATABLE READ locks existing rows from modification preventing non-repeatable reads, but cannot prevent another transaction from inserting new matching rows (Phantom Reads).",
            "Hard"
        ));
        sqlList.add(new McqQuestion(
            "sql-8", "sql",
            "What is a 'Covering Index' in relational database optimization?",
            null,
            List.of(
                "An index that contains all columns requested by a query, allowing the query to be fulfilled entirely from the B-tree index without touching data pages",
                "An index that covers all tables in a database schema",
                "An index that encrypts column values on disk",
                "A clustered index that spans multiple hard drives"
            ),
            0,
            "A covering index satisfies a query exclusively from the index tree leaf nodes, completely eliminating expensive table/heap lookups (Key Lookups).",
            "Hard"
        ));
        sqlList.add(new McqQuestion(
            "sql-9", "sql",
            "In recursive Common Table Expressions (WITH RECURSIVE cte AS ...), what prevents the query from entering an infinite loop?",
            null,
            List.of(
                "The recursive query must have a termination condition (WHERE clause) that eventually returns an empty result set",
                "The database kills recursive queries after exactly 10 iterations",
                "Recursive CTEs can only join on primary keys",
                "The recursion limit is hardcoded to 1 in ANSI SQL"
            ),
            0,
            "A recursive CTE consists of an anchor member and a recursive member joined by UNION ALL. The recursion terminates when the recursive member yields zero rows.",
            "Hard"
        ));
        languageBanks.put("sql", sqlList);
    }

    public static List<McqQuestion> getDynamicQuestions(String language, int requestedCount) {
        return getDynamicQuestions(language, "all", requestedCount);
    }

    public static List<McqQuestion> getDynamicQuestions(String language, String difficulty, int requestedCount) {
        if (language == null || language.isEmpty()) language = "all";
        language = language.toLowerCase().trim();

        if (difficulty == null || difficulty.isEmpty()) difficulty = "all";
        difficulty = difficulty.trim();

        List<McqQuestion> candidates = new ArrayList<>();

        if ("all".equals(language)) {
            for (List<McqQuestion> list : languageBanks.values()) {
                candidates.addAll(list);
            }
            // Fetch extra dynamic questions from Open Trivia DB API when on all/medium
            if ("all".equalsIgnoreCase(difficulty) || "medium".equalsIgnoreCase(difficulty)) {
                List<McqQuestion> apiQuestions = fetchFromOpenTriviaApi(4);
                candidates.addAll(apiQuestions);
            }
        } else {
            List<McqQuestion> bank = languageBanks.get(language);
            if (bank != null) {
                candidates.addAll(bank);
            } else {
                for (List<McqQuestion> list : languageBanks.values()) {
                    candidates.addAll(list);
                }
            }
        }

        // Apply Difficulty filter if specified
        if (!"all".equalsIgnoreCase(difficulty)) {
            List<McqQuestion> filtered = new ArrayList<>();
            for (McqQuestion q : candidates) {
                if (difficulty.equalsIgnoreCase(q.difficulty)) {
                    filtered.add(q);
                }
            }
            // If the filtered pool has questions, prioritize them
            if (!filtered.isEmpty()) {
                candidates = filtered;
            }
        }

        // Shuffle questions randomly
        Collections.shuffle(candidates);

        int count = Math.min(requestedCount, candidates.size());
        if (count <= 0 && !candidates.isEmpty()) count = candidates.size();

        List<McqQuestion> selected = new ArrayList<>();
        for (int i = 0; i < count && i < candidates.size(); i++) {
            selected.add(candidates.get(i));
        }

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
