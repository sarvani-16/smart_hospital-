package smarthospital.util;

import smarthospital.m1_string.*;
import smarthospital.m2_suffix.*;
import smarthospital.m3_dynamic.*;
import smarthospital.m4_flow.*;
import smarthospital.m5_np.*;
import smarthospital.m6_random_parallel.*;
import smarthospital.model.*;
import smarthospital.persistence.HospitalCSVManager;
import smarthospital.service.*;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Comprehensive Automated Verification Test for SmartHospital.
 * Tests every single module (M1 to M6) and CSV persistence lifecycle.
 */
public class TestSmartHospital {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    private static void assertTrue(String testName, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
            throw new AssertionError("Test failed: " + testName);
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   SMART HOSPITAL - AUTOMATED TEST SUITE        ");
        System.out.println("=================================================");

        testCSVPersistenceLifecycle();
        testM1StringAlgorithms();
        testM2SuffixStructures();
        testM3DynamicProgramming();
        testM4NetworkFlow();
        testM5NPCompletenessAndReductions();
        testM6RandomizedAndParallel();

        System.out.println("=================================================");
        System.out.printf("   ALL TESTS COMPLETED: %d / %d PASSED!        \n", testsPassed, testsTotal);
        System.out.println("=================================================");
    }

    private static void testCSVPersistenceLifecycle() {
        System.out.println("\n[Testing CSV Persistence Lifecycle]");
        try {
            String testCsvPath = "data/test_hospital_data.csv";
            Path p = Paths.get(testCsvPath);
            Files.deleteIfExists(p);

            // 1. Initial creation
            HospitalCSVManager mgr1 = new HospitalCSVManager(testCsvPath);
            mgr1.initializeFile();
            assertTrue("CSV file created on first run", Files.exists(p));
            int initialCount = mgr1.getTotalRecordCount();
            assertTrue("Sample data seeded (count >= 70)", initialCount >= 70);

            // 2. Add patient
            Patient pNew = new Patient("P999", "Test Patient Automated", 35, "Male", "+91-99999-00000", "O+", "Hypertension Test", "No prior issues");
            mgr1.savePatient(pNew);
            assertTrue("New patient added in-memory", mgr1.getPatients().containsKey("P999"));

            // 3. Re-open file in fresh manager
            HospitalCSVManager mgr2 = new HospitalCSVManager(testCsvPath);
            mgr2.initializeFile();
            assertTrue("Patient P999 persisted and reloaded", mgr2.getPatients().containsKey("P999"));
            assertTrue("Sample data NOT duplicated on subsequent run", mgr2.getTotalRecordCount() == initialCount + 1);

            // 4. Update patient
            pNew.setName("Test Patient Updated");
            mgr2.savePatient(pNew);

            HospitalCSVManager mgr3 = new HospitalCSVManager(testCsvPath);
            mgr3.initializeFile();
            assertTrue("Patient update persisted", "Test Patient Updated".equals(mgr3.getPatients().get("P999").getName()));

            // 5. Delete patient
            mgr3.deleteRecord("PATIENT", "P999");
            HospitalCSVManager mgr4 = new HospitalCSVManager(testCsvPath);
            mgr4.initializeFile();
            assertTrue("Patient deletion persisted", !mgr4.getPatients().containsKey("P999"));
            assertTrue("Count restored to initial count", mgr4.getTotalRecordCount() == initialCount);

            Files.deleteIfExists(p);
            System.out.println("  CSV Persistence tests passed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("Persistence exception: " + e.getMessage(), false);
        }
    }

    private static void testM1StringAlgorithms() {
        System.out.println("\n[Testing M1 - String Algorithms]");
        String text = "Patient Rahul Kumar presents with Type-2 Diabetes Mellitus and hypertension.";
        
        // 1. KMP
        List<Integer> kmpHits = KMP.search(text, "Diabetes", true);
        assertTrue("KMP finds 'Diabetes'", !kmpHits.isEmpty() && kmpHits.get(0) == 41);

        // 2. Z-Function
        List<Integer> zHits = ZFunction.search(text, "hypertension", true);
        assertTrue("Z-Function finds 'hypertension'", !zHits.isEmpty());

        List<ZFunction.PhraseOccurrence> repeated = ZFunction.findRepeatedPhrases("blood pressure normal, blood pressure normal, blood pressure normal", 10, 2);
        assertTrue("Z-Function detects repeated phrases", !repeated.isEmpty());

        // 3. Rabin-Karp
        List<Integer> rkHits = RabinKarp.search("Record Code ICD10-E11.4 verified", "ICD10-E11.4", true);
        assertTrue("Rabin-Karp finds diagnostic code", !rkHits.isEmpty() && rkHits.get(0) == 12);

        // 4. Aho-Corasick
        AhoCorasick ac = new AhoCorasick(List.of("diabetes", "asthma", "hypertension", "stroke"));
        List<AhoCorasick.MatchResult> acHits = ac.searchInText(text);
        assertTrue("Aho-Corasick finds both 'diabetes' and 'hypertension'", acHits.size() >= 2);
    }

    private static void testM2SuffixStructures() {
        System.out.println("\n[Testing M2 - Suffix Structures]");
        String doc = "cardiomyopathy and arrhythmia observed in cardiology";

        // 1. Suffix Array
        SuffixArray sa = new SuffixArray(doc);
        List<Integer> saHits = sa.search("cardio");
        assertTrue("Suffix Array finds 'cardio' (2 occurrences)", saHits.size() == 2);

        // 2. SA-IS
        int[] saisArr = SAIS.buildSuffixArray(doc);
        assertTrue("SA-IS output array length equals text length", saisArr.length == doc.length());

        // 3. LCP Array & Kasai
        int[] lcp = KasaiAlgorithm.constructLCP(doc, sa.getSA());
        assertTrue("Kasai LCP constructed", lcp.length == doc.length());
        LCPArray lcpUtil = new LCPArray(doc, sa.getSA());
        assertTrue("LCP distinct substrings count is positive", lcpUtil.countDistinctSubstrings() > 0);

        // 4. Suffix Tree
        SuffixTree tree = new SuffixTree("pancreatitis and bronchitis");
        assertTrue("Suffix Tree contains 'creati'", tree.contains("creati"));
        assertTrue("Suffix Tree contains 'chiti'", tree.contains("chiti"));

        // 5. Suffix Automaton
        SuffixAutomaton sam = new SuffixAutomaton("hypertension and hypotension");
        assertTrue("Suffix Automaton contains 'tension'", sam.contains("tension"));
        assertTrue("Suffix Automaton occurrence count for 'tension'", sam.countOccurrences("tension") == 2);
    }

    private static void testM3DynamicProgramming() {
        System.out.println("\n[Testing M3 - Advanced Dynamic Programming]");

        // 1. Levenshtein Distance
        int lev = LevenshteinDistance.computeDistance("diabetis", "diabetes");
        assertTrue("Levenshtein: 'diabetis' -> 'diabetes' is 1", lev == 1);

        // 2. Damerau-Levenshtein
        int levAnemia = LevenshteinDistance.computeDistance("anmeia", "anemia");
        int damAnemia = DamerauLevenshtein.computeDistance("anmeia", "anemia");
        assertTrue("Levenshtein without swap needs 2 edits", levAnemia == 2);
        assertTrue("Damerau-Levenshtein with adjacent swap needs 1 edit", damAnemia == 1);

        // 3. Weighted Edit Distance
        double wDist = WeightedEditDistance.computeDistance("0xygen", "Oxygen");
        assertTrue("Weighted Edit Distance recognizes OCR digit 0 vs letter O", wDist < 0.5);

        // 4. Needleman-Wunsch
        NeedlemanWunsch.AlignmentResult nw = NeedlemanWunsch.align("GAATTC", "GGATC");
        assertTrue("Needleman-Wunsch aligns sequences", nw.score > 0 && nw.identityPercentage > 50.0);

        // 5. Smith-Waterman
        SmithWaterman.LocalAlignmentResult sw = SmithWaterman.align("ACACACTA", "AGCACACA");
        assertTrue("Smith-Waterman finds local match", sw.maxScore > 0 && sw.alignedSeq1.length() > 0);

        // 6. Bitmask DP
        List<String> symptoms = List.of("Chest Pain", "Shortness of Breath", "Arrhythmia");
        List<BitmaskDP.DiagnosticTest> tests = List.of(
                new BitmaskDP.DiagnosticTest("T1", "ECG", 50.0, 1 | 4, List.of("Chest Pain", "Arrhythmia")),
                new BitmaskDP.DiagnosticTest("T2", "CXR", 80.0, 2, List.of("Shortness of Breath")),
                new BitmaskDP.DiagnosticTest("T3", "Combo", 110.0, 1 | 2 | 4, List.of("Chest Pain", "Shortness of Breath", "Arrhythmia"))
        );
        BitmaskDP.OptimalPanelResult opt = BitmaskDP.solve(symptoms, tests);
        assertTrue("Bitmask DP finds minimum cost panel", opt.totalCost == 110.0 && opt.selectedTests.size() == 1);
    }

    private static void testM4NetworkFlow() {
        System.out.println("\n[Testing M4 - Network Flow]");

        // 1. Ford-Fulkerson
        FordFulkerson ff = new FordFulkerson(4);
        ff.addEdge(0, 1, 10);
        ff.addEdge(0, 2, 10);
        ff.addEdge(1, 2, 2);
        ff.addEdge(1, 3, 4);
        ff.addEdge(2, 3, 9);
        int ffFlow = ff.computeMaxFlow(0, 3);
        assertTrue("Ford-Fulkerson max flow = 13", ffFlow == 13);

        // 2. Edmonds-Karp
        int[][] cap = new int[4][4];
        cap[0][1] = 10; cap[0][2] = 10;
        cap[1][2] = 2; cap[1][3] = 4;
        cap[2][3] = 9;
        EdmondsKarp.FlowResult ek = EdmondsKarp.computeMaxFlow(cap, 0, 3, null);
        assertTrue("Edmonds-Karp max flow = 13", ek.maxFlow == 13);

        // 3. Dinic
        Dinic dinic = new Dinic(4);
        dinic.addEdge(0, 1, 10);
        dinic.addEdge(0, 2, 10);
        dinic.addEdge(1, 2, 2);
        dinic.addEdge(1, 3, 4);
        dinic.addEdge(2, 3, 9);
        int dFlow = dinic.maxFlow(0, 3);
        assertTrue("Dinic max flow = 13", dFlow == 13);

        // 4. Bipartite Matching (Hopcroft-Karp)
        BipartiteMatching bpm = new BipartiteMatching();
        bpm.addDoctor("Dr. A");
        bpm.addDoctor("Dr. B");
        bpm.addCase("Case 1");
        bpm.addCase("Case 2");
        bpm.addCompatibility(0, 0);
        bpm.addCompatibility(0, 1);
        bpm.addCompatibility(1, 0);
        BipartiteMatching.MatchingResult mr = bpm.solve();
        assertTrue("Bipartite Matching size = 2", mr.maxMatchingSize == 2);

        // 5. König's Theorem
        KonigTheorem.KonigResult kr = KonigTheorem.computeMinimumVertexCover(bpm, mr);
        assertTrue("König's theorem holds (|MaxMatching| == |MinVertexCover|)", kr.theoremVerified);
    }

    private static void testM5NPCompletenessAndReductions() {
        System.out.println("\n[Testing M5 - NP-Completeness & Reductions]");

        // 1 & 2. 3-SAT & SAT Solver
        ThreeSAT threeSat = new ThreeSAT();
        SATSolver.Solution sol = threeSat.solve();
        assertTrue("Hospital 3-SAT constraints are satisfiable", sol.satisfiable);

        // 3. 3-SAT -> CLIQUE
        ThreeSATToClique.ReductionGraph rg = ThreeSATToClique.reduce(threeSat);
        assertTrue("Reduction graph has 3 * clauses vertices", rg.getVertexCount() == 3 * threeSat.getClauses().size());
        List<ThreeSATToClique.ClauseVertex> clique = ThreeSATToClique.findCliqueOfSizeK(rg, rg.targetK);
        assertTrue("CLIQUE of size k found", clique.size() == rg.targetK);

        // 4. CLIQUE -> INDEPENDENT SET
        CliqueToIndependentSet.ComplementGraph cg = CliqueToIndependentSet.reduce(rg.adj, rg.targetK);
        List<Integer> cliqueIdx = new ArrayList<>();
        for (ThreeSATToClique.ClauseVertex v : clique) cliqueIdx.add(v.id);
        assertTrue("Independent set verified in complement graph G_bar",
                CliqueToIndependentSet.verifyIndependentSet(cg, cliqueIdx));

        // 5. INDEPENDENT SET -> VERTEX COVER
        IndependentSetToVertexCover.VertexCoverReductionResult vcRes =
                IndependentSetToVertexCover.reduce(cg.complementAdj, cliqueIdx);
        assertTrue("Duality: Vertex Cover covers all edges", vcRes.isProperCover);
        assertTrue("Vertex Cover size is |V| - k", vcRes.vertexCoverSize == cg.vertexCount - rg.targetK);

        // 6. Vertex Cover 2-Approximation
        boolean[][] conflictAdj = new boolean[4][4];
        conflictAdj[0][1] = true; conflictAdj[1][0] = true;
        conflictAdj[2][3] = true; conflictAdj[3][2] = true;
        VertexCoverApproximation.ApproxCoverResult appRes =
                VertexCoverApproximation.approximateVertexCover(conflictAdj, List.of("U1", "U2", "U3", "U4"));
        assertTrue("2-Approximation finds valid cover", appRes.approxCoverSize <= 2 * appRes.matchingSize);
    }

    private static void testM6RandomizedAndParallel() {
        System.out.println("\n[Testing M6 - Randomized & Parallel Algorithms]");

        // 1. Randomized QuickSort
        List<Integer> list = new ArrayList<>(List.of(45, 12, 85, 32, 89, 39, 69, 44, 42, 1, 10));
        RandomizedQuickSort.sort(list, Integer::compare);
        boolean sorted = true;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) sorted = false;
        }
        assertTrue("Randomized QuickSort correctly sorts list", sorted);

