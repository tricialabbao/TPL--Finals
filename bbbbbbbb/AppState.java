package com.tam.compiler;

import java.util.List;
import java.util.Map;

public class AppState {
    private boolean fileLoaded = false;
    private boolean lexicalPassed = false;
    private boolean syntaxPassed = false;
    private boolean semanticPassed = false;
    private boolean hasError = false;
    private List<LexicalAnalyzer.Token> tokens;
    private Map<String, SemanticAnalyzer.VariableInfo> variables;

    public void reset() {
        fileLoaded = false;
        lexicalPassed = false;
        syntaxPassed = false;
        semanticPassed = false;
        hasError = false;
        tokens = null;
        variables = null;
    }

    // Getters and setters
    public boolean isFileLoaded() { return fileLoaded; }
    public void setFileLoaded(boolean fileLoaded) { this.fileLoaded = fileLoaded; }
    public boolean isLexicalPassed() { return lexicalPassed; }
    public void setLexicalPassed(boolean lexicalPassed) { 
        this.lexicalPassed = lexicalPassed;
        if (lexicalPassed) {
            this.syntaxPassed = false;
            this.semanticPassed = false;
        }
    }
    public boolean isSyntaxPassed() { return syntaxPassed; }
    public void setSyntaxPassed(boolean syntaxPassed) { 
        this.syntaxPassed = syntaxPassed;
        if (syntaxPassed) {
            this.semanticPassed = false;
        }
    }
    public boolean isSemanticPassed() { return semanticPassed; }
    public void setSemanticPassed(boolean semanticPassed) { this.semanticPassed = semanticPassed; }
    public List<LexicalAnalyzer.Token> getTokens() { return tokens; }
    public void setTokens(List<LexicalAnalyzer.Token> tokens) { this.tokens = tokens; }
    public Map<String, SemanticAnalyzer.VariableInfo> getVariables() { return variables; }
    public void setVariables(Map<String, SemanticAnalyzer.VariableInfo> variables) { this.variables = variables; }
    public boolean hasError() { return hasError; }
    public void setHasError(boolean hasError) { this.hasError = hasError; }
}