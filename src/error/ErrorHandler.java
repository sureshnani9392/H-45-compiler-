package error;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized error handling for the H-45 compiler
 */
public class ErrorHandler {
    private final List<CompilerError> errors;
    private final List<CompilerError> warnings;
    
    public ErrorHandler() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    public void reportError(CompilerError error) {
        errors.add(error);
        System.err.println("ERROR: " + error);
    }
    
    public void reportError(CompilerError.ErrorType type, String message, int line, int column) {
        reportError(new CompilerError(type, message, line, column));
    }
    
    public void reportError(CompilerError.ErrorType type, String message) {
        reportError(new CompilerError(type, message));
    }
    
    public void reportWarning(CompilerError warning) {
        warnings.add(warning);
        System.err.println("WARNING: " + warning);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public int getWarningCount() {
        return warnings.size();
    }
    
    public List<CompilerError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public List<CompilerError> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    public void clear() {
        errors.clear();
        warnings.clear();
    }
    
    public void printSummary() {
        if (hasErrors()) {
            System.err.println("\nCompilation failed with " + getErrorCount() + " error(s)");
            if (hasWarnings()) {
                System.err.println("and " + getWarningCount() + " warning(s)");
            }
        } else if (hasWarnings()) {
            System.err.println("\nCompilation completed with " + getWarningCount() + " warning(s)");
        } else {
            System.out.println("\nCompilation completed successfully");
        }
    }
}