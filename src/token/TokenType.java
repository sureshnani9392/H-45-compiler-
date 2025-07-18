package token;

/**
 * Enumeration of all token types supported by the H-45 language
 */
public enum TokenType {
    // Literals
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    BOOLEAN_LITERAL,
    
    // Identifiers
    IDENTIFIER,
    
    // Keywords
    INT, FLOAT, BOOL, STRING,
    IF, ELSE, WHILE, FOR,
    FUNCTION, RETURN,
    TRUE, FALSE,
    PRINT,
    
    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
    ASSIGN,
    EQUAL, NOT_EQUAL,
    LESS_THAN, LESS_EQUAL,
    GREATER_THAN, GREATER_EQUAL,
    LOGICAL_AND, LOGICAL_OR, LOGICAL_NOT,
    
    // Delimiters
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,
    SEMICOLON, COMMA,
    
    // Special
    NEWLINE,
    EOF,
    UNKNOWN
}