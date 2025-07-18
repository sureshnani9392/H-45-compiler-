#!/bin/bash

# H-45 Compiler Build Script

echo "Building H-45 Compiler..."

# Create directories if they don't exist
mkdir -p bin

# Compile all Java source files
echo "Compiling Java sources..."
javac -d bin src/**/*.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo ""
    echo "Usage:"
    echo "  java -cp bin compiler.H45Compiler <source-file.h45>"
    echo ""
    echo "Examples:"
    echo "  java -cp bin compiler.H45Compiler examples/hello.h45"
    echo "  java -cp bin compiler.H45Compiler examples/arithmetic.h45"
    echo "  java -cp bin compiler.H45Compiler examples/control_flow.h45"
    echo ""
    echo "Test the compiler:"
    echo "  javac -cp bin -d bin tests/CompilerTest.java"
    echo "  java -cp bin CompilerTest"
else
    echo "Build failed!"
    exit 1
fi