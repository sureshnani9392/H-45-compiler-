package ast;

import java.util.List;

/**
 * Represents a block statement (e.g., { ... })
 */
public class BlockStatement implements Statement {
    private final List<Statement> statements;
    
    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBlockStatement(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Statement stmt : statements) {
            sb.append("  ").append(stmt).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}