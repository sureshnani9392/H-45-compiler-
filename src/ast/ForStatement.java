package ast;

/**
 * Represents a for statement (e.g., for (init; condition; update) body)
 */
public class ForStatement implements Statement {
    private final Statement initializer;
    private final Expression condition;
    private final Expression increment;
    private final Statement body;
    
    public ForStatement(Statement initializer, Expression condition, Expression increment, Statement body) {
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }
    
    public Statement getInitializer() {
        return initializer;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public Expression getIncrement() {
        return increment;
    }
    
    public Statement getBody() {
        return body;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitForStatement(this);
    }
    
    @Override
    public String toString() {
        return String.format("for (%s; %s; %s) %s", initializer, condition, increment, body);
    }
}