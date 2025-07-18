package ast;

/**
 * Base interface for all Abstract Syntax Tree nodes
 */
public interface ASTNode {
    /**
     * Accept a visitor for traversing the AST
     * @param visitor The visitor to accept
     * @param <R> The return type of the visitor
     * @return The result of the visitor's visit method
     */
    <R> R accept(ASTVisitor<R> visitor);
}