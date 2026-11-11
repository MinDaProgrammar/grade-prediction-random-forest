package com.example;

public class Launcher {
    public static void main(String[] args) {
        // If the user types "cli" or "console", run FileDisplayer after the jar file from the terminal
        if (args.length > 0 && (args[0].equalsIgnoreCase("cli") || args[0].equalsIgnoreCase("console"))) {
            System.out.println("Launching Console Mode...");
            FileDisplayer.main(args);
        } else {
            // Otherwise, default to the JavaFX App
            System.out.println("Launching GUI Mode...");
            App.main(args);
        }
    }
}