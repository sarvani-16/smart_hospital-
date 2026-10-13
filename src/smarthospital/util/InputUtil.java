package smarthospital.util;

import java.util.Scanner;

/**
 * Utility for robust, fail-safe user input in console menus.
 * Prevents input crashes and validates data bounds.
 */
public class InputUtil {

    public static String readString(Scanner scanner, String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!allowEmpty && input.isEmpty()) {
                System.out.println(">> Error: Input cannot be empty. Please try again.");
                continue;
            }
            return input;
        }
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val < min || val > max) {
                    System.out.printf(">> Error: Please enter a number between %d and %d.\n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid numeric input. Please enter an integer.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(line);
                if (val < min || val > max) {
                    System.out.printf(">> Error: Please enter a decimal between %.2f and %.2f.\n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid decimal input. Please enter a valid number.");
            }
        }
    }

    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            System.out.println(">> Error: Please enter 'y' or 'n'.");
        }
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print("\nPress [ENTER] to return to the menu...");
        try {
            scanner.nextLine();
        } catch (Exception ignored) {}
    }

    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(64));
        System.out.printf(" %s\n", title);
        System.out.println("=".repeat(64));
    }

    public static void printSubHeader(String title) {
        System.out.println("-".repeat(50));
        System.out.printf(" %s\n", title);
        System.out.println("-".repeat(50));
    }
}
