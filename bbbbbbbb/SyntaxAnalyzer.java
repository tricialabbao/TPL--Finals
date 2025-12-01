package com.tam.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SyntaxAnalyzer {
    
    public static class Result {
        public final boolean success;
        public final String message;
        public final List<String> errors;
        
        public Result(boolean success, String message, List<String> errors) {
            this.success = success;
            this.message = message;
            this.errors = errors;
        }
    }

    // Pattern to enforce "Type Name = Value" structure
    private final Pattern declarationPattern = 
        Pattern.compile("^(int|double|float|char|boolean|byte|short|long|String)\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*(.+)$");

    public Result analyze(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new Result(false, "No code to analyze", new ArrayList<>());
        }

        String[] lines = code.split("\n");
        List<String> errors = new ArrayList<>();

        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum].trim();
            if (line.isEmpty()) continue;

            // Semicolon Check
            // Every statement in Java must end with a semicolon.
            if (!line.endsWith(";")) {
                errors.add("Line " + (lineNum + 1) + ": Missing semicolon");
                continue;
            }

            // Remove semicolon to check the rest of the syntax
            String cleanLine = line.substring(0, line.length() - 1).trim();

            //Structure Check
            // Does the line follow strict "Type Name = Value" syntax?
            if (!declarationPattern.matcher(cleanLine).matches()) {
                errors.add("Line " + (lineNum + 1) + ": Invalid declaration syntax");
            }
        }

        // Fail if any errors were found
        if (!errors.isEmpty()) {
            return new Result(false, "Syntax Analysis Failed\n\n" + String.join("\n", errors), errors);
        }

        return new Result(true, "Syntax Analysis Passed!", new ArrayList<>());
    }
}