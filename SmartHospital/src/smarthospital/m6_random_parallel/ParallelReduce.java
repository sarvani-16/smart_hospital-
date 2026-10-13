package smarthospital.m6_random_parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Parallel Reduce using Java ForkJoin Framework for Hospital Analytics.
 * 
 * Purpose:
 *   Performs parallel divide-and-conquer associative reductions on hospital telemetry
 *   and patient analytics data (total billing, mean patient age, maximum vital abnormality,
 *   aggregate bed occupancy).
 * 
 * Algorithm:
 *   Recursive binary splitting until reaching a base threshold, followed by tree recombination.
 * 
 * Complexity:
 *   Work W: O(N) operations.
 *   Span / Critical Path D: O(log N) parallel depth.
 * 
 * Space Complexity:
 *   O(log N) stack frames across the ForkJoin worker thread pool.
 */
public class ParallelReduce {

    private static final int THRESHOLD = 500;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static class ReductionMetrics {
        public final double sum;
        public final double min;
        public final double max;
        public final double average;
        public final int count;
        public final long parallelDurationNanos;
        public final long sequentialDurationNanos;

        public ReductionMetrics(double sum, double min, double max, int count, long parTime, long seqTime) {
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.count = count;
            this.average = (count == 0) ? 0.0 : sum / count;
            this.parallelDurationNanos = parTime;
            this.sequentialDurationNanos = seqTime;
        }

        @Override
        public String toString() {
            return String.format("Reduction Metrics (N = %d):\n" +
                    "  Sum: %.2f | Mean: %.2f | Min: %.2f | Max: %.2f\n" +
                    "  Parallel Duration  : %,d ns\n" +
                    "  Sequential Duration: %,d ns\n" +
                    "  Speedup Factor     : %.2fx",
                    count, sum, average, min, max, parallelDurationNanos, sequentialDurationNanos,
                    (parallelDurationNanos == 0 ? 1.0 : (double) sequentialDurationNanos / parallelDurationNanos));
        }
    }

    private static class PartialResult {
        double sum;
        double min;
        double max;

        PartialResult(double sum, double min, double max) {
            this.sum = sum;
            this.min = min;
            this.max = max;
        }
    }

    private static class ReduceTask extends RecursiveTask<PartialResult> {
        private final double[] data;
        private final int start;
        private final int end;

        ReduceTask(double[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected PartialResult compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                double s = 0.0;
                double mn = Double.MAX_VALUE;
                double mx = -Double.MAX_VALUE;
                for (int i = start; i < end; i++) {
                    double v = data[i];
                    s += v;
                    if (v < mn) mn = v;
                    if (v > mx) mx = v;
                }
                return new PartialResult(s, mn, mx);
            } else {
                int mid = start + length / 2;
                ReduceTask leftTask = new ReduceTask(data, start, mid);
                ReduceTask rightTask = new ReduceTask(data, mid, end);

                leftTask.fork();
                PartialResult rightRes = rightTask.compute();
                PartialResult leftRes = leftTask.join();

                return new PartialResult(
                        leftRes.sum + rightRes.sum,
                        Math.min(leftRes.min, rightRes.min),
                        Math.max(leftRes.max, rightRes.max)
                );
            }
        }
    }

    /**
     * Executes parallel reduction and compares against sequential performance.
     */
    public static ReductionMetrics compute(double[] data) {
        if (data == null || data.length == 0) {
            return new ReductionMetrics(0, 0, 0, 0, 0, 0);
        }

        // Sequential timing
        long tSeqStart = System.nanoTime();
        double seqSum = 0;
        double seqMin = Double.MAX_VALUE;
        double seqMax = -Double.MAX_VALUE;
        for (double d : data) {
            seqSum += d;
            if (d < seqMin) seqMin = d;
            if (d > seqMax) seqMax = d;
        }
        long seqTime = System.nanoTime() - tSeqStart;

        // Parallel timing
        long tParStart = System.nanoTime();
        ReduceTask mainTask = new ReduceTask(data, 0, data.length);
        PartialResult parRes = POOL.invoke(mainTask);
        long parTime = System.nanoTime() - tParStart;

        return new ReductionMetrics(parRes.sum, parRes.min, parRes.max, data.length, parTime, seqTime);
    }
}
