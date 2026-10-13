package smarthospital.m2_suffix;

import java.util.Arrays;

/**
 * SA-IS (Suffix Array Induced Sorting) Algorithm.
 * 
 * Purpose:
 *   Constructs the Suffix Array in guaranteed linear time O(N) for large-scale
 *   hospital diagnostic report indexing and medical corpus processing.
 * 
 * Input:
 *   Text string representing large medical document logs.
 * 
 * Output:
 *   int[] suffix array SA of length N.
 * 
 * Time Complexity:
 *   O(N) deterministic linear time.
 * 
 * Space Complexity:
 *   O(N) working memory.
 */
public class SAIS {

    private static final boolean S_TYPE = false;
    private static final boolean L_TYPE = true;

    /**
     * Builds Suffix Array using SA-IS algorithm in O(N) time.
     */
    public static int[] buildSuffixArray(String text) {
        if (text == null) return new int[0];
        int n = text.length();
        if (n == 0) return new int[0];
        if (n == 1) return new int[]{0};

        int[] s = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            s[i] = text.charAt(i) + 1; // Shift by 1 to reserve 0 for sentinel
            if (s[i] > maxChar) maxChar = s[i];
        }
        s[n] = 0; // Sentinel 0 (strictly smaller than all other characters)

        int[] sa = sais(s, maxChar + 1);

        // Exclude the sentinel from the final suffix array
        int[] result = new int[n];
        System.arraycopy(sa, 1, result, 0, n);
        return result;
    }

    private static int[] sais(int[] s, int alphabetSize) {
        int n = s.length;
        if (n <= 1) {
            int[] sa = new int[n];
            sa[0] = 0;
            return sa;
        }
        if (n == 2) {
            int[] sa = new int[2];
            if (s[0] < s[1]) {
                sa[0] = 0;
                sa[1] = 1;
            } else {
                sa[0] = 1;
                sa[1] = 0;
            }
            return sa;
        }

        // Step 1: Classify characters as S-type or L-type
        boolean[] t = new boolean[n];
        t[n - 1] = S_TYPE;
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                t[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                t[i] = L_TYPE;
            } else {
                t[i] = t[i + 1];
            }
        }

        // Bucket sizes
        int[] buckets = new int[alphabetSize];
        for (int val : s) {
            buckets[val]++;
        }

        // Find LMS characters
        int[] lmsMap = new int[n];
        Arrays.fill(lmsMap, -1);
        int m = 0;
        for (int i = 1; i < n; i++) {
            if (t[i] == S_TYPE && t[i - 1] == L_TYPE) {
                lmsMap[i] = m++;
            }
        }

        int[] lms = new int[m];
        int idx = 0;
        for (int i = 1; i < n; i++) {
            if (t[i] == S_TYPE && t[i - 1] == L_TYPE) {
                lms[idx++] = i;
            }
        }

        int[] sa = new int[n];
        Arrays.fill(sa, -1);

        // Induced sort with initial LMS placement
        inducedSort(s, sa, t, buckets, alphabetSize, lms);

        if (m > 0) {
            // Compact sorted LMS substrings
            int[] sortedLMS = new int[m];
            int sortedIdx = 0;
            for (int val : sa) {
                if (val > 0 && lmsMap[val] != -1) {
                    sortedLMS[sortedIdx++] = val;
                }
            }

            // Assign names to LMS substrings
            int[] summaryS = new int[m];
            int name = 0;
            summaryS[lmsMap[sortedLMS[0]]] = 0;

            for (int i = 1; i < m; i++) {
                int prev = sortedLMS[i - 1];
                int curr = sortedLMS[i];
                boolean diff = false;

                for (int d = 0; ; d++) {
                    if (s[prev + d] != s[curr + d] || t[prev + d] != t[curr + d]) {
                        diff = true;
                        break;
                    }
                    if (d > 0 && (lmsMap[prev + d] != -1 || lmsMap[curr + d] != -1)) {
                        break;
                    }
                }

                if (diff) name++;
                summaryS[lmsMap[curr]] = name;
            }

            int[] summarySA;
            if (name + 1 < m) {
                summarySA = sais(summaryS, name + 1);
            } else {
                summarySA = new int[m];
                for (int i = 0; i < m; i++) {
                    summarySA[summaryS[i]] = i;
                }
            }

            // Map back to LMS positions
            int[] orderedLMS = new int[m];
            for (int i = 0; i < m; i++) {
                orderedLMS[i] = lms[summarySA[i]];
            }

            // Final induced sort
            Arrays.fill(sa, -1);
            inducedSort(s, sa, t, buckets, alphabetSize, orderedLMS);
        }

        return sa;
    }

    private static void inducedSort(int[] s, int[] sa, boolean[] t, int[] buckets, int alphabetSize, int[] lms) {
        int n = s.length;
        int[] bucketEnds = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            sum += buckets[i];
            bucketEnds[i] = sum;
        }

        // Put LMS suffixes into end of buckets
        int[] head = Arrays.copyOf(bucketEnds, alphabetSize);
        for (int i = lms.length - 1; i >= 0; i--) {
            int j = lms[i];
            sa[--head[s[j]]] = j;
        }

        // Induced sort L-type
        int[] bucketStarts = new int[alphabetSize];
        sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            bucketStarts[i] = sum;
            sum += buckets[i];
        }

        head = Arrays.copyOf(bucketStarts, alphabetSize);
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && t[j] == L_TYPE) {
                sa[head[s[j]]++] = j;
            }
        }

        // Induced sort S-type
        head = Arrays.copyOf(bucketEnds, alphabetSize);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && t[j] == S_TYPE) {
                sa[--head[s[j]]] = j;
            }
        }
    }
}
