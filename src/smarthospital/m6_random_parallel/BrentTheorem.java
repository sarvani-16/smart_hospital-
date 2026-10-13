package smarthospital.m6_random_parallel;

import java.util.ArrayList;
import java.util.List;

/**
 * Brent's Scheduling Principle / Brent's Theorem Analysis.
 * 
 * Purpose:
 *   Models and analyzes parallel scalability for heavy hospital data-processing pipelines
 *   (e.g., batch processing of 100,000 patient records, real-time ICU sensor analytics,
 *   epidemiological pattern mining).
 * 
 * Formal Theorem:
 *   Any parallel computation with work W (total operations) and span D (critical path depth)
 *   can be executed on p processors in time T_p bounded by:
 *       T_p <= (W - D) / p + D  <=  W / p + D
 * 
 * Metrics:
 *   - Work W = T_1 (Sequential execution time)
 *   - Span D = T_infinity (Shortest possible execution time on unlimited processors)
 *   - Inherent Parallelism = W / D
 *   - Theoretical Speedup S_p = T_1 / T_p <= min(p, W / D)
 *   - Efficiency E_p = S_p / p
 * 
 * Time Complexity:
 *   O(1) analytical calculation; O(K * p) for empirical benchmarks.
 * 
 * Space Complexity:
 *   O(1) memory.
 */
public class BrentTheorem {

    public static class ProcessorEvaluation {
        public final int p;
        public final double brentBoundTp;
        public final double theoreticalSpeedup;
        public final double efficiency;

        public ProcessorEvaluation(int p, double brentBoundTp, double speedup, double efficiency) {
            this.p = p;
            this.brentBoundTp = brentBoundTp;
            this.theoreticalSpeedup = speedup;
            this.efficiency = efficiency;
        }

        @Override
        public String toString() {
            return String.format("  p = %2d cores | Brent Upper Bound T_p: %8.2f us | Speedup: %5.2fx | Efficiency: %5.1f%%",
                    p, brentBoundTp, theoreticalSpeedup, efficiency * 100.0);
        }
    }

    public static class AnalysisReport {
        public final double workW;
        public final double spanD;
        public final double parallelism;
        public final List<ProcessorEvaluation> evaluations;

        public AnalysisReport(double workW, double spanD, double parallelism, List<ProcessorEvaluation> evals) {
            this.workW = workW;
            this.spanD = spanD;
            this.parallelism = parallelism;
            this.evaluations = evals;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=================================================================\n");
            sb.append("           BRENT'S THEOREM PARALLEL PROCESSING ANALYSIS          \n");
            sb.append("=================================================================\n");
            sb.append(String.format("  Total Work (W = T_1)               : %,10.2f us\n", workW));
            sb.append(String.format("  Critical Path Span (D = T_inf)     : %,10.2f us\n", spanD));
            sb.append(String.format("  Inherent Parallelism (W / D)       : %,10.2f (Max effective cores)\n", parallelism));
            sb.append("-----------------------------------------------------------------\n");
            sb.append("  Multi-Core Performance Bounds (Brent's Bound: T_p <= (W - D)/p + D):\n");
            for (ProcessorEvaluation pe : evaluations) {
                sb.append(pe.toString()).append("\n");
            }
            sb.append("=================================================================\n");
            return sb.toString();
        }
    }

    /**
     * Computes Brent's theorem bounds for a given Work W and Span D.
     */
    public static AnalysisReport analyze(double workW, double spanD, int[] processorCounts) {
        double parallelism = spanD > 0 ? (workW / spanD) : 0;
        List<ProcessorEvaluation> evals = new ArrayList<>();

        for (int p : processorCounts) {
            double tp = ((workW - spanD) / p) + spanD;
            double speedup = workW / tp;
            double efficiency = speedup / p;
            evals.add(new ProcessorEvaluation(p, tp, speedup, efficiency));
        }

        return new AnalysisReport(workW, spanD, parallelism, evals);
    }
}
