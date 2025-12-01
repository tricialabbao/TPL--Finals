package com.tam.compiler;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CompilerUI {
    private Stage primaryStage;
    private Scene scene;
    private VBox root;
    
    private AppState appState;
    private LexicalAnalyzer lexicalAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;
    private SemanticAnalyzer semanticAnalyzer;
    
    private WindowController windowController;
    private UIComponents uiComponents;
    private EventHandlers eventHandlers;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.appState = new AppState();
        this.lexicalAnalyzer = new LexicalAnalyzer();
        this.syntaxAnalyzer = new SyntaxAnalyzer();
        this.semanticAnalyzer = new SemanticAnalyzer();
        
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        initializeUI();
        
        primaryStage.setTitle("TAM's Java Compiler");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(750);
        
        windowController.centerOnScreen();
        primaryStage.show();
    }

    private void initializeUI() {
        root = new VBox();
        root.getStyleClass().add("app");
        
        // Apply modern dark gradient background using Region
        javafx.scene.layout.Background background = new javafx.scene.layout.Background(
            new javafx.scene.layout.BackgroundFill(
                new javafx.scene.paint.LinearGradient(
                    0, 0, 1, 1, true,
                    javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#0f172a")),
                    new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#1e293b"))
                ),
                null, null
            )
        );
        root.setBackground(background);
        
        // Initialize managers
        windowController = new WindowController(primaryStage);
        
        // Create UI components
        uiComponents = new UIComponents(windowController);
        
        // Build layout
        HBox titleBar = uiComponents.createTitleBar();
        HBox header = uiComponents.createHeader();
        HBox compilerBody = createCompilerBody();
        
        root.getChildren().addAll(titleBar, header, compilerBody);
        scene = new Scene(root);
        
        // Setup controllers
        windowController.setupKeyboardShortcuts(scene);
        eventHandlers = new EventHandlers(
            appState, lexicalAnalyzer, syntaxAnalyzer, semanticAnalyzer,
            uiComponents, primaryStage
        );
        eventHandlers.setupAllHandlers();
        
        // Set initial button states (all disabled until file is loaded)
        uiComponents.updateButtonStates(appState);
    }

    private HBox createCompilerBody() {
        HBox compilerBody = new HBox();
        compilerBody.getStyleClass().add("compiler-body");
        compilerBody.setPadding(new Insets(15, 25, 25, 25));
        compilerBody.setSpacing(20);
        
        VBox sidebar = uiComponents.createSidebar();
        VBox mainContent = uiComponents.createMainContent();
        
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        compilerBody.getChildren().addAll(sidebar, mainContent);
        
        return compilerBody;
    }
}