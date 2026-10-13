package smarthospital.m1_string;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) Pattern Matching Algorithm.
 * 
 * Purpose:
 *   Searches for occurrences of a disease name or medical term within clinical text,
 *   patient disease descriptions, medical histories, diagnoses, and treatment plans.
 *   Avoids redundant backtracking by precomputing the Longest Proper Prefix which is
 *   also a Suffix (LPS array).
 * 
 * Input:
 *   text: The clinical text to search within.
 *   pattern: The disease or medical term to locate.
 * 
 * Output:
 *   List of 0-based starting indices where the pattern occurs in the text.
 * 
 * Time Complexity:
 *   Preprocessing: O(m) where m = pattern.length()
 *   Search: O(n) where n = text.length()
 *   Total Time: O(n + m)
 * 
 * Space Complexity:
 *   O(m) for the LPS array.
 */
public class KMP {

    /**
     * Computes the Longest Proper Prefix which is also Suffix (LPS) array.
     */
    public static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // Length of previous longest prefix suffix
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // Fallback to previous longest prefix
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for all occurrences of pattern in text using KMP.
     * Case-insensitive matching is supported.
     */
    public static List<Integer> search(String text, String pattern, boolean ignoreCase) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String t = ignoreCase ? text.toLowerCase() : text;
        String p = ignoreCase ? pattern.toLowerCase() : pattern;

        int[] lps = computeLPS(p);
        int n = t.length();
        int m = p.length();
        int i = 0; // Index for text
        int j = 0; // Index for pattern

        while (i < n) {
            if (p.charAt(j) == t.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                matches.add(i - j); // Match found at starting index (i - j)
                j = lps[j - 1];
            } else if (i < n && p.charAt(j) != t.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Checks if text contains pattern.
     */
    public static boolean contains(String text, String pattern, boolean ignoreCase) {
        List<Integer> occurrences = search(text, pattern, ignoreCase);
        return !occurrences.isEmpty();
    }
}
