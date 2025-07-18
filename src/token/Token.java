package token;

/**
 * Represents a token in the H-45 language
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;
    
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%s', line=%d, column=%d}", 
                           type, value, line, column);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Token token = (Token) obj;
        return line == token.line && 
               column == token.column && 
               type == token.type && 
               value.equals(token.value);
    }
    
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + line;
        result = 31 * result + column;
        return result;
    }
}