        // 2. Reservoir Sampling
        ReservoirSampling rs = new ReservoirSampling(3, 42L);
        for (int i = 1; i <= 20; i++) {
            rs.streamObservation(new HospitalObservation("OBS" + i, "P" + i, "BP", "120/80", "Normal", "2026-09-01"));
        }
        assertTrue("Reservoir Sampling holds exactly K items", rs.getSample().size() == 3);
        assertTrue("Reservoir processed all streamed items", rs.getTotalStreamed() == 20);

        // 3. Miller-Rabin
        assertTrue("Miller-Rabin verifies prime 1000000007", MillerRabin.isPrime(1000000007L, 20));
        assertTrue("Miller-Rabin identifies composite 1000000008", !MillerRabin.isPrime(1000000008L, 20));
        BigInteger key = MillerRabin.generateHospitalSecurityKey(64, 20);
        assertTrue("Miller-Rabin generated valid large prime", MillerRabin.isProbablePrime(key, 20));

        // 4. Blelloch Scan
        double[] input = new double[]{3, 1, 7, 0, 4, 1, 6, 3};
        BlellochScan.ScanResult scanRes = BlellochScan.scan(input);
        assertTrue("Blelloch Scan total sum = 25", scanRes.totalSum == 25.0);
        assertTrue("Blelloch Scan exclusive prefix at index 3 is 11", scanRes.exclusivePrefix[3] == 11.0);
        assertTrue("Blelloch Scan inclusive prefix at index 3 is 11", scanRes.inclusivePrefix[3] == 11.0);

        // 5. Parallel Reduce
        double[] data = new double[2000];
        Arrays.fill(data, 5.0);
        ParallelReduce.ReductionMetrics red = ParallelReduce.compute(data);
        assertTrue("Parallel Reduce computes sum = 10000", red.sum == 10000.0);
        assertTrue("Parallel Reduce computes mean = 5.0", red.average == 5.0);

        // 6. Brent's Theorem
        BrentTheorem.AnalysisReport bReport = BrentTheorem.analyze(10000, 100, new int[]{1, 2, 4, 8});
        assertTrue("Brent's Theorem parallelism is W / D = 100", bReport.parallelism == 100.0);
        assertTrue("Brent's Theorem computes p evaluations", bReport.evaluations.size() == 4);
    }
}
