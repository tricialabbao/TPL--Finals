package com.tam.compiler;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;

public class EventHandlers {
    private AppState appState;
    private LexicalAnalyzer lexicalAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;
    private SemanticAnalyzer semanticAnalyzer;
    private UIComponents uiComponents;
    private Stage primaryStage;

    public EventHandlers(AppState appState, 
                        LexicalAnalyzer lexicalAnalyzer,
                        SyntaxAnalyzer syntaxAnalyzer, 
                        SemanticAnalyzer semanticAnalyzer,
                        UIComponents uiComponents, 
                        Stage primaryStage) {
        this.appState = appState;
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.syntaxAnalyzer = syntaxAnalyzer;
        this.semanticAnalyzer = semanticAnalyzer;
        this.uiComponents = uiComponents;
        this.primaryStage = primaryStage;
    }

    public void setupAllHandlers() {
        uiComponents.getUploadZone().setOnMouseClicked(e -> handleFileUpload());
        uiComponents.getLexicalStage().setOnMouseClicked(e -> handleLexicalAnalysis());
        uiComponents.getSyntaxStage().setOnMouseClicked(e -> handleSyntaxAnalysis());
        uiComponents.getSemanticStage().setOnMouseClicked(e -> handleSemanticAnalysis());
        uiComponents.getClearButton().setOnAction(e -> handleClear());
    }

    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Java Files", "*.java", "*.txt")
        );
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                uiComponents.getCodeArea().setText(content);
                appState.setFileLoaded(true);
                uiComponents.setResultText("File loaded successfully!\n\nClick \"Lexical Analysis\" to begin.", false);
                uiComponents.updateButtonStates(appState);
            } catch (Exception e) {
                uiComponents.setResultText("ERROR: Failed to read file!", true);
            }
        }
    }

    private void handleLexicalAnalysis() {
        String code = uiComponents.getCodeArea().getText();
        uiComponents.setResultText("Performing Lexical Analysis\n\n", false);
        uiComponents.setStageStatus("lexical", "running");

        // THREADING LOGIC:
        // We run the analysis in a background thread to prevent the UI from freezing
        // If we ran this on the main JavaFX thread, the window would stop responding
        // during the Thread.sleep delay
        new Thread(() -> {
            try {
                // Fake delay to simulate complex processing (makes the UI look cooler)
                Thread.sleep(1500);

                // UI updates MUST happen on the main JavaFX Application Thread.
                // Platform.runLater queues this code to run safely on the main thread.
                javafx.application.Platform.runLater(() -> {
                    LexicalAnalyzer.Result result = lexicalAnalyzer.analyze(code);
                    
                    if (result.success) {
                        appState.setLexicalPassed(true);
                        appState.setTokens(result.tokens);
                        uiComponents.setStageStatus("lexical", "success");
                    } else {
                        // On Failure: Set Error flag. This locks the UI (Check UIComponents logic)
                        appState.setLexicalPassed(false);
                        appState.setHasError(true);
                        uiComponents.setStageStatus("lexical", "error");
                    }
                    
                    uiComponents.setResultText(result.message, !result.success);
                    uiComponents.updateButtonStates(appState);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleSyntaxAnalysis() {
        String code = uiComponents.getCodeArea().getText();
        uiComponents.setResultText("Performing Syntax Analysis\n\n", false);
        uiComponents.setStageStatus("syntax", "running");

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                
                javafx.application.Platform.runLater(() -> {
                    SyntaxAnalyzer.Result result = syntaxAnalyzer.analyze(code);
                    
                    if (result.success) {
                        appState.setSyntaxPassed(true);
                        uiComponents.setStageStatus("syntax", "success");
                    } else {
                        appState.setSyntaxPassed(false);
                        appState.setHasError(true);
                        uiComponents.setStageStatus("syntax", "error");
                    }
                    
                    uiComponents.setResultText(result.message, !result.success);
                    uiComponents.updateButtonStates(appState);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleSemanticAnalysis() {
        String code = uiComponents.getCodeArea().getText();
        uiComponents.setResultText("Performing Semantic Analysis\n\n", false);
        uiComponents.setStageStatus("semantic", "running");

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                
                javafx.application.Platform.runLater(() -> {
                    SemanticAnalyzer.Result result = semanticAnalyzer.analyze(code);
                    
                    if (result.success) {
                        appState.setSemanticPassed(true);
                        appState.setVariables(result.variables);
                        uiComponents.setStageStatus("semantic", "success");
                    } else {
                        appState.setSemanticPassed(false);
                        appState.setHasError(true);
                        uiComponents.setStageStatus("semantic", "error");
                    }
                    
                    uiComponents.setResultText(result.message, !result.success);
                    uiComponents.updateButtonStates(appState);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClear() {
        uiComponents.getCodeArea().clear();
        uiComponents.setResultText("Welcome! Load a Java file to start compilation analysis.", false);

        // Full Reset of the State Machine
        appState.reset();
        uiComponents.updateLineNumbers();
        uiComponents.updateButtonStates(appState);

        // Reset Visual Indicators to gray
        uiComponents.setStageStatus("lexical", "default");
        uiComponents.setStageStatus("syntax", "default");
        uiComponents.setStageStatus("semantic", "default");
    }
}