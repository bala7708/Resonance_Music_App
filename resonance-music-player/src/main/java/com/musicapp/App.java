package com.musicapp;

import com.musicapp.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Application entry point for Resonance Music Player.
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Starting Resonance Music Player v1.0.0");

        // Enable anti-aliased text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                new MainWindow();
                log.info("Main window launched successfully");
            } catch (Exception e) {
                log.error("Failed to launch application", e);
                JOptionPane.showMessageDialog(null,
                        "Failed to start Resonance: " + e.getMessage(),
                        "Launch Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
