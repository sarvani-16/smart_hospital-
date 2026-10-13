package smarthospital.m1_string;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp Rolling Hash String Matching Algorithm.
 * 
 * Purpose:
 *   Searches for diagnostic codes (such as ICD-10 disease classification codes,
 *   lab test panel codes, or patient record barcodes) using polynomial rolling hash.
 *   Calculates hash values efficiently in O(1) time per window slide.
 * 
 * Input:
 *   text: The document or medical record text containing diagnostic metadata.
 *   pattern: The diagnostic code or alphanumeric identifier to locate.
 * 
 * Output:
 *   List of match indices where the diagnostic code appears.
 * 
 * Time Complexity:
 *   Average Case: O(n + m) where n = text.length(), m = pattern.length()
 *   Worst Case: O(n * m) (with hash collisions)
 * 
 * Space Complexity:
 *   O(1) auxiliary space (constant memory for rolling hash variables).
 */
public class RabinKarp {

    private static final int BASE = 256;
    private static final long MOD = 1000000007L;

    /**
     * Searches for pattern in text using Rabin-Karp rolling hash.
     */
    public static List<Integer> search(String text, String pattern, boolean ignoreCase) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String t = ignoreCase ? text.toUpperCase() : text;
        String p = ignoreCase ? pattern.toUpperCase() : pattern;

        int n = t.length();
        int m = p.length();

        // Precompute BASE^(m-1) % MOD
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MOD;
        }

        long patternHash = 0;
        long textHash = 0;

        // Compute hash of pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + p.charAt(i)) % MOD;
            textHash = (BASE * textHash + t.charAt(i)) % MOD;
        }

        // Slide over text
        for (int i = 0; i <= n - m; i++) {
            // Check hash match
            if (patternHash == textHash) {
                // Verify character-by-character to eliminate spurious hits
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (t.charAt(i + j) != p.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Calculate rolling hash for next window
            if (i < n - m) {
                textHash = (BASE * (textHash - t.charAt(i) * h) + t.charAt(i + m)) % MOD;
                if (textHash < 0) {
                    textHash += MOD;
                }
            }
        }
        return matches;
    }

    /**
     * Checks if diagnostic code exists in document path or text.
     */
    public static boolean contains(String text, String code) {
        return !search(text, code, true).isEmpty();
    }
}
