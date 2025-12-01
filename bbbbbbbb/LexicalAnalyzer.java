package com.tam.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    
    public static class Token {
        public final String type;
        public final String value;
        public final int line;
        
        public Token(String type, String value, int line) {
            this.type = type;
            this.value = value;
            this.line = line;
        }
    }
    
    public static class Result {
        public final boolean success;
        public final String message;
        public final List<Token> tokens;
        public final List<String> errors;
        
        public Result(boolean success, String message, List<Token> tokens, List<String> errors) {
            this.success = success;
            this.message = message;
            this.tokens = tokens;
            this.errors = errors;
        }
    }
    
    private final Pattern[] tokenPatterns = {
        Pattern.compile("^(int|double|float|char|boolean|byte|short|long|String)$"),
        Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$"),
        Pattern.compile("^-?\\d+(\\.\\d+)?[fFdD]?$"),
        Pattern.compile("^\"[^\"]*\"$"),
        Pattern.compile("^'.'$"),
        Pattern.compile("^=$"),
        Pattern.compile("^;$")
    };
    
    private final String[] tokenTypes = {
        "KEYWORD", "IDENTIFIER", "NUMBER", "STRING", "CHAR", "ASSIGNMENT", "SEMICOLON"
    };

    // REGEX EXPLANATION:
    // This pattern looks for tokens in a specific order:
    // Group 1: Strings ("example") - caught first so content inside isn't split
    // Group 2: Single characters ('a')
    // Group 3: Delimiters and Operators (= or ;)
    // Group 4: Numbers (123, 1.5) or Identifiers (variable names)
    private final Pattern tokenPattern = Pattern.compile(
            // Group 1: Strings ("example") - Catch these first so spaces/keywords inside aren't split
            "(\"[^\"]*\")" +
            // Group 2: Single characters ('a')
            "|('[^']')" +
            // Group 3: Delimiters (= or ;)
            "|(=|;)" +
            // Group 4: Numbers. Now optimized to catch suffixes like 1.5f or 100d
            // If we don't catch the suffix here, 'f' becomes an IDENTIFIER token (Error)
            "|(-?\\d+(?:\\.\\d+)?[fFdD]?|[a-zA-Z_][a-zA-Z0-9_]*)"
    );

    public Result analyze(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new Result(false, "There is no code open to analyze", new ArrayList<>(), new ArrayList<>());
        }

        List<Token> tokens = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        String[] lines = code.split("\n");

        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum].trim();
            if (line.isEmpty()) continue;

            java.util.regex.Matcher matcher = tokenPattern.matcher(line);

            // We use matcher.find() instead of string.split()
            // split() consumes delimiters (deleting '=' and ';'), making them impossible to detect
            // matcher.find() extracts them as distinct tokens without removal
            while (matcher.find()) {
                String tokenStr = matcher.group().trim();

                if (tokenStr.isEmpty()) continue;

                String type = identifyTokenType(tokenStr);

                if (type != null) {
                    tokens.add(new Token(type, tokenStr, lineNum + 1));
                } else {
                    errors.add("Line " + (lineNum + 1) + ": Invalid token '" + tokenStr + "'");
                }
            }
        }

        if (!errors.isEmpty()) {
            return new Result(false, "Lexical Analysis Failed!\n\n" + String.join("\n", errors), new ArrayList<>(), errors);
        }
        return new Result(true, "Lexical Analysis Passed!", tokens, new ArrayList<>());
    }

    private String identifyTokenType(String token) {
        for (int i = 0; i < tokenPatterns.length; i++) {
            if (tokenPatterns[i].matcher(token).matches()) {
                return tokenTypes[i];
            }
        }
        return null;
    }
}