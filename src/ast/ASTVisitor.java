package ast;

/**
 * Visitor interface for traversing AST nodes
 * @param <R> The return type of visitor methods
 */
public interface ASTVisitor<R> {
    // Expressions
    R visitBinaryExpression(BinaryExpression expr);
    R visitUnaryExpression(UnaryExpression expr);
    R visitLiteralExpression(LiteralExpression expr);
    R visitVariableExpression(VariableExpression expr);
    R visitCallExpression(CallExpression expr);
    R visitAssignmentExpression(AssignmentExpression expr);
    
    // Statements
    R visitExpressionStatement(ExpressionStatement stmt);
    R visitVariableDeclaration(VariableDeclaration stmt);
    R visitBlockStatement(BlockStatement stmt);
    R visitIfStatement(IfStatement stmt);
    R visitWhileStatement(WhileStatement stmt);
    R visitForStatement(ForStatement stmt);
    R visitReturnStatement(ReturnStatement stmt);
    R visitFunctionDeclaration(FunctionDeclaration stmt);
    R visitPrintStatement(PrintStatement stmt);
    
    // Program
    R visitProgram(Program program);
}