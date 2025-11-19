import { AppState } from './AppState.js';
import { UIController } from './UIController.js';
import { LexicalAnalyzer } from './LexicalAnalyzer.js';
import { SyntaxAnalyzer } from './SyntaxAnalyzer.js';
import { SemanticAnalyzer } from './SemanticAnalyzer.js';

class CompilerApp {
    constructor() {
        this.state = new AppState();
        this.ui = new UIController();
        this.lexicalAnalyzer = new LexicalAnalyzer();
        this.syntaxAnalyzer = new SyntaxAnalyzer();
        this.semanticAnalyzer = new SemanticAnalyzer();

        this.init();
    }

    init() {
        this.ui.attachEventHandlers({
            onFileLoad: (file) => this.handleFileLoad(file),
            onLexicalAnalysis: () => this.handleLexicalAnalysis(),
            onSyntaxAnalysis: () => this.handleSyntaxAnalysis(),
            onSemanticAnalysis: () => this.handleSemanticAnalysis(),
            onClear: () => this.handleClear()
        });

        this.ui.updateButtonStates(this.state);
    }

    handleFileLoad(file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            this.ui.setCodeText(e.target.result);
            this.state.setFileLoaded(true);
            this.ui.setResultText('File loaded successfully!\n\nClick "Lexical Analysis" to begin.');
            this.ui.updateButtonStates(this.state);
        };
        reader.onerror = () => {
            this.ui.setResultText('ERROR: Failed to read file!', true);
        };
        reader.readAsText(file);
    }

    handleLexicalAnalysis() {
        const code = this.ui.getCodeText();
        this.ui.setResultText('Performing Lexical Analysis\n\n');
        this.ui.setStageStatus('lexical', 'running');

        const result = this.lexicalAnalyzer.analyze(code);

        if (result.success) {
            this.state.setLexicalPassed(true);
            this.state.setTokens(result.tokens);
            this.ui.setStageStatus('lexical', 'success');
        } else {
            this.state.setLexicalPassed(false);
            this.ui.setStageStatus('lexical', 'error');
        }

        this.ui.setResultText(result.message, !result.success);
        this.ui.updateButtonStates(this.state);
    }

    handleSyntaxAnalysis() {
        const code = this.ui.getCodeText();
        this.ui.setResultText('Performing Syntax Analysis\n\n');
        this.ui.setStageStatus('syntax', 'running');

        const result = this.syntaxAnalyzer.analyze(code);

        if (result.success) {
            this.state.setSyntaxPassed(true);
            this.ui.setStageStatus('syntax', 'success');
        } else {
            this.state.setSyntaxPassed(false);
            this.ui.setStageStatus('syntax', 'error');
        }

        this.ui.setResultText(result.message, !result.success);
        this.ui.updateButtonStates(this.state);
    }

    handleSemanticAnalysis() {
        const code = this.ui.getCodeText();
        this.ui.setResultText('Performing Semantic Analysis\n\n');
        this.ui.setStageStatus('semantic', 'running');

        const result = this.semanticAnalyzer.analyze(code);

        if (result.success) {
            this.state.setSemanticPassed(true);
            this.state.setVariables(result.variables);
            this.ui.setStageStatus('semantic', 'success');
        } else {
            this.state.setSemanticPassed(false);
            this.ui.setStageStatus('semantic', 'error');
        }

        this.ui.setResultText(result.message, !result.success);
        this.ui.updateButtonStates(this.state);
    }

    handleClear() {
        this.ui.clearAll();
        this.state.reset();
        this.ui.updateButtonStates(this.state);
    }
}

// Start the app
document.addEventListener('DOMContentLoaded', () => {
    new CompilerApp();
});