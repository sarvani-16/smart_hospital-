package smarthospital.m2_suffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Suffix Array for Medical Record Indexing.
 * 
 * Purpose:
 *   Builds a lexicographically sorted array of all suffixes of clinical text,
 *   enabling fast binary search substring queries across indexed medical records.
 * 
 * Input:
 *   Text string (e.g. concatenated medical records or clinical reports).
 * 
 * Output:
 *   Integer array SA where SA[i] is the starting index of the i-th lexicographical suffix.
 * 
 * Time Complexity:
 *   Construction: O(N log^2 N) using Prefix Doubling algorithm.
 *   Pattern Search: O(M log N) where M is pattern length, N is text length.
 * 
 * Space Complexity:
 *   O(N) to store suffix ranks and the suffix array.
 */
public class SuffixArray {

    private final String text;
    private final int[] sa;
    private final int n;

    private static class Suffix {
        int index;
        int rank;
        int nextRank;
    }

    public SuffixArray(String text) {
        this.text = text;
        this.n = text.length();
        this.sa = buildSuffixArray(text);
    }

    public int[] getSA() {
        return sa;
    }

    public String getText() {
        return text;
    }

    /**
     * Builds Suffix Array using Prefix Doubling in O(N log^2 N).
     */
    private int[] buildSuffixArray(String s) {
        int len = s.length();
        Suffix[] suffixes = new Suffix[len];

        for (int i = 0; i < len; i++) {
            suffixes[i] = new Suffix();
            suffixes[i].index = i;
            suffixes[i].rank = s.charAt(i);
            suffixes[i].nextRank = (i + 1 < len) ? s.charAt(i + 1) : -1;
        }

        Arrays.sort(suffixes, (a, b) -> {
            if (a.rank != b.rank) return Integer.compare(a.rank, b.rank);
            return Integer.compare(a.nextRank, b.nextRank);
        });

        int[] ind = new int[len];
        for (int k = 4; k < 2 * len; k *= 2) {
            int rank = 0;
            int prevRank = suffixes[0].rank;
            suffixes[0].rank = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < len; i++) {
                if (suffixes[i].rank == prevRank && suffixes[i].nextRank == suffixes[i - 1].nextRank) {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = rank;
                } else {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < len; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].nextRank = (nextIndex < len) ? suffixes[ind[nextIndex]].rank : -1;
            }

            Arrays.sort(suffixes, (a, b) -> {
                if (a.rank != b.rank) return Integer.compare(a.rank, b.rank);
                return Integer.compare(a.nextRank, b.nextRank);
            });
        }

        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = suffixes[i].index;
        }
        return result;
    }

    /**
     * Searches for all occurrences of pattern in indexed text in O(M log N).
     */
    public List<Integer> search(String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || n == 0) return occurrences;

        int m = pattern.length();
        int l = 0, r = n - 1;
        int lowerBound = -1;

        // Binary search lower bound
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int cmp = compareSuffix(sa[mid], pattern);
            if (cmp >= 0) {
                if (cmp == 0) lowerBound = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        if (lowerBound == -1) return occurrences;

        // Binary search upper bound
        l = lowerBound;
        r = n - 1;
        int upperBound = lowerBound;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int cmp = compareSuffix(sa[mid], pattern);
            if (cmp == 0) {
                upperBound = mid;
                l = mid + 1;
            } else if (cmp < 0) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }

        for (int i = lowerBound; i <= upperBound; i++) {
            occurrences.add(sa[i]);
        }
        occurrences.sort(Comparator.naturalOrder());
        return occurrences;
    }

    private int compareSuffix(int suffixStart, String pattern) {
        int m = pattern.length();
        int rem = n - suffixStart;
        int checkLen = Math.min(m, rem);

        for (int i = 0; i < checkLen; i++) {
            char sc = text.charAt(suffixStart + i);
            char pc = pattern.charAt(i);
            if (sc != pc) return Character.compare(sc, pc);
        }

        if (rem < m) return -1; // Suffix is shorter than pattern
        return 0; // Matches prefix
    }
}
