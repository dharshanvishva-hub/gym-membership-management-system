package com.gym;

import com.gym.ui.ConsoleUI;
import com.gym.ui.GymAppGUI;

/**
 * Main application entry point for Scenario 27:
 * Gym Membership Management and Fitness Activity Analysis System.
 *
 * Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
 * Velalar College of Engineering and Technology, Erode - 12
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && ("--gui".equalsIgnoreCase(args[0]) || "-g".equalsIgnoreCase(args[0]))) {
            System.out.println("[INFO] Starting Gym Management System in Swing GUI mode...");
            GymAppGUI.launch();
        } else {
            // Default to Interactive Console UI
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        }
    }
}
