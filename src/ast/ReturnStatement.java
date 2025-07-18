package ast;

import token.Token;

/**
 * Represents a return statement (e.g., return expression;)
 */
public class ReturnStatement implements Statement {
    private final Token keyword;
    private final Expression value;
    
    public ReturnStatement(Token keyword, Expression value) {
        this.keyword = keyword;
        this.value = value;
    }
    
    public Token getKeyword() {
        return keyword;
    }
    
    public Expression getValue() {
        return value;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitReturnStatement(this);
    }
    
    @Override
    public String toString() {
        if (value != null) {
            return String.format("return %s;", value);
        } else {
            return "return;";
        }
    }
}