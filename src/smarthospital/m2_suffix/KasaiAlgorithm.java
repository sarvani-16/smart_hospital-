package smarthospital.m2_suffix;

/**
 * Kasai's Algorithm for Linear-Time LCP Array Construction.
 * 
 * Purpose:
 *   Computes the Longest Common Prefix (LCP) array from a text and its Suffix Array in O(N) time.
 *   Used in SmartHospital for identifying repeated medical phrases, duplicate clinical notes,
 *   and recurring diagnostic patterns.
 * 
 * Input:
 *   text: The clinical text.
 *   sa: The Suffix Array of the text.
 * 
 * Output:
 *   int[] lcp where lcp[i] is the length of the longest common prefix between
 *   suffix sa[i] and suffix sa[i - 1] (with lcp[0] = 0).
 * 
 * Time Complexity:
 *   O(N) deterministic linear time.
 * 
 * Space Complexity:
 *   O(N) to store the inverse suffix array (rank array) and LCP array.
 */
public class KasaiAlgorithm {

    /**
     * Constructs LCP array using Kasai's algorithm in O(N) time.
     */
    public static int[] constructLCP(String text, int[] sa) {
        int n = text.length();
        if (n == 0 || sa == null || sa.length == 0) return new int[0];

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            } else {
                lcp[0] = 0;
            }
        }

        return lcp;
    }
}
