package smarthospital.m5_np;

import java.util.*;

/**
 * Polynomial-Time Reduction: CLIQUE -> INDEPENDENT SET.
 * 
 * Purpose:
 *   Demonstrates Karp's polynomial reduction transforming a CLIQUE instance on graph G = (V, E)
 *   into an INDEPENDENT SET instance on the complement graph G_bar = (V, E_bar).
 * 
 * Reduction Construction:
 *   1. Complement Graph G_bar shares the identical vertex set V.
 *   2. For all pairs (u, v) with u != v: (u, v) in E_bar iff (u, v) not in E.
 *   3. S is a clique of size k in G <===> S is an independent set of size k in G_bar.
 * 
 * Time Complexity:
 *   O(V^2) to compute the graph complement.
 * 
 * Space Complexity:
 *   O(V^2) for the complement adjacency matrix.
 */
public class CliqueToIndependentSet {

    public static class ComplementGraph {
        public final int vertexCount;
        public final boolean[][] complementAdj;
        public final int targetK;

        public ComplementGraph(int vertexCount, boolean[][] complementAdj, int targetK) {
            this.vertexCount = vertexCount;
            this.complementAdj = complementAdj;
            this.targetK = targetK;
        }

        public int getEdgeCount() {
            int count = 0;
            for (int i = 0; i < vertexCount; i++) {
                for (int j = i + 1; j < vertexCount; j++) {
                    if (complementAdj[i][j]) count++;
                }
            }
            return count;
        }
    }

    /**
     * Constructs the complement graph G_bar from graph G.
     */
    public static ComplementGraph reduce(boolean[][] originalAdj, int targetK) {
        int n = originalAdj.length;
        boolean[][] comp = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    comp[i][j] = !originalAdj[i][j];
                }
            }
        }

        return new ComplementGraph(n, comp, targetK);
    }

    /**
     * Verifies that a given vertex set forms a valid Independent Set in G_bar.
     */
    public static boolean verifyIndependentSet(ComplementGraph compGraph, List<Integer> vertexIndices) {
        for (int i = 0; i < vertexIndices.size(); i++) {
            for (int j = i + 1; j < vertexIndices.size(); j++) {
                int u = vertexIndices.get(i);
                int v = vertexIndices.get(j);
                if (compGraph.complementAdj[u][v]) {
                    return false; // Edge exists in complement -> not independent
                }
            }
        }
        return true;
    }
}
