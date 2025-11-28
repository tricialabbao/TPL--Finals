package com.tam.compiler;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowController {
    private Stage stage;
    private boolean isMaximized = false;
    private double prevX, prevY, prevWidth, prevHeight;
    private double xOffset = 0;
    private double yOffset = 0;

    public WindowController(Stage stage) {
        this.stage = stage;
    }

    public void toggleMaximize() {
        if (isMaximized) {
            stage.setX(prevX);
            stage.setY(prevY);
            stage.setWidth(prevWidth);
            stage.setHeight(prevHeight);
            isMaximized = false;
        } else {
            prevX = stage.getX();
            prevY = stage.getY();
            prevWidth = stage.getWidth();
            prevHeight = stage.getHeight();
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            isMaximized = true;
        }
    }

    public void minimize() {
        stage.setIconified(true);
    }

    public void close() {
        stage.close();
    }

    public void centerOnScreen() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public void setupKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                toggleMaximize();
            } else if (event.getCode() == KeyCode.ESCAPE && isMaximized) {
                toggleMaximize();
            }
        });
    }

    public double getXOffset() { return xOffset; }
    public double getYOffset() { return yOffset; }
    public void setXOffset(double x) { xOffset = x; }
    public void setYOffset(double y) { yOffset = y; }
    public boolean isMaximized() { return isMaximized; }
}