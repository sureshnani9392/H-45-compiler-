package ast;

import java.util.List;

/**
 * Represents a complete program (root of the AST)
 */
public class Program implements ASTNode {
    private final List<Statement> statements;
    
    public Program(List<Statement> statements) {
        this.statements = statements;
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitProgram(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Statement stmt : statements) {
            sb.append(stmt).append("\n");
        }
        return sb.toString();
    }
}