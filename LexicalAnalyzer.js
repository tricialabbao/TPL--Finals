// Lexical Analysis
export class LexicalAnalyzer {
    constructor() {
        this.tokenPatterns = {
            KEYWORD: /^(int|double|float|char|boolean|byte|short|long|String)$/,
            IDENTIFIER: /^[a-zA-Z_][a-zA-Z0-9_]*$/,
            NUMBER: /^-?\d+(\.\d+)?$/,
            STRING: /^"[^"]*"$/,
            CHAR: /^'.'$/,
            ASSIGNMENT: /^=$/,
            SEMICOLON: /^;$/
        };
    }

    analyze(code) {
        if (!code || !code.trim()) {
            return {
                success: false,
                message: 'There is no code open to analyze',
                tokens: [],
                errors: []
            };
        }

        const lines = code.split('\n');
        let tokens = [];
        let errors = [];

        lines.forEach((line, lineNum) => {
            line = line.trim();
            if (!line) return;

            const lineTokens = line.split(/(\s+|=|;)/).filter(t => t.trim());

            lineTokens.forEach(token => {
                const type = this.identifyTokenType(token);
                
                if (type) {
                    tokens.push({ 
                        type, 
                        value: token, 
                        line: lineNum + 1 
                    });
                } else {
                    errors.push(`Line ${lineNum + 1}: Invalid token '${token}'`);
                }
            });
        });

        if (errors.length > 0) {
            return {
                success: false,
                message: 'Lexical Analysis Failed!\n\n' + errors.join("\n"),
                tokens: [],
                errors
            };
        }

        return {
            success: true,
            message: 'Lexical Analysis Passed!',
            tokens,
            errors: []
        };
    }

    identifyTokenType(token) {
        for (let [type, pattern] of Object.entries(this.tokenPatterns)) {
            if (pattern.test(token)) {
                return type;
            }
        }
        return null;
    }
}