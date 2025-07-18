package ast;

/**
 * Represents an if statement (e.g., if (condition) thenBranch else elseBranch)
 */
public class IfStatement implements Statement {
    private final Expression condition;
    private final Statement thenBranch;
    private final Statement elseBranch;
    
    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public Statement getThenBranch() {
        return thenBranch;
    }
    
    public Statement getElseBranch() {
        return elseBranch;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIfStatement(this);
    }
    
    @Override
    public String toString() {
        if (elseBranch != null) {
            return String.format("if (%s) %s else %s", condition, thenBranch, elseBranch);
        } else {
            return String.format("if (%s) %s", condition, thenBranch);
        }
    }
}