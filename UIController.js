/**
 * UIController - Enhanced Modern UI Management
 */
export class UIController {
    constructor() {
        this.elements = {
            codeArea: document.getElementById('codeArea'),
            resultArea: document.getElementById('resultArea'), // now <div class="console">
            fileInput: document.getElementById('fileInput'),
            openFileBtn: document.getElementById('openFileBtn'),
            lexicalBtn: document.querySelector('[data-stage="lexical"]'),
            syntaxBtn: document.querySelector('[data-stage="syntax"]'),
            semanticBtn: document.querySelector('[data-stage="semantic"]'),
            clearBtn: document.getElementById('clearBtn'),
            lineNumbers: document.getElementById('lineNumbers'),
            lineCount: document.getElementById('lineCount'),
            charCount: document.getElementById('charCount'),
            consoleBadge: document.getElementById('consoleBadge')
        };

        // Status elements
        this.statusElements = {
            lexical: document.getElementById('lexicalStatus'),
            syntax: document.getElementById('syntaxStatus'),
            semantic: document.getElementById('semanticStatus')
        };

        this.initEditorSync();
        this.updateLineNumbers();
    }

    // Sync scrolling and update line numbers
    initEditorSync() {
        const textarea = this.elements.codeArea;
        const lineNumbers = this.elements.lineNumbers;

        const syncScroll = () => {
            lineNumbers.scrollTop = textarea.scrollTop;
        };

        const updateOnInput = () => {
            this.updateLineNumbers();
            syncScroll();
        };

        textarea.addEventListener('scroll', syncScroll);
        textarea.addEventListener('input', updateOnInput);
        textarea.addEventListener('keydown', updateOnInput);
    }

    updateLineNumbers() {
        const code = this.getCodeText();
        const lineCount = code.split('\n').length;
        const linesHTML = Array.from({ length: lineCount }, (_, i) => i + 1).join('\n');
        this.elements.lineNumbers.textContent = linesHTML || '1';

        this.elements.lineCount.textContent = `${lineCount} line${lineCount !== 1 ? 's' : ''}`;
        this.elements.charCount.textContent = `${code.length} char${code.length !== 1 ? 's' : ''}`;
    }

    getCodeText() {
        return this.elements.codeArea.value;
    }

    setCodeText(text) {
        this.elements.codeArea.value = text;
        this.updateLineNumbers();
    }

    setResultText(text, isError = false) {
        const console = this.elements.resultArea;
        console.textContent = text;

        // Update badge
        if (isError) {
            this.elements.consoleBadge.textContent = 'Failed';
            this.elements.consoleBadge.className = 'badge badge-error';
        } else if (text.includes('Passed') || text.includes('successfully')) {
            this.elements.consoleBadge.textContent = 'Success';
            this.elements.consoleBadge.className = 'badge badge-success';
        } else if (text.includes('Analyzing') || text.includes('Performing')) {
            this.elements.consoleBadge.textContent = 'Running';
            this.elements.consoleBadge.className = 'badge badge-running';
        } else {
            this.elements.consoleBadge.textContent = 'Ready';
            this.elements.consoleBadge.className = 'badge badge-ready';
        }
    }

    clearAll() {
        this.elements.codeArea.value = '';
        this.setResultText('Welcome! Load a Java file to start compilation analysis.');
        this.elements.fileInput.value = '';
        this.updateLineNumbers();

        // Reset all stages
        Object.values(this.statusElements).forEach(el => {
            el.innerHTML = '<i class="fas fa-hourglass-half"></i>';
        });
        document.querySelectorAll('.stage').forEach(stage => {
            stage.classList.remove('active', 'success', 'error');
        });
    }

    // Visual feedback for each stage
    setStageStatus(stage, status) {
        const stageEl = document.querySelector(`[data-stage="${stage}"]`);
        const iconEl = this.statusElements[stage];

        // Remove previous states
        stageEl.classList.remove('active', 'success', 'error');

        if (status === 'running') {
            stageEl.classList.add('active');
            iconEl.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
        } else if (status === 'success') {
            stageEl.classList.add('success');
            iconEl.innerHTML = '<i class="fas fa-check-circle"></i>';
        } else if (status === 'error') {
            stageEl.classList.add('error');
            iconEl.innerHTML = '<i class="fas fa-times-circle"></i>';
        } else {
            iconEl.innerHTML = '<i class="fas fa-hourglass-half"></i>';
        }
    }

    updateButtonStates(state) {
        // Enable/disable stages based on progress
        const lexicalStage = document.querySelector('[data-stage="lexical"]');
        const syntaxStage = document.querySelector('[data-stage="syntax"]');
        const semanticStage = document.querySelector('[data-stage="semantic"]');

        // Lexical
        if (state.fileLoaded) {
            lexicalStage.style.cursor = 'pointer';
            lexicalStage.onclick = () => this.handlers?.onLexicalAnalysis?.();
        } else {
            lexicalStage.style.cursor = 'not-allowed';
            lexicalStage.onclick = null;
        }

        // Syntax
        if (state.lexicalPassed) {
            syntaxStage.style.cursor = 'pointer';
            syntaxStage.onclick = () => this.handlers?.onSyntaxAnalysis?.();
        } else {
            syntaxStage.style.cursor = 'not-allowed';
            syntaxStage.onclick = null;
        }

        // Semantic
        if (state.syntaxPassed) {
            semanticStage.style.cursor = 'pointer';
            semanticStage.onclick = () => this.handlers?.onSemanticAnalysis?.();
        } else {
            semanticStage.style.cursor = 'not-allowed';
            semanticStage.onclick = null;
        }

        // Clear button always enabled when something is loaded
        this.elements.clearBtn.disabled = !state.fileLoaded;
    }

    attachEventHandlers(handlers) {
        this.handlers = handlers;

        // File upload
        this.elements.openFileBtn.addEventListener('click', () => {
            this.elements.fileInput.click();
        });

        this.elements.fileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) handlers.onFileLoad(file);
        });

        // Stage clicks
        document.querySelector('[data-stage="lexical"]').addEventListener('click', () => {
            if (this.elements.codeArea.value.trim()) handlers.onLexicalAnalysis();
        });
        document.querySelector('[data-stage="syntax"]').addEventListener('click', () => {
            if (this.elements.codeArea.value.trim()) handlers.onSyntaxAnalysis();
        });
        document.querySelector('[data-stage="semantic"]').addEventListener('click', () => {
            if (this.elements.codeArea.value.trim()) handlers.onSemanticAnalysis();
        });

        // Clear
        this.elements.clearBtn.addEventListener('click', handlers.onClear);

        // Dark mode toggle
        document.getElementById('themeToggle').addEventListener('click', () => {
            document.body.classList.toggle('dark');
            const icon = document.querySelector('#themeToggle i');
            icon.classList.toggle('fa-moon');
            icon.classList.toggle('fa-sun');
        });
    }
}