package ast;

import token.Token;

/**
 * Represents a binary expression (e.g., a + b, x == y)
 */
public class BinaryExpression implements Expression {
    private final Expression left;
    private final Token operator;
    private final Expression right;
    
    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    public Expression getLeft() {
        return left;
    }
    
    public Token getOperator() {
        return operator;
    }
    
    public Expression getRight() {
        return right;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBinaryExpression(this);
    }
    
    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator.getValue(), right);
    }
}