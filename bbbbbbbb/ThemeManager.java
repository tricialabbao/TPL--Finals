package com.tam.compiler;

import javafx.scene.layout.VBox;

public class ThemeManager {
    private VBox root;
    private boolean isDarkMode = false;

    public ThemeManager(VBox root) {
        this.root = root;
    }

    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }

    public void applyLightTheme() {
        isDarkMode = false;
        root.setStyle("-fx-background-color: #f8fafc;");
    }

    public void applyDarkTheme() {
        isDarkMode = true;
        root.setStyle("-fx-background-color: #0f172a;");
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public String getBackgroundColor() {
        return isDarkMode ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.7)";
    }

    public String getBorderColor() {
        return isDarkMode ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.1)";
    }

    public String getStageBgColor() {
        return isDarkMode ? "rgba(255,255,255,0.05)" : "rgba(255,255,255,0.8)";
    }

    public String getStageBorderColor() {
        return isDarkMode ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.08)";
    }

    public String getPanelBgColor() {
        return isDarkMode ? "rgba(30,41,59,0.8)" : "rgba(255,255,255,0.9)";
    }

    public String getPanelBorderColor() {
        return isDarkMode ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.1)";
    }

    public String getPanelHeaderBgColor() {
        return isDarkMode ? "rgba(0,0,0,0.3)" : "rgba(0,0,0,0.04)";
    }

    public String getTextColor() {
        return isDarkMode ? "#e2e8f0" : "#1e293b";
    }

    public String getMutedTextColor() {
        return isDarkMode ? "rgba(226,232,240,0.6)" : "rgba(30,41,59,0.6)";
    }

    public String getIconColor() {
        return isDarkMode ? "#a78bfa" : "#8b5cf6";
    }
}