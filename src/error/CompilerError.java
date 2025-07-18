package error;

/**
 * Represents a compilation error in the H-45 compiler
 */
public class CompilerError extends Exception {
    private final int line;
    private final int column;
    private final ErrorType type;
    
    public enum ErrorType {
        LEXICAL_ERROR,
        SYNTAX_ERROR,
        SEMANTIC_ERROR,
        TYPE_ERROR,
        UNDECLARED_VARIABLE,
        REDECLARATION_ERROR,
        FUNCTION_NOT_FOUND,
        ARGUMENT_MISMATCH
    }
    
    public CompilerError(ErrorType type, String message, int line, int column) {
        super(message);
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    public CompilerError(ErrorType type, String message) {
        this(type, message, -1, -1);
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public ErrorType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        if (line >= 0 && column >= 0) {
            return String.format("[%s] Line %d, Column %d: %s", 
                               type, line, column, getMessage());
        } else {
            return String.format("[%s] %s", type, getMessage());
        }
    }
}