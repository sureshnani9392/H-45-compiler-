package symboltable;

import token.TokenType;

/**
 * Represents a symbol in the symbol table
 */
public class Symbol {
    public enum SymbolType {
        VARIABLE,
        FUNCTION,
        PARAMETER
    }
    
    private final String name;
    private final TokenType dataType;
    private final SymbolType symbolType;
    private final int scopeLevel;
    private Object value;
    
    public Symbol(String name, TokenType dataType, SymbolType symbolType, int scopeLevel) {
        this.name = name;
        this.dataType = dataType;
        this.symbolType = symbolType;
        this.scopeLevel = scopeLevel;
        this.value = null;
    }
    
    public String getName() {
        return name;
    }
    
    public TokenType getDataType() {
        return dataType;
    }
    
    public SymbolType getSymbolType() {
        return symbolType;
    }
    
    public int getScopeLevel() {
        return scopeLevel;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("Symbol{name='%s', dataType=%s, symbolType=%s, scopeLevel=%d, value=%s}",
                           name, dataType, symbolType, scopeLevel, value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Symbol symbol = (Symbol) obj;
        return scopeLevel == symbol.scopeLevel &&
               name.equals(symbol.name) &&
               dataType == symbol.dataType &&
               symbolType == symbol.symbolType;
    }
    
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dataType.hashCode();
        result = 31 * result + symbolType.hashCode();
        result = 31 * result + scopeLevel;
        return result;
    }
}