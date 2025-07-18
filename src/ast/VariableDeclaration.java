package ast;

import token.Token;

/**
 * Represents a variable declaration (e.g., int x = 5;)
 */
public class VariableDeclaration implements Statement {
    private final Token type;
    private final Token name;
    private final Expression initializer;
    
    public VariableDeclaration(Token type, Token name, Expression initializer) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }
    
    public Token getType() {
        return type;
    }
    
    public Token getName() {
        return name;
    }
    
    public Expression getInitializer() {
        return initializer;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
    
    @Override
    public String toString() {
        if (initializer != null) {
            return String.format("%s %s = %s;", type.getValue(), name.getValue(), initializer);
        } else {
            return String.format("%s %s;", type.getValue(), name.getValue());
        }
    }
}