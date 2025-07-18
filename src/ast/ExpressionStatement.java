package ast;

/**
 * Represents an expression statement (e.g., function call as a statement)
 */
public class ExpressionStatement implements Statement {
    private final Expression expression;
    
    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
    
    public Expression getExpression() {
        return expression;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitExpressionStatement(this);
    }
    
    @Override
    public String toString() {
        return expression + ";";
    }
}