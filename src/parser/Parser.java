package parser;

import token.Token;
import token.TokenType;
import ast.*;
import error.ErrorHandler;
import error.CompilerError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Recursive descent parser for the H-45 language
 */
public class Parser {
    private final List<Token> tokens;
    private final ErrorHandler errorHandler;
    private int current = 0;
    
    public Parser(List<Token> tokens, ErrorHandler errorHandler) {
        this.tokens = tokens;
        this.errorHandler = errorHandler;
    }
    
    public Program parse() {
        List<Statement> statements = new ArrayList<>();
        
        while (!isAtEnd()) {
            // Skip newlines at the top level
            if (match(TokenType.NEWLINE)) {
                continue;
            }
            
            Statement stmt = declaration();
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        
        return new Program(statements);
    }
    
    private Statement declaration() {
        try {
            if (check(TokenType.INT) || check(TokenType.FLOAT) || 
                check(TokenType.BOOL) || check(TokenType.STRING)) {
                
                if (checkNext(TokenType.IDENTIFIER) && checkNextNext(TokenType.LEFT_PAREN)) {
                    return functionDeclaration();
                } else {
                    return variableDeclaration();
                }
            }
            
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    
    private Statement functionDeclaration() {
        Token returnType = advance(); // consume type
        Token name = consume(TokenType.IDENTIFIER, "Expected function name");
        
        consume(TokenType.LEFT_PAREN, "Expected '(' after function name");
        
        List<FunctionDeclaration.Parameter> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                Token paramType = advance();
                if (!isType(paramType.getType())) {
                    errorHandler.reportError(CompilerError.ErrorType.SYNTAX_ERROR,
                            "Expected parameter type", paramType.getLine(), paramType.getColumn());
                    throw new ParseError();
                }
                
                Token paramName = consume(TokenType.IDENTIFIER, "Expected parameter name");
                parameters.add(new FunctionDeclaration.Parameter(paramType, paramName));
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters");
        consume(TokenType.LEFT_BRACE, "Expected '{' before function body");
        
        BlockStatement body = blockStatement();
        
        return new FunctionDeclaration(returnType, name, parameters, body);
    }
    
    private Statement variableDeclaration() {
        Token type = advance(); // consume type
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name");
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
        return new VariableDeclaration(type, name, initializer);
    }
    
    private Statement statement() {
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.RETURN)) return returnStatement();
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.LEFT_BRACE)) return blockStatement();
        
        return expressionStatement();
    }
    
    private Statement ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after if condition");
        
        Statement thenBranch = statement();
        Statement elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        
        return new IfStatement(condition, thenBranch, elseBranch);
    }
    
    private Statement whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after while condition");
        Statement body = statement();
        
        return new WhileStatement(condition, body);
    }
    
    private Statement forStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'for'");
        
        Statement initializer;
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (isType(peek().getType())) {
            initializer = variableDeclaration();
        } else {
            initializer = expressionStatement();
        }
        
        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after loop condition");
        
        Expression increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Expected ')' after for clauses");
        
        Statement body = statement();
        
        return new ForStatement(initializer, condition, increment, body);
    }
    
    private Statement returnStatement() {
        Token keyword = previous();
        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after return value");
        return new ReturnStatement(keyword, value);
    }
    
    private Statement printStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'print'");
        Expression expr = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after print expression");
        consume(TokenType.SEMICOLON, "Expected ';' after print statement");
        
        return new PrintStatement(expr);
    }
    
    private BlockStatement blockStatement() {
        List<Statement> statements = new ArrayList<>();
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            if (match(TokenType.NEWLINE)) {
                continue;
            }
            statements.add(declaration());
        }
        
        consume(TokenType.RIGHT_BRACE, "Expected '}' after block");
        return new BlockStatement(statements);
    }
    
    private Statement expressionStatement() {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression");
        return new ExpressionStatement(expr);
    }
    
    private Expression expression() {
        return assignment();
    }
    
    private Expression assignment() {
        Expression expr = logicalOr();
        
        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Expression value = assignment();
            
            if (expr instanceof VariableExpression) {
                Token name = ((VariableExpression) expr).getName();
                return new AssignmentExpression(name, value);
            }
            
            errorHandler.reportError(CompilerError.ErrorType.SYNTAX_ERROR,
                    "Invalid assignment target", equals.getLine(), equals.getColumn());
        }
        
        return expr;
    }
    
    private Expression logicalOr() {
        Expression expr = logicalAnd();
        
        while (match(TokenType.LOGICAL_OR)) {
            Token operator = previous();
            Expression right = logicalAnd();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression logicalAnd() {
        Expression expr = equality();
        
        while (match(TokenType.LOGICAL_AND)) {
            Token operator = previous();
            Expression right = equality();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression equality() {
        Expression expr = comparison();
        
        while (match(TokenType.NOT_EQUAL, TokenType.EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression comparison() {
        Expression expr = term();
        
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL,
                     TokenType.LESS_THAN, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression term() {
        Expression expr = factor();
        
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression factor() {
        Expression expr = unary();
        
        while (match(TokenType.DIVIDE, TokenType.MULTIPLY, TokenType.MODULO)) {
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpression(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expression unary() {
        if (match(TokenType.LOGICAL_NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new UnaryExpression(operator, right);
        }
        
        return call();
    }
    
    private Expression call() {
        Expression expr = primary();
        
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        
        return expr;
    }
    
    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        
        Token paren = consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments");
        return new CallExpression(callee, paren, arguments);
    }
    
    private Expression primary() {
        if (match(TokenType.TRUE)) {
            return new LiteralExpression(true);
        }
        
        if (match(TokenType.FALSE)) {
            return new LiteralExpression(false);
        }
        
        if (match(TokenType.INTEGER_LITERAL)) {
            return new LiteralExpression(Integer.parseInt(previous().getValue()));
        }
        
        if (match(TokenType.FLOAT_LITERAL)) {
            return new LiteralExpression(Double.parseDouble(previous().getValue()));
        }
        
        if (match(TokenType.STRING_LITERAL)) {
            return new LiteralExpression(previous().getValue());
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpression(previous());
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            Expression expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression");
            return expr;
        }
        
        throw error(peek(), "Expected expression");
    }
    
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        
        return false;
    }
    
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        
        throw error(peek(), message);
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).getType() == type;
    }
    
    private boolean checkNextNext(TokenType type) {
        if (current + 2 >= tokens.size()) return false;
        return tokens.get(current + 2).getType() == type;
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private boolean isType(TokenType type) {
        return type == TokenType.INT || type == TokenType.FLOAT ||
               type == TokenType.BOOL || type == TokenType.STRING;
    }
    
    private ParseError error(Token token, String message) {
        errorHandler.reportError(CompilerError.ErrorType.SYNTAX_ERROR,
                message, token.getLine(), token.getColumn());
        return new ParseError();
    }
    
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
            
            switch (peek().getType()) {
                case IF:
                case FOR:
                case WHILE:
                case RETURN:
                case FUNCTION:
                case INT:
                case FLOAT:
                case BOOL:
                case STRING:
                    return;
            }
            
            advance();
        }
    }
    
    private static class ParseError extends RuntimeException {}
}