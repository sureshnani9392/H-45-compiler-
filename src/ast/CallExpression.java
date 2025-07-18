package ast;

import token.Token;
import java.util.List;

/**
 * Represents a function call expression (e.g., foo(a, b))
 */
public class CallExpression implements Expression {
    private final Expression callee;
    private final Token paren;
    private final List<Expression> arguments;
    
    public CallExpression(Expression callee, Token paren, List<Expression> arguments) {
        this.callee = callee;
        this.paren = paren;
        this.arguments = arguments;
    }
    
    public Expression getCallee() {
        return callee;
    }
    
    public Token getParen() {
        return paren;
    }
    
    public List<Expression> getArguments() {
        return arguments;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitCallExpression(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(callee).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(arguments.get(i));
        }
        sb.append(")");
        return sb.toString();
    }
}