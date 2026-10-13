package smarthospital.m3_dynamic;

import java.util.*;

/**
 * Damerau-Levenshtein Distance for Medical Spelling Correction.
 * 
 * Purpose:
 *   Extends classical Levenshtein distance by including transpositions of two
 *   adjacent characters as a single elementary edit operation.
 *   Essential for correcting clinical typing slips (e.g. "diebtes" -> "diabetes",
 *   "astmha" -> "asthma", "anmeia" -> "anemia").
 * 
 * Input:
 *   source: Misspelled medical input.
 *   target: Dictionary medical term.
 * 
 * Output:
 *   Damerau-Levenshtein distance and ranked suggestions.
 * 
 * Time Complexity:
 *   O(m * n) where m = source.length(), n = target.length().
 * 
 * Space Complexity:
 *   O(m * n) DP table.
 */
public class DamerauLevenshtein {

    /**
     * Computes Damerau-Levenshtein distance with adjacent transpositions.
     */
    public static int computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        String a = s1.trim().toLowerCase();
        String b = s2.trim().toLowerCase();

        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;

                // Basic operations: deletion, insertion, substitution
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,
                                 dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );

                // Transposition of two adjacent characters
                if (i > 1 && j > 1 &&
                    a.charAt(i - 1) == b.charAt(j - 2) &&
                    a.charAt(i - 2) == b.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[m][n];
    }

    public static List<LevenshteinDistance.Suggestion> findClosestTerms(String query, Collection<String> vocabulary, int topK) {
        List<LevenshteinDistance.Suggestion> suggestions = new ArrayList<>();
        if (query == null || query.isEmpty()) return suggestions;

        Collection<String> vocab = (vocabulary != null && !vocabulary.isEmpty()) ? vocabulary : LevenshteinDistance.HOSPITAL_LEXICON;
        for (String term : vocab) {
            int dist = computeDistance(query, term);
            suggestions.add(new LevenshteinDistance.Suggestion(term, dist));
        }

        suggestions.sort(Comparator.comparingInt(s -> s.distance));
        if (suggestions.size() > topK) {
            return new ArrayList<>(suggestions.subList(0, topK));
        }
        return suggestions;
    }
}
