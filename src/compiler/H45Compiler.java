package compiler;

import lexer.Lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import codegen.CodeGenerator;
import ast.Program;
import token.Token;
import error.ErrorHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main compiler class for the H-45 language
 */
public class H45Compiler {
    private final ErrorHandler errorHandler;
    
    public H45Compiler() {
        this.errorHandler = new ErrorHandler();
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java H45Compiler <source-file>");
            System.exit(1);
        }
        
        H45Compiler compiler = new H45Compiler();
        String result = compiler.compile(args[0]);
        
        if (result != null) {
            System.out.println("Compilation successful!");
            System.out.println("\nGenerated Code:");
            System.out.println("===============");
            System.out.println(result);
            
            // Write output to file
            try {
                String outputFile = args[0].replaceAll("\\.[^.]*$", ".asm");
                Files.write(Paths.get(outputFile), result.getBytes());
                System.out.println("\nOutput written to: " + outputFile);
            } catch (IOException e) {
                System.err.println("Error writing output file: " + e.getMessage());
            }
        } else {
            System.err.println("Compilation failed!");
            System.exit(1);
        }
    }
    
    public String compile(String sourceFile) {
        try {
            // Read source file
            String source = readFile(sourceFile);
            return compileSource(source);
        } catch (IOException e) {
            errorHandler.reportError(error.CompilerError.ErrorType.SEMANTIC_ERROR,
                    "Could not read source file: " + e.getMessage());
            return null;
        }
    }
    
    public String compileSource(String source) {
        try {
            // Phase 1: Lexical Analysis
            System.out.println("Phase 1: Lexical Analysis...");
            Lexer lexer = new Lexer(source, errorHandler);
            List<Token> tokens = lexer.tokenize();
            
            if (errorHandler.hasErrors()) {
                errorHandler.printSummary();
                return null;
            }
            
            System.out.println("Tokens generated: " + tokens.size());
            
            // Phase 2: Syntax Analysis
            System.out.println("Phase 2: Syntax Analysis...");
            Parser parser = new Parser(tokens, errorHandler);
            Program ast = parser.parse();
            
            if (errorHandler.hasErrors()) {
                errorHandler.printSummary();
                return null;
            }
            
            System.out.println("AST generated successfully");
            
            // Phase 3: Semantic Analysis
            System.out.println("Phase 3: Semantic Analysis...");
            SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
            analyzer.analyze(ast);
            
            if (errorHandler.hasErrors()) {
                errorHandler.printSummary();
                return null;
            }
            
            System.out.println("Semantic analysis completed");
            
            // Phase 4: Code Generation
            System.out.println("Phase 4: Code Generation...");
            CodeGenerator generator = new CodeGenerator();
            String generatedCode = generator.generate(ast);
            
            errorHandler.printSummary();
            return generatedCode;
            
        } catch (Exception e) {
            System.err.println("Unexpected error during compilation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean hasErrors() {
        return errorHandler.hasErrors();
    }
    
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
    
    private String readFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
    
    // Utility method for interactive compilation
    public void compileAndShowTokens(String source) {
        System.out.println("=== LEXICAL ANALYSIS ===");
        Lexer lexer = new Lexer(source, errorHandler);
        List<Token> tokens = lexer.tokenize();
        
        for (Token token : tokens) {
            System.out.println(token);
        }
        
        if (!errorHandler.hasErrors()) {
            System.out.println("\n=== SYNTAX ANALYSIS ===");
            Parser parser = new Parser(tokens, errorHandler);
            Program ast = parser.parse();
            
            if (!errorHandler.hasErrors()) {
                System.out.println("AST:");
                System.out.println(ast);
                
                System.out.println("\n=== SEMANTIC ANALYSIS ===");
                SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
                analyzer.analyze(ast);
                
                if (!errorHandler.hasErrors()) {
                    System.out.println("\n=== CODE GENERATION ===");
                    CodeGenerator generator = new CodeGenerator();
                    String code = generator.generate(ast);
                    System.out.println(code);
                }
            }
        }
        
        errorHandler.printSummary();
        errorHandler.clear();
    }
}