package smarthospital.m3_dynamic;

import java.util.*;

/**
 * Levenshtein Distance for Medical Term Correction.
 * 
 * Purpose:
 *   Measures edit distance (insertions, deletions, substitutions) between an input query
 *   and clinical terms in the hospital lexicon. Used to correct misspelled diagnoses,
 *   medications, and disease names entered by doctors and staff.
 * 
 * Input:
 *   source: The typed or OCR medical term.
 *   target: The dictionary candidate term.
 * 
 * Output:
 *   Integer edit distance, and ranked list of closest vocabulary matches.
 * 
 * Time Complexity:
 *   O(m * n) where m = source.length(), n = target.length().
 *   Lexicon correction: O(V * m * n) for vocabulary of size V.
 * 
 * Space Complexity:
 *   O(m * n) for DP matrix (or O(min(m, n)) for distance only).
 */
public class LevenshteinDistance {

    /**
     * Standard hospital medical dictionary for autocorrection.
     */
    public static final List<String> HOSPITAL_LEXICON = List.of(
            "diabetes", "hypertension", "asthma", "pneumonia", "stroke",
            "carcinoma", "arrhythmia", "osteoarthritis", "appendicitis",
            "tuberculosis", "leukemia", "nephropathy", "cardiomyopathy",
            "hyperlipidemia", "anemia", "ischemia", "bronchitis", "pancreatitis",
            "metformin", "atorvastatin", "ceftriaxone", "amiodarone", "salbutamol",
            "alteplase", "telmisartan", "pregabalin", "aspirin", "clopidogrel"
    );

    /**
     * Computes classical Levenshtein Distance in O(m * n).
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
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,      // Deletion
                                 dp[i][j - 1] + 1),     // Insertion
                        dp[i - 1][j - 1] + cost         // Substitution
                );
            }
        }
        return dp[m][n];
    }

    public static class Suggestion {
        public final String term;
        public final int distance;

        public Suggestion(String term, int distance) {
            this.term = term;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("%-18s (Edit Distance: %d)", term, distance);
        }
    }

    /**
     * Finds top-K closest medical terms in the vocabulary.
     */
    public static List<Suggestion> findClosestTerms(String query, Collection<String> vocabulary, int topK) {
        List<Suggestion> suggestions = new ArrayList<>();
        if (query == null || query.isEmpty()) return suggestions;

        Collection<String> vocab = (vocabulary != null && !vocabulary.isEmpty()) ? vocabulary : HOSPITAL_LEXICON;
        for (String term : vocab) {
            int dist = computeDistance(query, term);
            suggestions.add(new Suggestion(term, dist));
        }

        suggestions.sort(Comparator.comparingInt(s -> s.distance));
        if (suggestions.size() > topK) {
            return new ArrayList<>(suggestions.subList(0, topK));
        }
        return suggestions;
    }
}
