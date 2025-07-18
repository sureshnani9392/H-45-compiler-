package ast;

import token.Token;
import java.util.List;

/**
 * Represents a function declaration (e.g., int foo(int x, int y) { ... })
 */
public class FunctionDeclaration implements Statement {
    private final Token returnType;
    private final Token name;
    private final List<Parameter> parameters;
    private final BlockStatement body;
    
    public static class Parameter {
        private final Token type;
        private final Token name;
        
        public Parameter(Token type, Token name) {
            this.type = type;
            this.name = name;
        }
        
        public Token getType() {
            return type;
        }
        
        public Token getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return String.format("%s %s", type.getValue(), name.getValue());
        }
    }
    
    public FunctionDeclaration(Token returnType, Token name, List<Parameter> parameters, BlockStatement body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }
    
    public Token getReturnType() {
        return returnType;
    }
    
    public Token getName() {
        return name;
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    public BlockStatement getBody() {
        return body;
    }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType.getValue()).append(" ").append(name.getValue()).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameters.get(i));
        }
        sb.append(") ").append(body);
        return sb.toString();
    }
}