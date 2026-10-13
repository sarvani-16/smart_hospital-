package smarthospital.m1_string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Z-Algorithm (Z-Function) Implementation.
 * 
 * Purpose:
 *   Computes the Z-array where Z[i] is the length of the longest substring starting
 *   from text[i] that is also a prefix of text.
 *   Used in SmartHospital for:
 *   1. Searching clinical patterns in O(n + m) linear time.
 *   2. Detecting repeated medical phrases, boilerplate discharge instructions,
 *      and redundant diagnostic notes across hospital reports.
 * 
 * Input:
 *   String s (for Z-array) or (text, pattern) for search.
 * 
 * Output:
 *   int[] Z array; List of match indices; List of repeated medical phrases.
 * 
 * Time Complexity:
 *   Z-array construction: O(n) where n = s.length()
 *   Pattern Search: O(n + m)
 * 
 * Space Complexity:
 *   O(n) auxiliary space for the Z-array.
 */
public class ZFunction {

    /**
     * Computes the Z-array for string s in O(n) time.
     * Z[i] is the length of the longest common prefix between s and s[i..n-1].
     */
    public static int[] calculateZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    /**
     * Searches for occurrences of pattern in text using the Z-algorithm.
     * Constructs string: pattern + "$" + text
     */
    public static List<Integer> search(String text, String pattern, boolean ignoreCase) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String t = ignoreCase ? text.toLowerCase() : text;
        String p = ignoreCase ? pattern.toLowerCase() : pattern;
        String concat = p + "$" + t;

        int[] z = calculateZ(concat);
        int pLen = p.length();

        for (int i = pLen + 1; i < concat.length(); i++) {
            if (z[i] == pLen) {
                matches.add(i - (pLen + 1));
            }
        }
        return matches;
    }

    public static class PhraseOccurrence {
        public final String phrase;
        public final int count;
        public final List<Integer> positions;

        public PhraseOccurrence(String phrase, int count, List<Integer> positions) {
            this.phrase = phrase;
            this.count = count;
            this.positions = positions;
        }
    }

    /**
     * Detects repeated medical phrases in clinical text using Z-function.
     * Evaluates candidate phrases of length >= minLength and frequency >= minCount.
     */
    public static List<PhraseOccurrence> findRepeatedPhrases(String text, int minLength, int minCount) {
        List<PhraseOccurrence> results = new ArrayList<>();
        if (text == null || text.length() < minLength) return results;

        Map<String, List<Integer>> phraseMap = new HashMap<>();
        int n = text.length();

        // Examine suffix substrings
        for (int i = 0; i <= n - minLength; i++) {
            String suffix = text.substring(i);
            int[] z = calculateZ(suffix);
            for (int j = 1; j < suffix.length(); j++) {
                if (z[j] >= minLength) {
                    int len = z[j];
                    String phrase = suffix.substring(j, j + len).trim();
                    if (phrase.length() >= minLength && phrase.contains(" ")) {
                        phraseMap.putIfAbsent(phrase, new ArrayList<>());
                        int absPos = i + j;
                        if (!phraseMap.get(phrase).contains(absPos)) {
                            phraseMap.get(phrase).add(absPos);
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, List<Integer>> entry : phraseMap.entrySet()) {
            if (entry.getValue().size() + 1 >= minCount) {
                // +1 because initial prefix was also an occurrence
                results.add(new PhraseOccurrence(entry.getKey(), entry.getValue().size() + 1, entry.getValue()));
            }
        }

        results.sort((a, b) -> Integer.compare(b.phrase.length(), a.phrase.length()));
        return results;
    }
}
