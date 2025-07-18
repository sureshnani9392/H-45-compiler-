# H-45 Compiler Implementation Overview

## Project Summary

The H-45 compiler is a complete compiler implementation written in Java that translates H-45 source code into assembly-like intermediate representation. The compiler demonstrates all four classic phases of compilation: lexical analysis, syntax analysis, semantic analysis, and code generation.

## Architecture

### 1. Lexical Analysis (`src/lexer/`)
- **Lexer.java**: Tokenizes source code into meaningful tokens
- **Token.java**: Represents individual tokens with type, value, and position
- **TokenType.java**: Enumerates all possible token types

### 2. Syntax Analysis (`src/parser/`)
- **Parser.java**: Recursive descent parser that builds an Abstract Syntax Tree (AST)
- Implements operator precedence and associativity
- Provides error recovery mechanisms

### 3. Abstract Syntax Tree (`src/ast/`)
- **ASTNode.java**: Base interface for all AST nodes
- **ASTVisitor.java**: Visitor pattern for traversing the AST
- Expression nodes: BinaryExpression, UnaryExpression, LiteralExpression, etc.
- Statement nodes: VariableDeclaration, IfStatement, WhileStatement, etc.
- **Program.java**: Root node representing the entire program

### 4. Semantic Analysis (`src/semantic/`)
- **SemanticAnalyzer.java**: Performs type checking and symbol resolution
- Implements scoped symbol tables
- Validates function calls and return types
- Reports semantic errors with precise location information

### 5. Symbol Table Management (`src/symboltable/`)
- **SymbolTable.java**: Manages variable and function declarations with scope support
- **Symbol.java**: Represents symbols with type information and scope level

### 6. Code Generation (`src/codegen/`)
- **CodeGenerator.java**: Generates assembly-like intermediate code
- Implements register allocation and stack management
- Handles control flow constructs (if/else, loops)
- Generates function calls with proper calling conventions

### 7. Error Handling (`src/error/`)
- **ErrorHandler.java**: Centralized error reporting system
- **CompilerError.java**: Represents compilation errors with detailed information
- Categorizes errors by type (lexical, syntax, semantic, type errors)

### 8. Main Compiler (`src/compiler/`)
- **H45Compiler.java**: Main compiler class that orchestrates all phases
- Provides command-line interface and file I/O
- Coordinates the compilation pipeline

## H-45 Language Features

### Data Types
- `int`: 32-bit integers
- `float`: Floating-point numbers
- `bool`: Boolean values (`true`, `false`)
- `string`: String literals

### Control Flow
- **Conditional statements**: `if/else`
- **Loops**: `while`, `for`
- **Functions**: User-defined functions with parameters and return values

### Operators
- **Arithmetic**: `+`, `-`, `*`, `/`, `%`
- **Comparison**: `==`, `!=`, `<`, `<=`, `>`, `>=`
- **Logical**: `&&`, `||`, `!`
- **Assignment**: `=`

### Built-in Functions
- `print()`: Output values to console

## Example Programs

### Hello World
```h45
int main() {
    print("Hello, World!");
    return 0;
}
```

### Factorial Function
```h45
int factorial(int n) {
    if (n <= 1) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

int main() {
    int result = factorial(5);
    print(result);
    return 0;
}
```

## Generated Code

The compiler generates assembly-like intermediate representation:

```assembly
; H-45 Compiler Generated Code
section .text
global _start

main:
    push ebp
    mov ebp, esp
    sub esp, 64     ; reserve space for locals
    ; Variable declaration: x
    mov eax, 10    ; integer literal
    mov [x], eax    ; store initial value
    ; Print statement
    mov eax, [x]    ; load variable x
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Return statement
    mov eax, 0    ; integer literal
    mov esp, ebp
    pop ebp
    ret

_start:
    call main
    mov eax, 1      ; sys_exit
    mov ebx, 0      ; exit status
    int 0x80        ; call kernel
```

## Compilation Process

1. **Lexical Analysis**: Source code → Tokens
2. **Syntax Analysis**: Tokens → Abstract Syntax Tree
3. **Semantic Analysis**: AST → Type-checked AST + Symbol Table
4. **Code Generation**: AST → Assembly code

## Error Handling

The compiler provides comprehensive error reporting:

- **Lexical errors**: Invalid characters, unterminated strings
- **Syntax errors**: Missing semicolons, unmatched parentheses
- **Semantic errors**: Undeclared variables, type mismatches
- **Function errors**: Wrong argument counts, return type mismatches

## Building and Running

```bash
# Build the compiler
./build.sh

# Compile a H-45 program
java -cp bin compiler.H45Compiler examples/hello.h45

# Run tests
javac -cp bin -d bin tests/CompilerTest.java
java -cp bin CompilerTest
```

## Technical Highlights

### Design Patterns Used
- **Visitor Pattern**: For AST traversal and processing
- **Strategy Pattern**: For different compilation phases
- **Builder Pattern**: For AST construction
- **Factory Pattern**: For token and AST node creation

### Advanced Features
- **Scope Management**: Nested scopes with proper variable resolution
- **Type System**: Static type checking with type inference
- **Error Recovery**: Parser continues after errors to find more issues
- **Symbol Resolution**: Forward declarations and function overloading support
- **Register Allocation**: Simplified register management in code generation

### Extensibility
The compiler is designed for easy extension:
- Adding new operators requires changes to lexer, parser, and code generator
- New data types can be added by extending the type system
- Additional language constructs follow the established visitor pattern
- Code generation targets can be changed by implementing new visitors

## Performance Characteristics

- **Time Complexity**: O(n) for each compilation phase where n is source code size
- **Space Complexity**: O(n) for AST and symbol table storage
- **Error Detection**: Comprehensive error checking with precise location reporting
- **Code Quality**: Generated code includes debugging comments and proper stack management

## Future Enhancements

Potential improvements for the H-45 compiler:

1. **Optimization Passes**: Constant folding, dead code elimination
2. **Better Code Generation**: Real register allocation, instruction selection
3. **Language Features**: Arrays, structures, pointer arithmetic
4. **Standard Library**: Built-in functions for I/O, string manipulation
5. **Debugging Support**: Line number mapping, symbol information
6. **Multiple Targets**: Generate code for different architectures

## Conclusion

The H-45 compiler demonstrates a complete implementation of all major compiler phases, providing a solid foundation for understanding compiler construction. The modular architecture, comprehensive error handling, and clean separation of concerns make it both educational and practical.