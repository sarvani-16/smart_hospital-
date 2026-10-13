package smarthospital.m3_dynamic;

import java.util.*;

/**
 * Bitmask Dynamic Programming for Optimal Diagnostic-Test Combination Selection.
 * 
 * Purpose:
 *   Selects the optimal, lowest-cost combination of diagnostic tests and lab panels
 *   that covers all active patient symptoms and diagnostic markers.
 *   Uses a bitmask where the i-th bit represents coverage of the i-th symptom.
 * 
 * Input:
 *   List of target patient symptoms.
 *   Catalog of diagnostic tests (each covering a subset of symptoms with a cost).
 * 
 * Output:
 *   Optimal battery of tests, total minimum cost, and complete symptom coverage.
 * 
 * Time Complexity:
 *   O(T * 2^N) where N is number of symptoms (N <= 16) and T is number of available tests.
 * 
 * Space Complexity:
 *   O(2^N) for DP state array and parent tracking pointers.
 */
public class BitmaskDP {

    public static class DiagnosticTest {
        public final String id;
        public final String name;
        public final double cost;
        public final int symptomMask;
        public final List<String> coversSymptoms;

        public DiagnosticTest(String id, String name, double cost, int symptomMask, List<String> coversSymptoms) {
            this.id = id;
            this.name = name;
            this.cost = cost;
            this.symptomMask = symptomMask;
            this.coversSymptoms = coversSymptoms;
        }

        @Override
        public String toString() {
            return String.format("[%s] %-24s | Cost: $%-6.1f | Covers: %s", id, name, cost, coversSymptoms);
        }
    }

    public static class OptimalPanelResult {
        public final double totalCost;
        public final List<DiagnosticTest> selectedTests;
        public final int coveredMask;
        public final int targetMask;

        public OptimalPanelResult(double totalCost, List<DiagnosticTest> selectedTests, int coveredMask, int targetMask) {
            this.totalCost = totalCost;
            this.selectedTests = selectedTests;
            this.coveredMask = coveredMask;
            this.targetMask = targetMask;
        }
    }

    public static final List<String> DEFAULT_SYMPTOMS = List.of(
            "Chest Pain",             // bit 0
            "Shortness of Breath",    // bit 1
            "Cardiac Arrhythmia",     // bit 2
            "Elevated Troponin",      // bit 3
            "High Fever",             // bit 4
            "Productive Cough",       // bit 5
            "Severe Headache",        // bit 6
            "Focal Neurologic Deficit"// bit 7
    );

    public static List<DiagnosticTest> getDefaultCatalog(List<String> symptomList) {
        List<DiagnosticTest> catalog = new ArrayList<>();
        catalog.add(createTest("T-ECG", "12-Lead ECG", 65.0, List.of("Chest Pain", "Cardiac Arrhythmia"), symptomList));
        catalog.add(createTest("T-TROP", "Cardiac Troponin-I Panel", 120.0, List.of("Chest Pain", "Elevated Troponin"), symptomList));
        catalog.add(createTest("T-CXR", "Digital Chest X-Ray", 95.0, List.of("Shortness of Breath", "Productive Cough"), symptomList));
        catalog.add(createTest("T-CBC", "Complete Blood Count & ESR", 45.0, List.of("High Fever", "Productive Cough"), symptomList));
        catalog.add(createTest("T-CTB", "Brain Non-Contrast CT", 350.0, List.of("Severe Headache", "Focal Neurologic Deficit"), symptomList));
        catalog.add(createTest("T-CTA", "CT Pulmonary Angiogram", 480.0, List.of("Chest Pain", "Shortness of Breath"), symptomList));
        catalog.add(createTest("T-ECHO", "Transthoracic Echocardiogram", 280.0, List.of("Chest Pain", "Shortness of Breath", "Cardiac Arrhythmia"), symptomList));
        catalog.add(createTest("T-RESP", "Multiplex Respiratory PCR Panel", 160.0, List.of("High Fever", "Productive Cough", "Shortness of Breath"), symptomList));
        return catalog;
    }

    private static DiagnosticTest createTest(String id, String name, double cost, List<String> covers, List<String> allSymptoms) {
        int mask = 0;
        for (String s : covers) {
            int idx = allSymptoms.indexOf(s);
            if (idx != -1) {
                mask |= (1 << idx);
            }
        }
        return new DiagnosticTest(id, name, cost, mask, covers);
    }

    /**
     * Solves the minimum-cost test battery selection using Bitmask DP.
     */
    public static OptimalPanelResult solve(List<String> targetSymptoms, List<DiagnosticTest> tests) {
        int n = targetSymptoms.size();
        int targetMask = (1 << n) - 1;
        int stateCount = 1 << n;

        double[] dp = new double[stateCount];
        int[] parentMask = new int[stateCount];
        int[] testUsed = new int[stateCount];

        Arrays.fill(dp, Double.MAX_VALUE);
        Arrays.fill(parentMask, -1);
        Arrays.fill(testUsed, -1);

        dp[0] = 0.0;

        for (int mask = 0; mask < stateCount; mask++) {
            if (dp[mask] == Double.MAX_VALUE) continue;

            for (int t = 0; t < tests.size(); t++) {
                DiagnosticTest test = tests.get(t);
                int nextMask = mask | (test.symptomMask & targetMask);
                double nextCost = dp[mask] + test.cost;

                if (nextCost < dp[nextMask]) {
                    dp[nextMask] = nextCost;
                    parentMask[nextMask] = mask;
                    testUsed[nextMask] = t;
                }
            }
        }

        // Traceback optimal tests
        List<DiagnosticTest> selected = new ArrayList<>();
        int curr = targetMask;

        if (dp[targetMask] == Double.MAX_VALUE) {
            // Find best reachable mask if 100% coverage cannot be satisfied by catalog
            int bestMask = 0;
            for (int m = 0; m < stateCount; m++) {
                if (dp[m] != Double.MAX_VALUE && Integer.bitCount(m) > Integer.bitCount(bestMask)) {
                    bestMask = m;
                }
            }
            curr = bestMask;
        }

        double totalCost = dp[curr];
        int coveredMask = curr;

        while (curr > 0 && testUsed[curr] != -1) {
            selected.add(tests.get(testUsed[curr]));
            curr = parentMask[curr];
        }

        Collections.reverse(selected);
        return new OptimalPanelResult(totalCost, selected, coveredMask, targetMask);
    }
}
