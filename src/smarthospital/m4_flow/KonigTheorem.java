package smarthospital.m4_flow;

import java.util.*;

/**
 * König's Theorem: Minimum Vertex Cover in Bipartite Graphs.
 * 
 * Purpose:
 *   Proves and constructs the Minimum Vertex Cover from a Maximum Bipartite Matching.
 *   Theorem: In any bipartite graph, |Maximum Matching| = |Minimum Vertex Cover|.
 *   Hospital Application: "Conflict Coverage & Clinical Oversight"
 *   Finds the minimal set of doctors and patient cases such that every clinical conflict
 *   or monitored relationship is directly supervised by at least one selected party.
 * 
 * Input:
 *   BipartiteMatching graph and its solved matching result.
 * 
 * Output:
 *   Minimum Vertex Cover vertices (subset of doctors and cases), verifying
 *   |MinVertexCover| == |MaxMatching|.
 * 
 * Time Complexity:
 *   O(V + E) post-matching alternating reachability traversal.
 * 
 * Space Complexity:
 *   O(V + E) for alternating reachability trees.
 */
public class KonigTheorem {

    public static class KonigResult {
        public final int matchingSize;
        public final int vertexCoverSize;
        public final List<String> coverDoctors;
        public final List<String> coverCases;
        public final boolean theoremVerified;

        public KonigResult(int matchingSize, int vertexCoverSize, List<String> coverDocs,
                           List<String> coverCases, boolean verified) {
            this.matchingSize = matchingSize;
            this.vertexCoverSize = vertexCoverSize;
            this.coverDoctors = coverDocs;
            this.coverCases = coverCases;
            this.theoremVerified = verified;
        }

        @Override
        public String toString() {
            return String.format("König's Theorem Verification:\n" +
                    "  |Maximum Matching|    = %d\n" +
                    "  |Minimum Vertex Cover| = %d\n" +
                    "  Theorem Holds?         = %s\n" +
                    "  Covering Doctors (%d)  : %s\n" +
                    "  Covering Cases (%d)    : %s",
                    matchingSize, vertexCoverSize, theoremVerified ? "TRUE (Q.E.D.)" : "FALSE",
                    coverDoctors.size(), coverDoctors, coverCases.size(), coverCases);
        }
    }

    /**
     * Constructs Minimum Vertex Cover from Bipartite Graph and its solved matching.
     */
    public static KonigResult computeMinimumVertexCover(BipartiteMatching bpm, BipartiteMatching.MatchingResult matchResult) {
        List<String> leftNodes = bpm.getLeftNodes();   // Doctors (L)
        List<String> rightNodes = bpm.getRightNodes(); // Cases (R)
        List<List<Integer>> adj = bpm.getAdj();

        int uSize = leftNodes.size();
        int vSize = rightNodes.size();
        int[] pairU = matchResult.pairU; // L -> R
        int[] pairV = matchResult.pairV; // R -> L

        // Reachability flags
        boolean[] reachableL = new boolean[uSize];
        boolean[] reachableR = new boolean[vSize];

        // Queue for alternating BFS starting from unmatched nodes in L
        Queue<Integer> queue = new ArrayDeque<>();
        for (int u = 0; u < uSize; u++) {
            if (pairU[u] == -1) {
                reachableL[u] = true;
                queue.add(u);
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adj.get(u)) {
                // Follow unmatched edge from L to R
                if (pairU[u] != v && !reachableR[v]) {
                    reachableR[v] = true;
                    // Follow matched edge from R to L
                    int matchedU = pairV[v];
                    if (matchedU != -1 && !reachableL[matchedU]) {
                        reachableL[matchedU] = true;
                        queue.add(matchedU);
                    }
                }
            }
        }

        // Min Vertex Cover C = (L \ S_L) U (R \cap S_R)
        List<String> coverDoctors = new ArrayList<>();
        for (int u = 0; u < uSize; u++) {
            if (!reachableL[u]) {
                coverDoctors.add(leftNodes.get(u));
            }
        }

        List<String> coverCases = new ArrayList<>();
        for (int v = 0; v < vSize; v++) {
            if (reachableR[v]) {
                coverCases.add(rightNodes.get(v));
            }
        }

        int totalCover = coverDoctors.size() + coverCases.size();
        boolean verified = (totalCover == matchResult.maxMatchingSize);

        return new KonigResult(matchResult.maxMatchingSize, totalCover, coverDoctors, coverCases, verified);
    }
}
