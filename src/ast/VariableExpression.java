package ast;

import token.Token;

/**
 * Represents a variable expression (e.g., x, myVariable)
 */
public class VariableExpression implements Expression {
    private final Token name;
    
    public VariableExpression(Token name) {
        this.name = name;
    }
    
    public Token getName() {
        return name;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableExpression(this);
    }
    
    @Override
    public String toString() {
        return name.getValue();
    }
}