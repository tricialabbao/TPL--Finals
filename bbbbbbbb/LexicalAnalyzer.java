package com.tam.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    // Simple Token structure to hold our data
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

    // Result object to send data back to the UI
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

    // === REGEX PATTERNS FOR TOKEN CLASSIFICATION ===
    // We use these strictly to label a token AFTER we've extracted it.
    private final Pattern[] tokenPatterns = {
        Pattern.compile("^(int|double|float|char|boolean|byte|short|long|String)$"),
        Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$"),
        Pattern.compile("^-?\\d+(\\.\\d+)?[fFdDlL]?$"),
        Pattern.compile("^\"[^\"]*\"$"),
        Pattern.compile("^'.'$"),
        Pattern.compile("^=$"),
        Pattern.compile("^;$")
    };

    private final String[] tokenTypes = {
        "KEYWORD", "IDENTIFIER", "NUMBER", "STRING", "CHAR", "ASSIGNMENT", "SEMICOLON"
    };

    // === MAIN TOKEN EXTRACTION REGEX ===
    // This is the engine of the Lexer. The order is important
    // We match complex structures (like Strings) first so they aren't chopped up.
    private final Pattern tokenPattern = Pattern.compile(
            // Strings
            "(\"[^\"]*\")" +
            // Single characters ('a')
            "|('[^']')" +
            //Delimiters (= or ;)
            "|([=;])" +
            // Numbers (Integers, Decimals, with f/L suffixes)
            "|(-?\\d+(?:\\.\\d+)?[fFdDlL])?" +
            //Identifiers (Variable names or Keywords)
            "|([a-zA-Z_][a-zA-Z0-9_]*)" +
            //Matches any non-whitespace character (\S) that wasn't caught above
            "|(\\S)"
    );

    public Result analyze(String code) {
        // Safety check: Don't crash on empty input
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

                if (matcher.group(6) != null) {
                    errors.add("Line " + (lineNum + 1) + ": Unknown token '" + tokenStr + "'");
                    continue;
                }

                if (tokenStr.isEmpty()) continue;

                String type = identifyTokenType(tokenStr);

                if (type != null) {
                    tokens.add(new Token(type, tokenStr, lineNum + 1));
                } else {
                    errors.add("Line " + (lineNum + 1) + ": Invalid token '" + tokenStr + "'");
                }
            }
        }

        // If even one error exists, the whole analysis fails
        if (!errors.isEmpty()) {
            return new Result(false, "Lexical Analysis Failed!\n\n" + String.join("\n", errors), new ArrayList<>(), errors);
        }
        return new Result(true, "Lexical Analysis Passed!", tokens, new ArrayList<>());
    }

    // Helper: Matches the raw string against our specific definitions
    private String identifyTokenType(String token) {
        for (int i = 0; i < tokenPatterns.length; i++) {
            if (tokenPatterns[i].matcher(token).matches()) {
                return tokenTypes[i];
            }
        }
        return null;
    }
}