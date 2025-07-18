package ast;

/**
 * Represents a literal expression (e.g., 42, "hello", true)
 */
public class LiteralExpression implements Expression {
    private final Object value;
    
    public LiteralExpression(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return value;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }
    
    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}