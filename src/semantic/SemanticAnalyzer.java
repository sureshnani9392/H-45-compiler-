package semantic;

import ast.*;
import token.Token;
import token.TokenType;
import symboltable.Symbol;
import symboltable.SymbolTable;
import error.ErrorHandler;
import error.CompilerError;

import java.util.List;

/**
 * Semantic analyzer for the H-45 language
 * Performs type checking and symbol resolution
 */
public class SemanticAnalyzer implements ASTVisitor<TokenType> {
    private final ErrorHandler errorHandler;
    private SymbolTable currentScope;
    private boolean inFunction = false;
    private TokenType expectedReturnType = null;
    
    public SemanticAnalyzer(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.currentScope = new SymbolTable();
    }
    
    public void analyze(Program program) {
        program.accept(this);
    }
    
    @Override
    public TokenType visitProgram(Program program) {
        for (Statement statement : program.getStatements()) {
            statement.accept(this);
        }
        return null;
    }
    
    @Override
    public TokenType visitFunctionDeclaration(FunctionDeclaration stmt) {
        String functionName = stmt.getName().getValue();
        
        // Check if function is already declared
        if (currentScope.isDefinedInCurrentScope(functionName)) {
            errorHandler.reportError(CompilerError.ErrorType.REDECLARATION_ERROR,
                    "Function '" + functionName + "' is already declared",
                    stmt.getName().getLine(), stmt.getName().getColumn());
            return null;
        }
        
        // Define function in current scope
        currentScope.define(functionName, stmt.getReturnType().getType(), Symbol.SymbolType.FUNCTION);
        
        // Enter function scope
        SymbolTable previous = currentScope;
        currentScope = new SymbolTable(currentScope);
        boolean wasInFunction = inFunction;
        TokenType previousReturnType = expectedReturnType;
        
        inFunction = true;
        expectedReturnType = stmt.getReturnType().getType();
        
        // Add parameters to function scope
        for (FunctionDeclaration.Parameter param : stmt.getParameters()) {
            String paramName = param.getName().getValue();
            if (currentScope.isDefinedInCurrentScope(paramName)) {
                errorHandler.reportError(CompilerError.ErrorType.REDECLARATION_ERROR,
                        "Parameter '" + paramName + "' is already declared",
                        param.getName().getLine(), param.getName().getColumn());
            } else {
                currentScope.define(paramName, param.getType().getType(), Symbol.SymbolType.PARAMETER);
            }
        }
        
        // Analyze function body
        stmt.getBody().accept(this);
        
        // Restore previous scope
        currentScope = previous;
        inFunction = wasInFunction;
        expectedReturnType = previousReturnType;
        
        return stmt.getReturnType().getType();
    }
    
    @Override
    public TokenType visitVariableDeclaration(VariableDeclaration stmt) {
        String varName = stmt.getName().getValue();
        
        // Check if variable is already declared in current scope
        if (currentScope.isDefinedInCurrentScope(varName)) {
            errorHandler.reportError(CompilerError.ErrorType.REDECLARATION_ERROR,
                    "Variable '" + varName + "' is already declared",
                    stmt.getName().getLine(), stmt.getName().getColumn());
            return null;
        }
        
        TokenType declaredType = stmt.getType().getType();
        
        // Check initializer type if present
        if (stmt.getInitializer() != null) {
            TokenType initType = stmt.getInitializer().accept(this);
            if (initType != null && !isCompatible(declaredType, initType)) {
                errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                        "Cannot assign " + initType + " to variable of type " + declaredType,
                        stmt.getName().getLine(), stmt.getName().getColumn());
            }
        }
        
