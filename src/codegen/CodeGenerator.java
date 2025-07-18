package codegen;

import ast.*;
import token.TokenType;
import java.util.List;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Code generator for the H-45 language
 * Generates assembly-like intermediate code
 */
public class CodeGenerator implements ASTVisitor<Void> {
    private final PrintWriter output;
    private final StringWriter stringWriter;
    private int labelCounter = 0;
    private int tempCounter = 0;
    
    public CodeGenerator() {
        this.stringWriter = new StringWriter();
        this.output = new PrintWriter(stringWriter);
    }
    
    public CodeGenerator(PrintWriter output) {
        this.output = output;
        this.stringWriter = null;
    }
    
    public String generate(Program program) {
        output.println("; H-45 Compiler Generated Code");
        output.println("; Target: Assembly-like Intermediate Representation");
        output.println();
        
        program.accept(this);
        
        output.flush();
        if (stringWriter != null) {
            return stringWriter.toString();
        }
        return "";
    }
    
    @Override
    public Void visitProgram(Program program) {
        output.println("section .text");
        output.println("global _start");
        output.println();
        
        // Generate code for all statements
        for (Statement statement : program.getStatements()) {
            statement.accept(this);
        }
        
        // Add program exit
        output.println();
        output.println("_start:");
        output.println("    call main");
        output.println("    mov eax, 1      ; sys_exit");
        output.println("    mov ebx, 0      ; exit status");
        output.println("    int 0x80        ; call kernel");
        
        return null;
    }
    
    @Override
    public Void visitFunctionDeclaration(FunctionDeclaration stmt) {
        String functionName = stmt.getName().getValue();
        
        output.println();
        output.println(functionName + ":");
        output.println("    push ebp");
        output.println("    mov ebp, esp");
        
        // Reserve space for local variables (simplified)
        output.println("    sub esp, 64     ; reserve space for locals");
        
        // Generate function body
        stmt.getBody().accept(this);
        
        // Function epilogue
        output.println("    mov esp, ebp");
        output.println("    pop ebp");
        output.println("    ret");
        
        return null;
    }
    
    @Override
    public Void visitVariableDeclaration(VariableDeclaration stmt) {
        String varName = stmt.getName().getValue();
        
        output.println("    ; Variable declaration: " + varName);
        
        if (stmt.getInitializer() != null) {
            // Generate code for initializer
            stmt.getInitializer().accept(this);
            output.println("    mov [" + varName + "], eax    ; store initial value");
        }
        
        return null;
    }
    
    @Override
    public Void visitBlockStatement(BlockStatement stmt) {
        output.println("    ; Block start");
        
        for (Statement statement : stmt.getStatements()) {
            statement.accept(this);
        }
        
        output.println("    ; Block end");
        return null;
    }
    
    @Override
    public Void visitExpressionStatement(ExpressionStatement stmt) {
        stmt.getExpression().accept(this);
        return null;
    }
    
    @Override
    public Void visitIfStatement(IfStatement stmt) {
        String elseLabel = newLabel("else");
        String endLabel = newLabel("endif");
        
        output.println("    ; If statement");
        
        // Generate condition
        stmt.getCondition().accept(this);
        output.println("    cmp eax, 0");
        output.println("    je " + elseLabel);
        
        // Generate then branch
        stmt.getThenBranch().accept(this);
        output.println("    jmp " + endLabel);
        
        // Generate else branch
        output.println(elseLabel + ":");
        if (stmt.getElseBranch() != null) {
            stmt.getElseBranch().accept(this);
        }
        
        output.println(endLabel + ":");
        return null;
    }
    
    @Override
    public Void visitWhileStatement(WhileStatement stmt) {
        String loopLabel = newLabel("loop");
        String endLabel = newLabel("endloop");
        
        output.println("    ; While loop");
        output.println(loopLabel + ":");
        
        // Generate condition
        stmt.getCondition().accept(this);
        output.println("    cmp eax, 0");
        output.println("    je " + endLabel);
        
        // Generate body
        stmt.getBody().accept(this);
        output.println("    jmp " + loopLabel);
        
        output.println(endLabel + ":");
        return null;
    }
    
    @Override
    public Void visitForStatement(ForStatement stmt) {
        String loopLabel = newLabel("forloop");
        String endLabel = newLabel("endfor");
        
        output.println("    ; For loop");
        
        // Generate initializer
        if (stmt.getInitializer() != null) {
            stmt.getInitializer().accept(this);
        }
        
        output.println(loopLabel + ":");
        
        // Generate condition
        if (stmt.getCondition() != null) {
            stmt.getCondition().accept(this);
            output.println("    cmp eax, 0");
            output.println("    je " + endLabel);
        }
        
        // Generate body
        stmt.getBody().accept(this);
        
        // Generate increment
        if (stmt.getIncrement() != null) {
            stmt.getIncrement().accept(this);
        }
        
        output.println("    jmp " + loopLabel);
        output.println(endLabel + ":");
        return null;
    }
    
    @Override
    public Void visitReturnStatement(ReturnStatement stmt) {
        output.println("    ; Return statement");
        
        if (stmt.getValue() != null) {
            stmt.getValue().accept(this);
            // Result is in eax
        } else {
            output.println("    mov eax, 0      ; default return value");
        }
        
        output.println("    mov esp, ebp");
        output.println("    pop ebp");
        output.println("    ret");
        return null;
    }
    
