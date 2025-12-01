package com.tam.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SemanticAnalyzer {

    // Store info about declared variables to check for duplicates
    public static class VariableInfo {
        public final String type;
        public final String value;
        public final int line;
        
        public VariableInfo(String type, String value, int line) {
            this.type = type;
            this.value = value;
            this.line = line;
        }
    }
    
    public static class Result {
        public final boolean success;
        public final String message;
        public final Map<String, VariableInfo> variables;
        public final List<String> errors;
        
        public Result(boolean success, String message, Map<String, VariableInfo> variables, List<String> errors) {
            this.success = success;
            this.message = message;
            this.variables = variables;
            this.errors = errors;
        }
    }
    
    private final Map<String, Pattern> typeChecks = new HashMap<>();

    // Regex to break down a line into: TYPE | NAME | VALUE
    private final Pattern declarationPattern = 
        Pattern.compile("^(int|double|float|char|boolean|byte|short|long|String)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)$");

    public SemanticAnalyzer() {
        // === INITIALIZE TYPE RULES ===

        // Integers: Whole numbers (pos/neg)
        typeChecks.put("int", Pattern.compile("^-?\\d+$"));
        typeChecks.put("byte", Pattern.compile("^-?\\d+$"));
        typeChecks.put("short", Pattern.compile("^-?\\d+$"));

        // Longs: Whole numbers, optionally ending with 'l' or 'L'
        typeChecks.put("long", Pattern.compile("^-?\\d+[lL]?$"));

        // Doubles: Decimals, optionally ending with 'd'
        typeChecks.put("double", Pattern.compile("^-?\\d+(\\.\\d+)?[dD]?$"));

        // Floats: Must have 'f' suffix (1.5f) OR be a whole number (implicit cast)
        typeChecks.put("float", Pattern.compile("^-?\\d+(\\.\\d+)?[fF]$|^-?\\d+$"));

        // Chars: Single character in single quotes
        typeChecks.put("char", Pattern.compile("^'.'$"));

        // Booleans: strict true or false
        typeChecks.put("boolean", Pattern.compile("^(true|false)$"));

        // Strings: anything inside double quotes
        typeChecks.put("String", Pattern.compile("^\"[^\"]*\"$"));
    }

    public Result analyze(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new Result(false, "No code to analyze", new HashMap<>(), new ArrayList<>());
        }

        String[] lines = code.split("\n");
        List<String> errors = new ArrayList<>();
        Map<String, VariableInfo> variables = new HashMap<>();

        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum].trim();
            if (line.isEmpty()) continue;

            // We only strip the semicolon if it's the very last character
            // This prevents accidental deletion of semicolons inside strings (e.g. "Error;")
            String cleanLine = line;
            if (cleanLine.endsWith(";")) {
                cleanLine = cleanLine.substring(0, cleanLine.length() - 1).trim();
            }

            // Parse the line into components (Type, Name, Value)
            var matcher = declarationPattern.matcher(cleanLine);

            if (!matcher.matches()) continue;

            String type = matcher.group(1);
            String name = matcher.group(2);
            String value = matcher.group(3).trim();

            //Duplicate Declaration Check
            // You cannot declare the same variable name twice in the same scope.
            if (variables.containsKey(name)) {
                errors.add("Line " + (lineNum + 1) + ": '" + name + "' already declared.");
                continue;
            }
            //RULE 2: Type Compatibility Check
            // Does the value match the specific Regex for that type?
            if (!isValidValueForType(type, value)) {
                errors.add("Line " + (lineNum + 1) + ": Invalid value '" + value + "' for type '" + type + "'");
                continue;
            }

            variables.put(name, new VariableInfo(type, value, lineNum + 1));
        }

        if (!errors.isEmpty()) {
            return new Result(false, "Semantic Analysis Failed!\n\n" + String.join("\n", errors), 
                            new HashMap<>(), errors);
        }

        return new Result(true, "Semantic Analysis Passed!", variables, new ArrayList<>());
    }

    // === TYPE VALIDATION LOGIC ===
    private boolean isValidValueForType(String type, String value) {
        // Basic Regex Check (Format check)
        Pattern pattern = typeChecks.get(type);
        if (pattern == null || !pattern.matcher(value).matches()) {
            return false;
        }

        // Range Check (Logic check)
        // e.g., 1000 is a valid integer format, but too big for a byte (-128 to 127)
        try {
            switch (type) {
                case "byte":
                    if (value.matches("-?\\d+")) {
                        long val = Long.parseLong(value);
                        return val >= -128 && val <= 127;
                    }
                    break;
                case "short":
                    if (value.matches("-?\\d+")) {
                        long val = Long.parseLong(value);
                        return val >= -32768 && val <= 32767;
                    }
                    break;
                case "int":
                    if (value.matches("-?\\d+")) {
                        long val = Long.parseLong(value);
                        return val >= -2147483648L && val <= 2147483647L;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            return false; // Number is too big for Java to even parse (Overflow)
        }

        return true;
    }
}