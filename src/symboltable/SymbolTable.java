package symboltable;

import token.TokenType;
import java.util.*;

/**
 * Symbol table for managing variable and function declarations with scope support
 */
public class SymbolTable {
    private final Map<String, Symbol> symbols;
    private final SymbolTable enclosing;
    private final int scopeLevel;
    
    public SymbolTable() {
        this.symbols = new HashMap<>();
        this.enclosing = null;
        this.scopeLevel = 0;
    }
    
    public SymbolTable(SymbolTable enclosing) {
        this.symbols = new HashMap<>();
        this.enclosing = enclosing;
        this.scopeLevel = enclosing.scopeLevel + 1;
    }
    
    public void define(String name, TokenType dataType, Symbol.SymbolType symbolType) {
        Symbol symbol = new Symbol(name, dataType, symbolType, scopeLevel);
        symbols.put(name, symbol);
    }
    
    public void define(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }
    
    public Symbol get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }
        
        if (enclosing != null) {
            return enclosing.get(name);
        }
        
        return null;
    }
    
    public boolean isDefined(String name) {
        return get(name) != null;
    }
    
    public boolean isDefinedInCurrentScope(String name) {
        return symbols.containsKey(name);
    }
    
    public void assign(String name, Object value) {
        if (symbols.containsKey(name)) {
            symbols.get(name).setValue(value);
            return;
        }
        
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        
        throw new RuntimeException("Undefined variable '" + name + "'");
    }
    
    public SymbolTable getEnclosing() {
        return enclosing;
    }
    
    public int getScopeLevel() {
        return scopeLevel;
    }
    
    public Set<String> getSymbolNames() {
        return new HashSet<>(symbols.keySet());
    }
    
    public Collection<Symbol> getSymbols() {
        return new ArrayList<>(symbols.values());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SymbolTable[scope=").append(scopeLevel).append("] {\n");
        for (Symbol symbol : symbols.values()) {
            sb.append("  ").append(symbol).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}