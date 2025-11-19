export class SyntaxAnalyzer {
    constructor() {
        // Pattern for valid variable declaration
        this.declarationPattern = /^(int|double|float|char|boolean|byte|short|long|String)\s+[a-zA-Z_][a-zA-Z0-9_]*\s*=\s*(.+)$/;
    }

    analyze(code) {
        if (!code || !code.trim()) {
            return {
                success: false,
                message: 'No code to analyze',
                errors: []
            };
        }

        const lines = code.split('\n').filter(line => line.trim());
        let errors = [];

        lines.forEach((line, lineNum) => {
            line = line.trim();

            // Check for semicolon at the end
            if (!line.endsWith(';')) {
                errors.push(`Line ${lineNum + 1}: Missing semicolon`);
                return;
            }

            // Remove semicolon and check declaration pattern
            const cleanLine = line.slice(0, -1).trim();

            if (!this.declarationPattern.test(cleanLine)) {
                errors.push(`Line ${lineNum + 1}: Invalid declaration syntax`);
            }
        });

        if (errors.length > 0) {
            return {
                success: false,
                message: 'Syntax Analysis Failed\n\n' + errors.join("\n"),
                errors
            };
        }

        return {
            success: true,
            message: 'Syntax Analysis Passed!',
            errors: []
        };
    }
}