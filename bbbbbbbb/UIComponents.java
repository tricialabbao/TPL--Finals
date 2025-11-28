package com.tam.compiler;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class UIComponents {
    private WindowController windowController;
    
    // UI Components
    private TextArea codeArea;
    private TextArea resultArea;
    private TextArea lineNumbers;
    private Label lineCountLabel;
    private Label charCountLabel;
    private Label consoleBadge;
    private Button clearBtn;
    private VBox uploadZone;
    
    // Stage cards
    private VBox lexicalStage, syntaxStage, semanticStage;
    private Label lexicalStatus, syntaxStatus, semanticStatus;
    
    private Timeline pulseAnimation;

    public UIComponents(WindowController windowController) {
        this.windowController = windowController;
    }

    public HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("title-bar");
        titleBar.setPadding(new Insets(8, 16, 8, 16));
        titleBar.setSpacing(10);
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox windowControls = createWindowControls();
        titleBar.getChildren().addAll(spacer, windowControls);
        makeDraggable(titleBar);
        
        return titleBar;
    }

    private HBox createWindowControls() {
        HBox controls = new HBox();
        controls.setSpacing(12);
        controls.setAlignment(Pos.CENTER_RIGHT);
        
        Button minimizeBtn = createControlButton("─\r\n" + "");
        Button maximizeBtn = createControlButton("□\r\n" + "");
        Button closeBtn = createControlButton("×\r\n" + "");
        
        setupControlHover(minimizeBtn, "rgba(100,116,139,0.2)", "#94a3b8");
        setupControlHover(maximizeBtn, "rgba(100,116,139,0.2)", "#94a3b8");
        setupControlHover(closeBtn, "#ef4444", "white");
        
        minimizeBtn.setOnAction(e -> windowController.minimize());
        maximizeBtn.setOnAction(e -> windowController.toggleMaximize());
        closeBtn.setOnAction(e -> windowController.close());
        
        controls.getChildren().addAll(minimizeBtn, maximizeBtn, closeBtn);
        return controls;
    }

    private Button createControlButton(String text) {
        Button btn = new Button(text);
        // FIXED: Better styling for window control buttons
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; " +
                    "-fx-min-width: 36px; -fx-min-height: 36px; -fx-max-width: 36px; -fx-max-height: 36px; " +
                    "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand; " +
                    "-fx-font-size: 18px; -fx-font-weight: 600; " +
                    "-fx-border-color: transparent; -fx-border-width: 0;");
        return btn;
    }

    private void setupControlHover(Button button, String hoverColor, String textColor) {
        String baseStyle = button.getStyle();
        button.setOnMouseEntered(e -> 
            button.setStyle(baseStyle.replace("transparent", hoverColor) + "-fx-text-fill: " + textColor + ";"));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }

    private void makeDraggable(HBox titleBar) {
        titleBar.setOnMousePressed((MouseEvent event) -> {
            windowController.setXOffset(event.getSceneX());
            windowController.setYOffset(event.getSceneY());
        });
        
        titleBar.setOnMouseDragged((MouseEvent event) -> {
            if (!windowController.isMaximized()) {
                javafx.stage.Stage stage = (javafx.stage.Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() - windowController.getXOffset());
                stage.setY(event.getScreenY() - windowController.getYOffset());
            }
        });
        
        titleBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) windowController.toggleMaximize();
        });
    }

    public HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setSpacing(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: transparent;");
        
        HBox logo = createLogo();
        
        HBox.setHgrow(logo, Priority.ALWAYS);
        header.getChildren().add(logo);
        
        return header;
    }

    private HBox createLogo() {
        HBox logo = new HBox();
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.setSpacing(16);
        
        Label logoText = new Label("TAM's Java Compiler");
        logoText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        
        logo.getChildren().add(logoText);
        return logo;
    }

    // ==================== Sidebar ====================
    public VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);
        sidebar.setMaxWidth(280);
        sidebar.setSpacing(16);
        sidebar.setPadding(new Insets(15));
        
        uploadZone = createUploadZone();
        VBox stages = createStages();
        clearBtn = createClearButton();
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(uploadZone, stages, spacer, clearBtn);
        
        return sidebar;
    }

    private VBox createUploadZone() {
        VBox zone = new VBox();
        zone.setAlignment(Pos.CENTER);
        zone.setPadding(new Insets(24));
        updateUploadZoneStyle(zone, false);
        
        // Upload icon using styled circle
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(50, 50);
        iconContainer.setStyle("-fx-background-color: rgba(139,92,246,0.2); -fx-background-radius: 25px;");
        
        Label icon = new Label("↑");
        icon.setStyle("-fx-font-size: 26px; -fx-text-fill: #a78bfa; -fx-font-weight: bold;");
        iconContainer.getChildren().add(icon);
        
        Label uploadText = new Label("Drop file or click to upload");
        uploadText.setStyle("-fx-font-weight: 600; -fx-font-size: 13px; -fx-text-fill: #e2e8f0;");
        
        Label uploadSubtext = new Label(".java or .txt files only");
        uploadSubtext.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        
        VBox.setMargin(iconContainer, new Insets(0, 0, 12, 0));
        VBox.setMargin(uploadText, new Insets(0, 0, 3, 0));
        
        zone.setOnMouseEntered(e -> {
            updateUploadZoneStyle(zone, true);
            iconContainer.setScaleX(1.1);
            iconContainer.setScaleY(1.1);
        });
        zone.setOnMouseExited(e -> {
            updateUploadZoneStyle(zone, false);
            iconContainer.setScaleX(1.0);
            iconContainer.setScaleY(1.0);
        });
        
        zone.getChildren().addAll(iconContainer, uploadText, uploadSubtext);
        return zone;
    }
    
    private void updateUploadZoneStyle(VBox zone, boolean hover) {
        String borderColor = hover ? "#8b5cf6" : "rgba(100,116,139,0.4)";
        String bgColor = hover ? "rgba(139,92,246,0.12)" : "rgba(30,41,59,0.3)";
        String shadow = hover ? "-fx-effect: dropshadow(gaussian, rgba(139,92,246,0.4), 16, 0, 0, 0);" : 
                               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);";
        
        zone.setStyle("-fx-border-style: dashed; -fx-border-radius: 14px; " +
                     "-fx-background-radius: 14px; -fx-cursor: hand; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-color: " + borderColor + "; " +
                     "-fx-background-color: " + bgColor + "; " + shadow);
    }

    private VBox createStages() {
        VBox stages = new VBox();
        stages.setSpacing(12);
        
        lexicalStage = createStage("Lexical Analysis", "Tokenize source code");
        lexicalStatus = (Label) ((HBox) lexicalStage.getChildren().get(0)).getChildren().get(1);
        
        syntaxStage = createStage("Syntax Analysis", "Check grammar & structure");
        syntaxStatus = (Label) ((HBox) syntaxStage.getChildren().get(0)).getChildren().get(1);
        
        semanticStage = createStage("Semantic Analysis", "Type checking & logic");
        semanticStatus = (Label) ((HBox) semanticStage.getChildren().get(0)).getChildren().get(1);
        
        stages.getChildren().addAll(lexicalStage, syntaxStage, semanticStage);
        return stages;
    }

    private VBox createStage(String title, String description) {
        VBox stage = new VBox();
        stage.setPadding(new Insets(14));
        updateStageDefaultStyle(stage);
        
        HBox content = new HBox();
        content.setSpacing(12);
        content.setAlignment(Pos.CENTER_LEFT);
        
        VBox stageInfo = new VBox(3);
        Label stageTitle = new Label(title);
        stageTitle.setStyle("-fx-font-weight: 600; -fx-font-size: 13px; -fx-text-fill: #e2e8f0;");
        Label stageDesc = new Label(description);
        stageDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        stageInfo.getChildren().addAll(stageTitle, stageDesc);
        
        Label statusIcon = new Label("○");
        statusIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #475569; -fx-font-weight: bold;");
        
        HBox.setHgrow(stageInfo, Priority.ALWAYS);
        content.getChildren().addAll(stageInfo, statusIcon);
        stage.getChildren().add(content);
        
        stage.setOnMouseEntered(e -> { 
            if (!stage.isDisabled()) {
                stage.setScaleX(1.02); 
                stage.setScaleY(1.02);
            }
        });
        stage.setOnMouseExited(e -> { 
            stage.setScaleX(1.0); 
            stage.setScaleY(1.0); 
        });
        
        return stage;
    }
    
    private void updateStageDefaultStyle(VBox stage) {
        String borderColor = "rgba(71,85,105,0.4)";
        String bgColor = "rgba(30,41,59,0.3)";
        stage.setStyle("-fx-background-radius: 12px; -fx-border-radius: 12px; " +
                      "-fx-cursor: hand; -fx-border-width: 1.5px; " +
                      "-fx-border-color: " + borderColor + "; " +
                      "-fx-background-color: " + bgColor + "; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);");
    }

    private Button createClearButton() {
        Button btn = new Button("Clear All");
        updateClearButtonStyle(btn, false);
        btn.setOnMouseEntered(e -> {
            btn.setTranslateY(-2);
            updateClearButtonStyle(btn, true);
        });
        btn.setOnMouseExited(e -> {
            btn.setTranslateY(0);
            updateClearButtonStyle(btn, false);
        });
        return btn;
    }
    
    private void updateClearButtonStyle(Button btn, boolean hover) {
        String bgColor = hover ? "rgba(248,113,113,0.2)" : "rgba(239,68,68,0.12)";
        String shadow = hover ? "-fx-effect: dropshadow(gaussian, rgba(239,68,68,0.5), 16, 0, 0, 0);" : 
                               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);";
        
        btn.setStyle("-fx-background-color: " + bgColor + "; " +
                    "-fx-text-fill: #f87171; -fx-border-color: rgba(248,113,113,0.4); " +
                    "-fx-border-width: 1.5px; -fx-background-radius: 12px; -fx-border-radius: 12px; " +
                    "-fx-padding: 12px 18px; -fx-font-size: 13px; -fx-font-weight: 600; -fx-cursor: hand; " +
                    shadow);
    }

    // ==================== Main Content ====================
    public VBox createMainContent() {
        VBox mainContent = new VBox(18);
        mainContent.getStyleClass().add("main");
        
        VBox outputPanel = createOutputPanel();
        VBox editorPanel = createEditorPanel();
        
        VBox.setVgrow(editorPanel, Priority.ALWAYS);
        mainContent.getChildren().addAll(outputPanel, editorPanel);
        
        return mainContent;
    }

    private VBox createOutputPanel() {
        VBox panel = new VBox();
        panel.setPrefHeight(160);
        panel.setMinHeight(160);
        panel.setMaxHeight(160);
        // FIXED: Added visible bottom border
        panel.setStyle("-fx-background-radius: 16px; -fx-border-radius: 16px; " +
                      "-fx-border-width: 1.5px; -fx-border-color: rgba(255,255,255,0.12); " +
                      "-fx-background-color: rgba(15,23,42,0.6); " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 4);");
        
        HBox header = new HBox();
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(30,41,59,0.4); " +
                       "-fx-background-radius: 16px 16px 0 0;");
        
        Label title = new Label("Result");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #e2e8f0;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        consoleBadge = new Label("Ready");
        consoleBadge.setStyle("-fx-padding: 5 14 5 14; -fx-background-radius: 50px; " +
                             "-fx-font-size: 10px; -fx-font-weight: 600; -fx-text-fill: white;");
        updateBadge("ready");
        
        header.getChildren().addAll(title, spacer, consoleBadge);
        
        // Result area
        resultArea = new TextArea("Welcome to TAM's Java Compiler!\nLoad a file to begin compilation analysis.");
        resultArea.setWrapText(true);
        resultArea.setStyle("-fx-font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace; " +
                           "-fx-font-size: 12px; -fx-control-inner-background: #0a0f1e; " +
                           "-fx-text-fill: #cbd5e1; -fx-background-color: #0a0f1e; -fx-padding: 12px; " +
                           "-fx-border-radius: 0 0 16px 16px; -fx-background-radius: 0 0 16px 16px;");
        resultArea.setEditable(false);
        
        panel.getChildren().addAll(header, resultArea);
        VBox.setVgrow(resultArea, Priority.ALWAYS);
        
        return panel;
    }

    private VBox createEditorPanel() {
        VBox panel = new VBox();
        // FIXED: Added visible bottom border matching the Result panel
        panel.setStyle("-fx-background-radius: 16px; -fx-border-radius: 16px; -fx-border-width: 1.5px; " +
                      "-fx-border-color: rgba(255,255,255,0.12); -fx-background-color: rgba(15,23,42,0.6); " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 4);");
        
        HBox header = new HBox();
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(30,41,59,0.4); -fx-background-radius: 16px 16px 0 0;");
        
        Label title = new Label("Source Code");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #e2e8f0;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox editorInfo = new HBox(12);
        lineCountLabel = new Label("1 line");
        charCountLabel = new Label("0 chars");
        lineCountLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        charCountLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        editorInfo.getChildren().addAll(lineCountLabel, charCountLabel);
        
        header.getChildren().addAll(title, spacer, editorInfo);
        
        // Editor wrapper with line numbers
        HBox editorContainer = new HBox();
        editorContainer.setStyle("-fx-background-color: #0a0f1e; -fx-border-radius: 0 0 16px 16px; " +
                                "-fx-background-radius: 0 0 16px 16px;");
        
        // Line numbers area with ScrollPane
        ScrollPane lineNumberScroll = new ScrollPane();
        lineNumberScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lineNumberScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lineNumberScroll.setFitToHeight(true);
        lineNumberScroll.setPrefWidth(50);
        lineNumberScroll.setMinWidth(50);
        lineNumberScroll.setMaxWidth(50);
        lineNumberScroll.setStyle("-fx-background: rgba(15,23,42,0.5); -fx-background-color: rgba(15,23,42,0.5); " +
                                 "-fx-border-color: rgba(51,65,85,0.6); -fx-border-width: 0 1 0 0;");
        
        lineNumbers = new TextArea("1");
        lineNumbers.setStyle("-fx-font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace; " +
                            "-fx-font-size: 12px; -fx-control-inner-background: transparent; " +
                            "-fx-text-fill: #475569; -fx-background-color: transparent; " +
                            "-fx-text-alignment: right; -fx-padding: 12 8 12 8; -fx-font-weight: 500;");
        lineNumbers.setEditable(false);
        lineNumbers.setFocusTraversable(false);
        
        lineNumberScroll.setContent(lineNumbers);
        
        // Code area with ScrollPane
        ScrollPane codeScrollPane = new ScrollPane();
        codeScrollPane.setFitToWidth(true);
        codeScrollPane.setFitToHeight(true);
        codeScrollPane.setStyle("-fx-background: #0a0f1e; -fx-background-color: #0a0f1e;");
        
        codeArea = new TextArea();
        codeArea.setWrapText(true);
        codeArea.setPromptText("// Load a Java file to view code...");
        codeArea.setStyle("-fx-font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace; " +
                         "-fx-font-size: 13px; -fx-control-inner-background: #0a0f1e; " +
                         "-fx-text-fill: #e2e8f0; -fx-background-color: #0a0f1e; " +
                         "-fx-padding: 12 16 12 16; -fx-prompt-text-fill: #334155; " +
                         "-fx-highlight-fill: rgba(139,92,246,0.3); -fx-highlight-text-fill: #e2e8f0;");
        codeArea.setEditable(false);
        codeArea.textProperty().addListener((obs, old, val) -> updateLineNumbers());
        
        codeScrollPane.setContent(codeArea);
        
        // Sync scrolling between line numbers and code area
        codeScrollPane.vvalueProperty().addListener((obs, old, val) -> {
            lineNumberScroll.setVvalue(val.doubleValue());
        });
        
        HBox.setHgrow(codeScrollPane, Priority.ALWAYS);
        editorContainer.getChildren().addAll(lineNumberScroll, codeScrollPane);
        
        panel.getChildren().addAll(header, editorContainer);
        VBox.setVgrow(editorContainer, Priority.ALWAYS);
        
        return panel;
    }

    // ==================== Helper Methods ====================
    public void updateLineNumbers() {
        String code = codeArea.getText();
        String[] lines = code.split("\n", -1);
        int lineCount = lines.length;
        int charCount = code.length();
        
        lineCountLabel.setText(lineCount + " line" + (lineCount != 1 ? "s" : ""));
        charCountLabel.setText(charCount + " char" + (charCount != 1 ? "s" : ""));
        
        StringBuilder numbers = new StringBuilder();
        for (int i = 1; i <= lineCount; i++) {
            numbers.append(i);
            if (i < lineCount) numbers.append("\n");
        }
        lineNumbers.setText(numbers.toString());
    }

    public void setResultText(String text, boolean isError) {
        resultArea.setText(text);
        javafx.application.Platform.runLater(() -> resultArea.setScrollTop(0));
        
        String badgeType = isError ? "error" :
                          text.contains("Passed") || text.contains("successfully") ? "success" :
                          text.contains("Performing") || text.contains("Analyzing") ? "running" : "ready";
        updateBadge(badgeType);
    }

    private void updateBadge(String type) {
        String color = switch (type) {
            case "success" -> "#34d399";
            case "error" -> "#f87171";
            case "running" -> "#fbbf24";
            default -> "#64748b";
        };
        
        consoleBadge.setText(switch (type) {
            case "success" -> "Success";
            case "error" -> "Error";
            case "running" -> "Running";
            default -> "Ready";
        });
        
        consoleBadge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                             "-fx-padding: 6 16 6 16; -fx-background-radius: 50px; " +
                             "-fx-font-size: 11px; -fx-font-weight: 600; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);");
        
        if ("running".equals(type)) startPulseAnimation();
        else stopPulseAnimation();
    }

    private void startPulseAnimation() {
        stopPulseAnimation();
        pulseAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> consoleBadge.setOpacity(0.6)),
            new KeyFrame(Duration.seconds(1), e -> consoleBadge.setOpacity(1))
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.play();
    }

    private void stopPulseAnimation() {
        if (pulseAnimation != null) pulseAnimation.stop();
        consoleBadge.setOpacity(1);
    }

    public void setStageStatus(String stage, String status) {
        VBox stageEl = switch (stage) {
            case "lexical" -> lexicalStage;
            case "syntax" -> syntaxStage;
            case "semantic" -> semanticStage;
            default -> null;
        };
        
        Label statusEl = switch (stage) {
            case "lexical" -> lexicalStatus;
            case "syntax" -> syntaxStatus;
            case "semantic" -> semanticStatus;
            default -> null;
        };

        if (stageEl == null || statusEl == null) return;

        String baseStyle = "-fx-background-radius: 14px; -fx-border-radius: 14px; -fx-cursor: hand; -fx-border-width: 1.5px;";
        
        switch (status) {
            case "running" -> {
                stageEl.setStyle(baseStyle + " -fx-border-color: #a78bfa; -fx-background-color: rgba(139,92,246,0.15); " +
                               "-fx-effect: dropshadow(gaussian, rgba(139,92,246,0.5), 16, 0, 0, 0);");
                statusEl.setText("▷");
                statusEl.setStyle("-fx-font-size: 20px; -fx-text-fill: #a78bfa; -fx-font-weight: bold;");
            }
            case "success" -> {
                stageEl.setStyle(baseStyle + " -fx-border-color: #34d399; -fx-background-color: rgba(16,185,129,0.15); " +
                               "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.5), 16, 0, 0, 0);");
                statusEl.setText("✓");
                statusEl.setStyle("-fx-font-size: 20px; -fx-text-fill: #34d399; -fx-font-weight: bold;");
            }
            case "error" -> {
                stageEl.setStyle(baseStyle + " -fx-border-color: #f87171; -fx-background-color: rgba(239,68,68,0.15); " +
                               "-fx-effect: dropshadow(gaussian, rgba(239,68,68,0.5), 16, 0, 0, 0);");
                statusEl.setText("✗");
                statusEl.setStyle("-fx-font-size: 20px; -fx-text-fill: #f87171; -fx-font-weight: bold;");
            }
            default -> {
                updateStageDefaultStyle(stageEl);
                statusEl.setText("○");
                statusEl.setStyle("-fx-font-size: 20px; -fx-text-fill: #475569; -fx-font-weight: bold;");
            }
        }
    }

    public void updateButtonStates(AppState appState) {
        lexicalStage.setDisable(!appState.isFileLoaded());
        syntaxStage.setDisable(!appState.isLexicalPassed());
        semanticStage.setDisable(!appState.isSyntaxPassed());
        clearBtn.setDisable(!appState.isFileLoaded());
        
        lexicalStage.setOpacity(appState.isFileLoaded() ? 1.0 : 0.5);
        syntaxStage.setOpacity(appState.isLexicalPassed() ? 1.0 : 0.5);
        semanticStage.setOpacity(appState.isSyntaxPassed() ? 1.0 : 0.5);
        clearBtn.setOpacity(appState.isFileLoaded() ? 1.0 : 0.6);
    }

    //getters
    public TextArea getCodeArea() { return codeArea; }
    public VBox getUploadZone() { return uploadZone; }
    public VBox getLexicalStage() { return lexicalStage; }
    public VBox getSyntaxStage() { return syntaxStage; }
    public VBox getSemanticStage() { return semanticStage; }
    public Button getClearButton() { return clearBtn; }

}
