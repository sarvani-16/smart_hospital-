package smarthospital.m2_suffix;

import java.util.ArrayList;
import java.util.List;

/**
 * Longest Common Prefix (LCP) Array Utilities.
 * 
 * Purpose:
 *   Wraps the LCP array and provides analysis methods for:
 *   - Finding the longest repeated medical phrase or substring.
 *   - Counting the exact number of distinct medical substrings/tokens.
 *   - Detecting all recurring clinical sentences/phrases above a length threshold.
 * 
 * Input:
 *   Text string, Suffix Array, and LCP array.
 * 
 * Output:
 *   Distinct substring count, longest repeated substring, and list of repeated phrases.
 * 
 * Time Complexity:
 *   Longest Repeated Substring: O(N)
 *   Distinct Substrings: O(N)
 *   Repeated Phrase Search: O(N)
 * 
 * Space Complexity:
 *   O(N) storage.
 */
public class LCPArray {

    private final String text;
    private final int[] sa;
    private final int[] lcp;

    public LCPArray(String text, int[] sa) {
        this.text = text;
        this.sa = sa;
        this.lcp = KasaiAlgorithm.constructLCP(text, sa);
    }

    public int[] getLCP() {
        return lcp;
    }

    /**
     * Finds the longest repeated substring in the medical text.
     */
    public String getLongestRepeatedSubstring() {
        int maxLen = 0;
        int maxIndex = -1;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                maxIndex = sa[i];
            }
        }

        if (maxLen == 0 || maxIndex == -1) return "";
        return text.substring(maxIndex, maxIndex + maxLen);
    }

    /**
     * Counts total distinct substrings in the medical text using:
     * Total = N * (N + 1) / 2 - Sum(LCP[i])
     */
    public long countDistinctSubstrings() {
        long n = text.length();
        long total = n * (n + 1) / 2;
        long sumLcp = 0;
        for (int val : lcp) {
            sumLcp += val;
        }
        return total - sumLcp;
    }

    public static class RepeatedSubstringInfo {
        public final String phrase;
        public final int length;
        public final int position1;
        public final int position2;

        public RepeatedSubstringInfo(String phrase, int length, int pos1, int pos2) {
            this.phrase = phrase;
            this.length = length;
            this.position1 = pos1;
            this.position2 = pos2;
        }

        @Override
        public String toString() {
            return String.format("'%s' (Len: %d) at indices %d & %d", phrase, length, position1, position2);
        }
    }

    /**
     * Identifies all repeated medical phrases with length >= minLength.
     */
    public List<RepeatedSubstringInfo> findRepeatedPhrases(int minLength) {
        List<RepeatedSubstringInfo> phrases = new ArrayList<>();
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] >= minLength) {
                String sub = text.substring(sa[i], sa[i] + lcp[i]).trim();
                if (sub.length() >= minLength) {
                    phrases.add(new RepeatedSubstringInfo(sub, lcp[i], sa[i - 1], sa[i]));
                }
            }
        }
        return phrases;
    }
}
