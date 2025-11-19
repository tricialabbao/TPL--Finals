export class SemanticAnalyzer {
    constructor() {
        // Type validation patterns
        this.typeChecks = {
            int: /^-?\d+$/,
            byte: /^-?\d+$/,
            short: /^-?\d+$/,
            long: /^-?\d+$/,
            double: /^-?\d+(\.\d+)?$/,
            float: /^-?\d+(\.\d+)?$/,
            char: /^'.'$/,
            boolean: /^(true|false)$/,
            String: /^"[^"]*"$/
        };

        // Pattern to extract declaration parts
        this.declarationPattern = /^(int|double|float|char|boolean|byte|short|long|String)\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*=\s*(.+)$/;
    }

    analyze(code) {
        if (!code || !code.trim()) {
            return {
                success: false,
                message: 'No code to analyze',
                variables: {},
                errors: []
            };
        }

        const lines = code.split('\n').filter(line => line.trim());
        let errors = [];
        let variables = {};

        lines.forEach((line, lineNum) => {
            const cleanLine = line.replace(';', '').trim();
            const match = cleanLine.match(this.declarationPattern);

            if (!match) return;

            const [, type, name, value] = match;
            const trimmedValue = value.trim();

            // Check for duplicate variable declaration
            if (variables[name]) {
                errors.push(`Line ${lineNum + 1}: '${name}' already declared.`);
                return;
            }

            // Check type compatibility
            if (!this.isValidValueForType(type, trimmedValue)) {
                errors.push(`Line ${lineNum + 1}: Invalid value '${trimmedValue}' for type '${type}'`);
                return;
            }

            // Store variable information
            variables[name] = {
                type,
                value: trimmedValue,
                line: lineNum + 1
            };
        });

        if (errors.length > 0) {
            return {
                success: false,
                message: 'Semantic Analysis Failed!\n\n' + errors.join("\n"),
                variables: {},
                errors
            };
        }

        return {
            success: true,
            message: 'Semantic Analysis Passed!',
            variables,
            errors: []
        };
    }

    isValidValueForType(type, value) {
        const pattern = this.typeChecks[type];
        return pattern && pattern.test(value);
    }
}