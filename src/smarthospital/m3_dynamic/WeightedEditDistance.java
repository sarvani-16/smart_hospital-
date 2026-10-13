package smarthospital.m3_dynamic;

import java.util.*;

/**
 * Weighted Edit Distance for Medical Record Error Correction.
 * 
 * Purpose:
 *   Assigns domain-specific non-uniform costs to edits based on clinical error patterns:
 *   - Low cost (0.4) for optical OCR / digit-letter confusions (e.g., '0' <-> 'O', '1' <-> 'l').
 *   - Moderate cost (0.5) for vowel variations and phonetic homophones ('c' <-> 'k', 's' <-> 'z').
 *   - Standard cost (1.0) for standard insertion/deletion.
 *   - High cost (2.5) for dangerous medical swaps (e.g. 'o' <-> 'e' in hypo/hyper prefixes).
 * 
 * Input:
 *   Two medical strings or record entries.
 * 
 * Output:
 *   Double representing the minimum weighted edit distance.
 * 
 * Time Complexity:
 *   O(m * n) where m and n are string lengths.
 * 
 * Space Complexity:
 *   O(m * n) for the DP cost matrix.
 */
public class WeightedEditDistance {

    public static final double COST_INSERT = 1.0;
    public static final double COST_DELETE = 1.0;
    public static final double COST_DEFAULT_SUB = 1.5;

    public static double getSubstitutionCost(char c1, char c2) {
        if (c1 == c2) return 0.0;
        char a = Character.toLowerCase(c1);
        char b = Character.toLowerCase(c2);

        // Optical / OCR confusion
        if ((a == '0' && b == 'o') || (a == 'o' && b == '0') ||
            (a == '1' && b == 'l') || (a == 'l' && b == '1') ||
            (a == 'v' && b == 'u') || (a == 'u' && b == 'v') ||
            (a == '5' && b == 's') || (a == 's' && b == '5')) {
            return 0.4;
        }

        // Phonetic / homophone consonants
        if ((a == 'c' && b == 'k') || (a == 'k' && b == 'c') ||
            (a == 's' && b == 'z') || (a == 'z' && b == 's') ||
            (a == 'p' && b == 'b') || (a == 'b' && b == 'p') ||
            (a == 'm' && b == 'n') || (a == 'n' && b == 'm')) {
            return 0.6;
        }

        // Vowel substitutions
        String vowels = "aeiou";
        if (vowels.indexOf(a) != -1 && vowels.indexOf(b) != -1) {
            return 0.7;
        }

        return COST_DEFAULT_SUB;
    }

    /**
     * Computes the weighted edit distance between s1 and s2.
     */
    public static double computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Double.MAX_VALUE;
        int m = s1.length();
        int n = s2.length();

        double[][] dp = new double[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i * COST_DELETE;
        for (int j = 0; j <= n; j++) dp[0][j] = j * COST_INSERT;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                double subCost = getSubstitutionCost(s1.charAt(i - 1), s2.charAt(j - 1));
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + COST_DELETE,
                                 dp[i][j - 1] + COST_INSERT),
                        dp[i - 1][j - 1] + subCost
                );
            }
        }
        return Math.round(dp[m][n] * 100.0) / 100.0;
    }
}
