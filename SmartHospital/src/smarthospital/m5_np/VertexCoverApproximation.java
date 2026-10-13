package smarthospital.m5_np;

import java.util.*;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover.
 * 
 * Purpose:
 *   Finds a polynomial-time 2-approximation for the NP-Hard Minimum Vertex Cover problem
 *   on large hospital conflict networks (e.g. cross-infection risks, incompatible equipment
 *   scheduling, doctor on-call conflict graphs).
 * 
 * Algorithm:
 *   Maximal Matching Heuristic:
 *   1. Initialize cover C = empty.
 *   2. Pick an arbitrary uncovered edge e = (u, v).
 *   3. Add BOTH u and v to C.
 *   4. Remove all edges incident to u or v.
 *   5. Repeat until no edges remain.
 * 
 * Approximation Ratio:
 *   |C| <= 2 * |OPT|.
 *   Proof: The selected edges form a matching M. Any valid vertex cover must select at least
 *   one endpoint from each edge in M (|OPT| >= |M|). Since C selects both endpoints of each
 *   edge in M, |C| = 2 * |M| <= 2 * |OPT|.
 * 
 * Time Complexity:
 *   O(V + E) where V is the number of conflict nodes and E is the number of conflict links.
 * 
 * Space Complexity:
 *   O(V + E) for the adjacency structure and edge tracking.
 */
public class VertexCoverApproximation {

    public static class ConflictEdge {
        public final int u;
        public final int v;
        public final String nameU;
        public final String nameV;

        public ConflictEdge(int u, int v, String nameU, String nameV) {
            this.u = u;
            this.v = v;
            this.nameU = nameU;
            this.nameV = nameV;
        }

        @Override
        public String toString() {
            return nameU + " <--- Conflict ---> " + nameV;
        }
    }

    public static class ApproxCoverResult {
        public final Set<Integer> coverIndices;
        public final List<String> coverNodeNames;
        public final List<ConflictEdge> maximalMatchingEdges;
        public final int approxCoverSize;
        public final int matchingSize;
        public final double theoreticalUpperBoundRatio;

        public ApproxCoverResult(Set<Integer> coverIndices, List<String> coverNodeNames,
                                 List<ConflictEdge> maximalMatchingEdges) {
            this.coverIndices = coverIndices;
            this.coverNodeNames = coverNodeNames;
            this.maximalMatchingEdges = maximalMatchingEdges;
            this.approxCoverSize = coverIndices.size();
            this.matchingSize = maximalMatchingEdges.size();
            this.theoreticalUpperBoundRatio = 2.0;
        }

        @Override
        public String toString() {
            return String.format("Vertex Cover 2-Approximation Result:\n" +
                    "  Maximal Matching Size (|M|)     = %d\n" +
                    "  Approximate Cover Size (|C|)     = %d (exactly 2 * |M|)\n" +
                    "  Theoretical Guarantee            = |C| <= 2 * |OPT| (Ratio: %.1f)\n" +
                    "  Selected Supervisory Nodes (%d)  : %s",
                    matchingSize, approxCoverSize, theoreticalUpperBoundRatio, coverNodeNames.size(), coverNodeNames);
        }
    }

    /**
     * Executes the 2-approximation algorithm on an adjacency matrix with node names.
     */
    public static ApproxCoverResult approximateVertexCover(boolean[][] adj, List<String> nodeNames) {
        int n = adj.length;
        boolean[] covered = new boolean[n];
        Set<Integer> coverSet = new LinkedHashSet<>();
        List<ConflictEdge> matching = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (covered[i]) continue;

            for (int j = i + 1; j < n; j++) {
                if (adj[i][j] && !covered[j]) {
                    // Pick edge (i, j), add BOTH endpoints to cover
                    coverSet.add(i);
                    coverSet.add(j);
                    covered[i] = true;
                    covered[j] = true;

                    String nameI = (nodeNames != null && i < nodeNames.size()) ? nodeNames.get(i) : "N" + i;
                    String nameJ = (nodeNames != null && j < nodeNames.size()) ? nodeNames.get(j) : "N" + j;
                    matching.add(new ConflictEdge(i, j, nameI, nameJ));
                    break; // Move to next uncovered vertex
                }
            }
        }

        List<String> coverNames = new ArrayList<>();
        for (int idx : coverSet) {
            String name = (nodeNames != null && idx < nodeNames.size()) ? nodeNames.get(idx) : "N" + idx;
            coverNames.add(name);
        }

        return new ApproxCoverResult(coverSet, coverNames, matching);
    }
}
