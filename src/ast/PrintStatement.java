package ast;

/**
 * Represents a print statement (e.g., print(expression);)
 */
public class PrintStatement implements Statement {
    private final Expression expression;
    
    public PrintStatement(Expression expression) {
        this.expression = expression;
    }
    
    public Expression getExpression() {
        return expression;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitPrintStatement(this);
    }
    
    @Override
    public String toString() {
        return String.format("print(%s);", expression);
    }
}