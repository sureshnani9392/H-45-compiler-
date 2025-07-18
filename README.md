# H-45 Compiler

A complete compiler implementation written in Java for the H-45 programming language.

## Features

- **Lexical Analysis**: Tokenizes source code into meaningful tokens
- **Syntax Analysis**: Recursive descent parser with error recovery
- **Semantic Analysis**: Type checking and symbol table management
- **Code Generation**: Generates target code (assembly-like intermediate representation)
- **Error Handling**: Comprehensive error reporting with line numbers and descriptions

## Architecture

The H-45 compiler consists of several key components:

1. **Lexer** (`src/lexer/`): Tokenizes the input source code
2. **Parser** (`src/parser/`): Builds an Abstract Syntax Tree (AST)
3. **AST** (`src/ast/`): Abstract Syntax Tree node definitions
4. **Semantic Analyzer** (`src/semantic/`): Type checking and symbol resolution
5. **Code Generator** (`src/codegen/`): Generates target code
6. **Symbol Table** (`src/symboltable/`): Manages variable and function declarations
7. **Error Handler** (`src/error/`): Centralized error reporting

## H-45 Language Syntax

The H-45 language supports:
- Variable declarations with types (int, float, bool, string)
- Arithmetic and logical expressions
- Control flow (if/else, while, for)
- Function definitions and calls
- Basic I/O operations

Example H-45 program:
```
int main() {
    int x = 10;
    int y = 20;
    int sum = x + y;
    print(sum);
    return 0;
}
```

## Building and Running

```bash
# Compile the compiler
javac -d bin src/**/*.java

# Run the compiler on a H-45 source file
java -cp bin compiler.H45Compiler input.h45
```

## Project Structure

```
H-45/
├── src/
│   ├── compiler/          # Main compiler class
│   ├── lexer/            # Lexical analysis
│   ├── parser/           # Syntax analysis
│   ├── ast/              # Abstract Syntax Tree
│   ├── semantic/         # Semantic analysis
│   ├── codegen/          # Code generation
│   ├── symboltable/      # Symbol table management
│   ├── error/            # Error handling
│   └── token/            # Token definitions
├── examples/             # Example H-45 programs
├── tests/               # Test cases
└── bin/                 # Compiled Java classes
```