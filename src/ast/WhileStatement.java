package ast;

/**
 * Represents a while statement (e.g., while (condition) body)
 */
public class WhileStatement implements Statement {
    private final Expression condition;
    private final Statement body;
    
    public WhileStatement(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public Statement getBody() {
        return body;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitWhileStatement(this);
    }
    
    @Override
    public String toString() {
        return String.format("while (%s) %s", condition, body);
    }
}