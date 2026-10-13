package smarthospital.m5_np;

import java.util.*;

/**
 * Polynomial-Time Reduction: INDEPENDENT SET -> VERTEX COVER.
 * 
 * Purpose:
 *   Demonstrates Karp's fundamental duality reduction establishing that for any graph G = (V, E),
 *   a subset S subset V is an INDEPENDENT SET of size k if and only if its complement
 *   C = V \ S is a VERTEX COVER of size |V| - k.
 * 
 * Reduction Construction:
 *   1. Graph G remains unchanged.
 *   2. Target Vertex Cover size k' = |V| - k.
 *   3. If S is independent, no edge in E has both endpoints in S.
 *      Consequently, every edge in E must have at least one endpoint in V \ S.
 *      Hence, C = V \ S is a valid vertex cover.
 * 
 * Time Complexity:
 *   O(V + E) set complement and edge coverage verification.
 * 
 * Space Complexity:
 *   O(V) to store vertex sets.
 */
public class IndependentSetToVertexCover {

    public static class VertexCoverReductionResult {
        public final int originalV;
        public final int independentSetSize;
        public final int vertexCoverSize;
        public final List<Integer> vertexCoverIndices;
        public final boolean isProperCover;

        public VertexCoverReductionResult(int originalV, int isSize, int vcSize,
                                          List<Integer> cover, boolean isProper) {
            this.originalV = originalV;
            this.independentSetSize = isSize;
            this.vertexCoverSize = vcSize;
            this.vertexCoverIndices = cover;
            this.isProperCover = isProper;
        }

        @Override
        public String toString() {
            return String.format("Reduction Duality (IS -> VC):\n" +
                    "  |V|                   = %d\n" +
                    "  Target Independent Set k  = %d\n" +
                    "  Target Vertex Cover   k' = |V| - k = %d\n" +
                    "  Vertex Cover Size Computed = %d\n" +
                    "  All Edges Covered?     = %s",
                    originalV, independentSetSize, vertexCoverSize, vertexCoverIndices.size(),
                    isProperCover ? "TRUE (Verified)" : "FALSE");
        }
    }

    /**
     * Executes the reduction: given an Independent Set S in graph G, constructs Vertex Cover C = V \ S.
     */
    public static VertexCoverReductionResult reduce(boolean[][] adj, List<Integer> independentSetIndices) {
        int n = adj.length;
        Set<Integer> isSet = new HashSet<>(independentSetIndices);
        List<Integer> cover = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!isSet.contains(i)) {
                cover.add(i);
            }
        }

        // Verify that every edge in G has at least one endpoint in the cover
        Set<Integer> coverSet = new HashSet<>(cover);
        boolean proper = true;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (adj[i][j]) {
                    if (!coverSet.contains(i) && !coverSet.contains(j)) {
                        proper = false;
                        break;
                    }
                }
            }
            if (!proper) break;
        }

        return new VertexCoverReductionResult(n, independentSetIndices.size(), n - independentSetIndices.size(), cover, proper);
    }
}
