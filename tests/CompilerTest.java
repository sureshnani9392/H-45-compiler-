import compiler.H45Compiler;

/**
 * Simple test class for the H-45 compiler
 */
public class CompilerTest {
    public static void main(String[] args) {
        H45Compiler compiler = new H45Compiler();
        
        System.out.println("Testing H-45 Compiler");
        System.out.println("=====================");
        
        // Test 1: Simple arithmetic
        String simpleProgram = """
            int main() {
                int x = 10;
                int y = 20;
                int sum = x + y;
                print(sum);
                return 0;
            }
            """;
        
        System.out.println("Test 1: Simple arithmetic program");
        compiler.compileAndShowTokens(simpleProgram);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Test 2: Control flow
        String controlFlow = """
            int factorial(int n) {
                if (n <= 1) {
                    return 1;
                } else {
                    return n * factorial(n - 1);
                }
            }
            
            int main() {
                int num = 5;
                int result = factorial(num);
                print(result);
                return 0;
            }
            """;
        
        System.out.println("Test 2: Control flow and recursion");
        compiler.compileAndShowTokens(controlFlow);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Test 3: Error handling
        String errorProgram = """
            int main() {
                int x = "hello";  // Type error
                undeclaredVar = 5;  // Undeclared variable
                return "not an int";  // Wrong return type
            }
            """;
        
        System.out.println("Test 3: Error handling");
        compiler.compileAndShowTokens(errorProgram);
    }
}