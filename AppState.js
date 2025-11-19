export class AppState {
    constructor() {
        this.reset();
    }

    reset() {
        this.fileLoaded = false;
        this.lexicalPassed = false;
        this.syntaxPassed = false;
        this.semanticPassed = false;
        this.tokens = [];
        this.variables = {};
    }

    setFileLoaded(loaded) {
        this.fileLoaded = loaded;
    }

    setLexicalPassed(passed) {
        this.lexicalPassed = passed;
        if (passed) {
            // Clear subsequent stages if lexical analysis is re-run
            this.syntaxPassed = false;
            this.semanticPassed = false;
        }
    }

    setSyntaxPassed(passed) {
        this.syntaxPassed = passed;
        if (passed) {
            // Clear semantic stage if syntax analysis is re-run
            this.semanticPassed = false;
        }
    }

    setSemanticPassed(passed) {
        this.semanticPassed = passed;
    }

    setTokens(tokens) {
        this.tokens = tokens;
    }

    setVariables(variables) {
        this.variables = variables;
    }

    getTokens() {
        return this.tokens;
    }

    getVariables() {
        return this.variables;
    }
}