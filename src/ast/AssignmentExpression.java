package ast;

import token.Token;

/**
 * Represents an assignment expression (e.g., x = 5)
 */
public class AssignmentExpression implements Expression {
    private final Token name;
    private final Expression value;
    
    public AssignmentExpression(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }
    
    public Token getName() {
        return name;
    }
    
    public Expression getValue() {
        return value;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitAssignmentExpression(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s = %s", name.getValue(), value);
    }
}