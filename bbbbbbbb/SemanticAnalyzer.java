package com.tam.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SemanticAnalyzer {
    
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
    private final Pattern declarationPattern = 
        Pattern.compile("^(int|double|float|char|boolean|byte|short|long|String)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)$");

    public SemanticAnalyzer() {
        typeChecks.put("int", Pattern.compile("^-?\\d+$"));
        typeChecks.put("byte", Pattern.compile("^-?\\d+$"));
        typeChecks.put("short", Pattern.compile("^-?\\d+$"));
        typeChecks.put("long", Pattern.compile("^-?\\d+$"));
        typeChecks.put("double", Pattern.compile("^-?\\d+(\\.\\d+)?$"));
        typeChecks.put("float", Pattern.compile("^-?\\d+(\\.\\d+)?$"));
        typeChecks.put("char", Pattern.compile("^'.'$"));
        typeChecks.put("boolean", Pattern.compile("^(true|false)$"));
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

            String cleanLine = line.replace(";", "").trim();
            var matcher = declarationPattern.matcher(cleanLine);
            
            if (!matcher.matches()) continue;

            String type = matcher.group(1);
            String name = matcher.group(2);
            String value = matcher.group(3).trim();

            if (variables.containsKey(name)) {
                errors.add("Line " + (lineNum + 1) + ": '" + name + "' already declared.");
                continue;
            }

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

    private boolean isValidValueForType(String type, String value) {
        Pattern pattern = typeChecks.get(type);
        return pattern != null && pattern.matcher(value).matches();
    }
}