        // Define variable in current scope
        currentScope.define(varName, declaredType, Symbol.SymbolType.VARIABLE);
        return declaredType;
    }
    
    @Override
    public TokenType visitBlockStatement(BlockStatement stmt) {
        // Enter new scope
        SymbolTable previous = currentScope;
        currentScope = new SymbolTable(currentScope);
        
        // Analyze statements in block
        for (Statement statement : stmt.getStatements()) {
            statement.accept(this);
        }
        
        // Restore previous scope
        currentScope = previous;
        return null;
    }
    
    @Override
    public TokenType visitExpressionStatement(ExpressionStatement stmt) {
        return stmt.getExpression().accept(this);
    }
    
    @Override
    public TokenType visitIfStatement(IfStatement stmt) {
        TokenType conditionType = stmt.getCondition().accept(this);
        if (conditionType != null && conditionType != TokenType.BOOL) {
            errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                    "If condition must be boolean, got " + conditionType,
                    -1, -1); // TODO: Get actual position from AST
        }
        
        stmt.getThenBranch().accept(this);
        if (stmt.getElseBranch() != null) {
            stmt.getElseBranch().accept(this);
        }
        
        return null;
    }
    
    @Override
    public TokenType visitWhileStatement(WhileStatement stmt) {
        TokenType conditionType = stmt.getCondition().accept(this);
        if (conditionType != null && conditionType != TokenType.BOOL) {
            errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                    "While condition must be boolean, got " + conditionType,
                    -1, -1);
        }
        
        stmt.getBody().accept(this);
        return null;
    }
    
    @Override
    public TokenType visitForStatement(ForStatement stmt) {
        // Enter new scope for for loop
        SymbolTable previous = currentScope;
        currentScope = new SymbolTable(currentScope);
        
        if (stmt.getInitializer() != null) {
            stmt.getInitializer().accept(this);
        }
        
        if (stmt.getCondition() != null) {
            TokenType conditionType = stmt.getCondition().accept(this);
            if (conditionType != null && conditionType != TokenType.BOOL) {
                errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                        "For condition must be boolean, got " + conditionType,
                        -1, -1);
            }
        }
        
        if (stmt.getIncrement() != null) {
            stmt.getIncrement().accept(this);
        }
        
        stmt.getBody().accept(this);
        
        // Restore previous scope
        currentScope = previous;
        return null;
    }
    
    @Override
    public TokenType visitReturnStatement(ReturnStatement stmt) {
        if (!inFunction) {
            errorHandler.reportError(CompilerError.ErrorType.SEMANTIC_ERROR,
                    "Return statement outside function",
                    stmt.getKeyword().getLine(), stmt.getKeyword().getColumn());
            return null;
        }
        
        if (stmt.getValue() != null) {
            TokenType returnType = stmt.getValue().accept(this);
            if (returnType != null && expectedReturnType != null && 
                !isCompatible(expectedReturnType, returnType)) {
                errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                        "Cannot return " + returnType + " from function expecting " + expectedReturnType,
                        stmt.getKeyword().getLine(), stmt.getKeyword().getColumn());
            }
        } else if (expectedReturnType != null && expectedReturnType != TokenType.IDENTIFIER) {
            errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                    "Function must return a value of type " + expectedReturnType,
                    stmt.getKeyword().getLine(), stmt.getKeyword().getColumn());
        }
        
        return expectedReturnType;
    }
    
    @Override
    public TokenType visitPrintStatement(PrintStatement stmt) {
        stmt.getExpression().accept(this);
        return null;
    }
    
    @Override
    public TokenType visitBinaryExpression(BinaryExpression expr) {
        TokenType leftType = expr.getLeft().accept(this);
        TokenType rightType = expr.getRight().accept(this);
        
        if (leftType == null || rightType == null) {
            return null;
        }
        
        TokenType operatorType = expr.getOperator().getType();
        
        switch (operatorType) {
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case MODULO:
                if (isNumeric(leftType) && isNumeric(rightType)) {
                    return promoteNumericType(leftType, rightType);
                }
                break;
            
            case EQUAL:
            case NOT_EQUAL:
                if (isCompatible(leftType, rightType)) {
                    return TokenType.BOOL;
                }
                break;
            
            case LESS_THAN:
            case LESS_EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
                if (isNumeric(leftType) && isNumeric(rightType)) {
                    return TokenType.BOOL;
                }
                break;
            
            case LOGICAL_AND:
            case LOGICAL_OR:
                if (leftType == TokenType.BOOL && rightType == TokenType.BOOL) {
                    return TokenType.BOOL;
                }
                break;
        }
        
        errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                "Invalid operand types for " + operatorType + ": " + leftType + " and " + rightType,
                expr.getOperator().getLine(), expr.getOperator().getColumn());
        return null;
    }
    
    @Override
    public TokenType visitUnaryExpression(UnaryExpression expr) {
        TokenType operandType = expr.getOperand().accept(this);
        if (operandType == null) return null;
        
        TokenType operatorType = expr.getOperator().getType();
        
        switch (operatorType) {
            case MINUS:
                if (isNumeric(operandType)) {
                    return operandType;
                }
                break;
            case LOGICAL_NOT:
                if (operandType == TokenType.BOOL) {
                    return TokenType.BOOL;
                }
                break;
        }
        
        errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                "Invalid operand type for " + operatorType + ": " + operandType,
                expr.getOperator().getLine(), expr.getOperator().getColumn());
        return null;
    }
    
    @Override
    public TokenType visitLiteralExpression(LiteralExpression expr) {
        Object value = expr.getValue();
        if (value instanceof Integer) return TokenType.INT;
        if (value instanceof Double) return TokenType.FLOAT;
        if (value instanceof String) return TokenType.STRING;
        if (value instanceof Boolean) return TokenType.BOOL;
        return null;
    }
    
    @Override
    public TokenType visitVariableExpression(VariableExpression expr) {
        String varName = expr.getName().getValue();
        Symbol symbol = currentScope.get(varName);
        
        if (symbol == null) {
            errorHandler.reportError(CompilerError.ErrorType.UNDECLARED_VARIABLE,
                    "Undefined variable '" + varName + "'",
                    expr.getName().getLine(), expr.getName().getColumn());
            return null;
        }
        
        return symbol.getDataType();
    }
    
    @Override
    public TokenType visitAssignmentExpression(AssignmentExpression expr) {
        String varName = expr.getName().getValue();
        Symbol symbol = currentScope.get(varName);
        
        if (symbol == null) {
            errorHandler.reportError(CompilerError.ErrorType.UNDECLARED_VARIABLE,
                    "Undefined variable '" + varName + "'",
                    expr.getName().getLine(), expr.getName().getColumn());
            return null;
        }
        
        TokenType valueType = expr.getValue().accept(this);
        if (valueType != null && !isCompatible(symbol.getDataType(), valueType)) {
            errorHandler.reportError(CompilerError.ErrorType.TYPE_ERROR,
                    "Cannot assign " + valueType + " to variable of type " + symbol.getDataType(),
                    expr.getName().getLine(), expr.getName().getColumn());
        }
        
        return symbol.getDataType();
    }
    
    @Override
    public TokenType visitCallExpression(CallExpression expr) {
        if (!(expr.getCallee() instanceof VariableExpression)) {
            errorHandler.reportError(CompilerError.ErrorType.SEMANTIC_ERROR,
                    "Only functions can be called",
                    expr.getParen().getLine(), expr.getParen().getColumn());
            return null;
        }
        
        String functionName = ((VariableExpression) expr.getCallee()).getName().getValue();
        Symbol function = currentScope.get(functionName);
        
        if (function == null) {
            errorHandler.reportError(CompilerError.ErrorType.FUNCTION_NOT_FOUND,
                    "Undefined function '" + functionName + "'",
                    expr.getParen().getLine(), expr.getParen().getColumn());
            return null;
        }
        
        if (function.getSymbolType() != Symbol.SymbolType.FUNCTION) {
            errorHandler.reportError(CompilerError.ErrorType.SEMANTIC_ERROR,
                    "'" + functionName + "' is not a function",
                    expr.getParen().getLine(), expr.getParen().getColumn());
            return null;
        }
        
        // Check argument types (simplified - in a real compiler, we'd store parameter info)
        for (Expression arg : expr.getArguments()) {
            arg.accept(this);
        }
        
        return function.getDataType();
    }
    
    private boolean isNumeric(TokenType type) {
        return type == TokenType.INT || type == TokenType.FLOAT;
    }
    
    private boolean isCompatible(TokenType expected, TokenType actual) {
        if (expected == actual) return true;
        
        // Allow int to float promotion
        if (expected == TokenType.FLOAT && actual == TokenType.INT) return true;
        
        return false;
    }
    
    private TokenType promoteNumericType(TokenType left, TokenType right) {
        if (left == TokenType.FLOAT || right == TokenType.FLOAT) {
            return TokenType.FLOAT;
        }
        return TokenType.INT;
    }
}