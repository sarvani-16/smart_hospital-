package smarthospital.m6_random_parallel;

import java.util.Arrays;

/**
 * Blelloch Work-Efficient Parallel Prefix Scan Algorithm.
 * 
 * Purpose:
 *   Computes prefix sums (cumulative running totals) with optimal work efficiency.
 *   Used in SmartHospital for:
 *   - Aggregating running patient expenses and billing totals.
 *   - Computing cumulative patient admissions across sequential time intervals.
 *   - Calculating cumulative drug infusion dosages over telemetry observation streams.
 * 
 * Algorithm:
 *   1. Up-Sweep (Reduce Phase):
 *      Traverses bottom-up from leaves to root in O(log N) depth, building partial sums.
 *   2. Down-Sweep Phase:
 *      Sets root to 0 (identity) and traverses top-down in O(log N) depth, propagating
 *      prefix sums to each element.
 * 
 * Complexity:
 *   Work: O(N) operations (same work as optimal sequential scan).
 *   Span / Depth: O(log N) parallel time steps.
 * 
 * Space Complexity:
 *   O(N) working memory (padded to next power of 2).
 */
public class BlellochScan {

    public static class ScanResult {
        public final double[] original;
        public final double[] exclusivePrefix;
        public final double[] inclusivePrefix;
        public final double totalSum;
        public final int work;
        public final int span;

        public ScanResult(double[] original, double[] exclusive, double[] inclusive, double totalSum, int work, int span) {
            this.original = original;
            this.exclusivePrefix = exclusive;
            this.inclusivePrefix = inclusive;
            this.totalSum = totalSum;
            this.work = work;
            this.span = span;
        }
    }

    /**
     * Executes work-efficient parallel Blelloch scan on an array of doubles.
     */
    public static ScanResult scan(double[] input) {
        if (input == null || input.length == 0) {
            return new ScanResult(new double[0], new double[0], new double[0], 0.0, 0, 0);
        }

        int origLen = input.length;
        // Pad to next power of 2
        int n = 1;
        while (n < origLen) n <<= 1;

        double[] a = new double[n];
        System.arraycopy(input, 0, a, 0, origLen);

        int workCount = 0;
        int spanDepth = 0;

        // Phase 1: Up-Sweep (Reduce)
        for (int d = 1; d < n; d <<= 1) {
            spanDepth++;
            for (int k = 0; k < n; k += (d << 1)) {
                a[k + (d << 1) - 1] += a[k + d - 1];
                workCount++;
            }
        }

        double totalSum = a[n - 1];

        // Phase 2: Down-Sweep
        a[n - 1] = 0; // Set root to identity
        for (int d = n >> 1; d >= 1; d >>= 1) {
            spanDepth++;
            for (int k = 0; k < n; k += (d << 1)) {
                double temp = a[k + d - 1];
                a[k + d - 1] = a[k + (d << 1) - 1];
                a[k + (d << 1) - 1] += temp;
                workCount += 2;
            }
        }

        double[] exclusive = Arrays.copyOf(a, origLen);
        double[] inclusive = new double[origLen];
        for (int i = 0; i < origLen; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }

        return new ScanResult(input, exclusive, inclusive, totalSum, workCount, spanDepth);
    }
}
