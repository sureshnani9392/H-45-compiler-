package ast;

import token.Token;

/**
 * Represents a unary expression (e.g., -x, !flag)
 */
public class UnaryExpression implements Expression {
    private final Token operator;
    private final Expression operand;
    
    public UnaryExpression(Token operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }
    
    public Token getOperator() {
        return operator;
    }
    
    public Expression getOperand() {
        return operand;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitUnaryExpression(this);
    }
    
    @Override
    public String toString() {
        return String.format("(%s%s)", operator.getValue(), operand);
    }
}