    @Override
    public Void visitPrintStatement(PrintStatement stmt) {
        output.println("    ; Print statement");
        
        // Generate expression
        stmt.getExpression().accept(this);
        
        // Call print function (simplified)
        output.println("    push eax        ; push value to print");
        output.println("    call print_int  ; call print function");
        output.println("    add esp, 4      ; clean up stack");
        
        return null;
    }
    
    @Override
    public Void visitBinaryExpression(BinaryExpression expr) {
        // Generate left operand
        expr.getLeft().accept(this);
        output.println("    push eax        ; save left operand");
        
        // Generate right operand
        expr.getRight().accept(this);
        output.println("    mov ebx, eax    ; right operand in ebx");
        output.println("    pop eax         ; left operand in eax");
        
        TokenType operator = expr.getOperator().getType();
        
        switch (operator) {
            case PLUS:
                output.println("    add eax, ebx    ; addition");
                break;
            case MINUS:
                output.println("    sub eax, ebx    ; subtraction");
                break;
            case MULTIPLY:
                output.println("    imul eax, ebx   ; multiplication");
                break;
            case DIVIDE:
                output.println("    cdq             ; sign extend");
                output.println("    idiv ebx        ; division");
                break;
            case MODULO:
                output.println("    cdq             ; sign extend");
                output.println("    idiv ebx        ; division");
                output.println("    mov eax, edx    ; remainder in edx");
                break;
            case EQUAL:
                output.println("    cmp eax, ebx");
                output.println("    sete al");
                output.println("    movzx eax, al");
                break;
            case NOT_EQUAL:
                output.println("    cmp eax, ebx");
                output.println("    setne al");
                output.println("    movzx eax, al");
                break;
            case LESS_THAN:
                output.println("    cmp eax, ebx");
                output.println("    setl al");
                output.println("    movzx eax, al");
                break;
            case LESS_EQUAL:
                output.println("    cmp eax, ebx");
                output.println("    setle al");
                output.println("    movzx eax, al");
                break;
            case GREATER_THAN:
                output.println("    cmp eax, ebx");
                output.println("    setg al");
                output.println("    movzx eax, al");
                break;
            case GREATER_EQUAL:
                output.println("    cmp eax, ebx");
                output.println("    setge al");
                output.println("    movzx eax, al");
                break;
            case LOGICAL_AND:
                output.println("    and eax, ebx    ; logical and");
                break;
            case LOGICAL_OR:
                output.println("    or eax, ebx     ; logical or");
                break;
        }
        
        return null;
    }
    
    @Override
    public Void visitUnaryExpression(UnaryExpression expr) {
        expr.getOperand().accept(this);
        
        TokenType operator = expr.getOperator().getType();
        
        switch (operator) {
            case MINUS:
                output.println("    neg eax         ; negate");
                break;
            case LOGICAL_NOT:
                output.println("    cmp eax, 0");
                output.println("    sete al");
                output.println("    movzx eax, al");
                break;
        }
        
        return null;
    }
    
    @Override
    public Void visitLiteralExpression(LiteralExpression expr) {
        Object value = expr.getValue();
        
        if (value instanceof Integer) {
            output.println("    mov eax, " + value + "    ; integer literal");
        } else if (value instanceof Double) {
            output.println("    ; Float literal (simplified as integer)");
            output.println("    mov eax, " + ((Double) value).intValue());
        } else if (value instanceof Boolean) {
            int boolValue = (Boolean) value ? 1 : 0;
            output.println("    mov eax, " + boolValue + "    ; boolean literal");
        } else if (value instanceof String) {
            String stringLabel = newLabel("str");
            output.println("    mov eax, " + stringLabel + "    ; string literal");
            // In a real compiler, we'd add string to data section
        }
        
        return null;
    }
    
    @Override
    public Void visitVariableExpression(VariableExpression expr) {
        String varName = expr.getName().getValue();
        output.println("    mov eax, [" + varName + "]    ; load variable " + varName);
        return null;
    }
    
    @Override
    public Void visitAssignmentExpression(AssignmentExpression expr) {
        String varName = expr.getName().getValue();
        
        // Generate value expression
        expr.getValue().accept(this);
        
        // Store in variable
        output.println("    mov [" + varName + "], eax    ; assign to " + varName);
        
        return null;
    }
    
    @Override
    public Void visitCallExpression(CallExpression expr) {
        if (!(expr.getCallee() instanceof VariableExpression)) {
            return null;
        }
        
        String functionName = ((VariableExpression) expr.getCallee()).getName().getValue();
        
        output.println("    ; Function call: " + functionName);
        
        // Push arguments in reverse order
        List<Expression> args = expr.getArguments();
        for (int i = args.size() - 1; i >= 0; i--) {
            args.get(i).accept(this);
            output.println("    push eax        ; push argument " + i);
        }
        
        // Call function
        output.println("    call " + functionName);
        
        // Clean up stack
        if (!args.isEmpty()) {
            output.println("    add esp, " + (args.size() * 4) + "    ; clean up arguments");
        }
        
        return null;
    }
    
    private String newLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }
    
    private String newTemp() {
        return "temp" + (tempCounter++);
    }
}