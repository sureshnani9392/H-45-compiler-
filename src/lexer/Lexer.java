package lexer;

import token.Token;
import token.TokenType;
import error.ErrorHandler;
import error.CompilerError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lexical analyzer for the H-45 language
 */
public class Lexer {
    private final String source;
    private final ErrorHandler errorHandler;
    private final List<Token> tokens;
    
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;
    
    private static final Map<String, TokenType> keywords = new HashMap<>();
    
    static {
        keywords.put("int", TokenType.INT);
        keywords.put("float", TokenType.FLOAT);
        keywords.put("bool", TokenType.BOOL);
        keywords.put("string", TokenType.STRING);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("return", TokenType.RETURN);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("print", TokenType.PRINT);
    }
    
    public Lexer(String source, ErrorHandler errorHandler) {
        this.source = source;
        this.errorHandler = errorHandler;
        this.tokens = new ArrayList<>();
    }
    
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
            case '\n':
                addToken(TokenType.NEWLINE);
                line++;
                column = 1;
                break;
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '[':
                addToken(TokenType.LEFT_BRACKET);
                break;
            case ']':
                addToken(TokenType.RIGHT_BRACKET);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                addToken(TokenType.MULTIPLY);
                break;
            case '/':
                if (match('/')) {
                    // Line comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;
            case '%':
                addToken(TokenType.MODULO);
                break;
            case '!':
                addToken(match('=') ? TokenType.NOT_EQUAL : TokenType.LOGICAL_NOT);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL : TokenType.ASSIGN);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS_THAN);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);
                break;
            case '&':
                if (match('&')) {
                    addToken(TokenType.LOGICAL_AND);
                } else {
                    errorHandler.reportError(CompilerError.ErrorType.LEXICAL_ERROR,
                            "Unexpected character: '&'", line, column - 1);
                }
                break;
            case '|':
                if (match('|')) {
                    addToken(TokenType.LOGICAL_OR);
                } else {
                    errorHandler.reportError(CompilerError.ErrorType.LEXICAL_ERROR,
                            "Unexpected character: '|'", line, column - 1);
                }
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    errorHandler.reportError(CompilerError.ErrorType.LEXICAL_ERROR,
                            "Unexpected character: '" + c + "'", line, column - 1);
                }
                break;
        }
    }
    
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
        
        if (isAtEnd()) {
            errorHandler.reportError(CompilerError.ErrorType.LEXICAL_ERROR,
                    "Unterminated string", line, column);
            return;
        }
        
        // The closing "
        advance();
        
        // Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING_LITERAL, value);
    }
    
    private void number() {
        while (isDigit(peek())) advance();
        
        // Look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            
            while (isDigit(peek())) advance();
            addToken(TokenType.FLOAT_LITERAL);
        } else {
            addToken(TokenType.INTEGER_LITERAL);
        }
    }
    
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        
        if (type == TokenType.TRUE || type == TokenType.FALSE) {
            addToken(TokenType.BOOLEAN_LITERAL, text);
        } else {
            addToken(type);
        }
    }
    
    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    private char advance() {
        column++;
        return source.charAt(current++);
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        
        current++;
        column++;
        return true;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    
    private void addToken(TokenType type, String literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, literal != null ? literal : text, line, column - text.length()));
    }
}