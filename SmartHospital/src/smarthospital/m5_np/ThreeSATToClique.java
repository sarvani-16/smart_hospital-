package smarthospital.m5_np;

import java.util.*;

/**
 * Polynomial-Time Reduction: 3-SAT -> CLIQUE.
 * 
 * Purpose:
 *   Demonstrates Karp's classic reduction transforming a 3-SAT formula with m clauses
 *   into an undirected graph G = (V, E) such that G contains a clique of size k = m
 *   if and only if the 3-SAT formula is satisfiable.
 * 
 * Reduction Construction:
 *   1. For each clause C_i = (l_i,1 v l_i,2 v l_i,3), generate 3 vertices: (i, 1), (i, 2), (i, 3).
 *      Total vertices |V| = 3 * m.
 *   2. Connect vertex (i, a) and (j, b) with an edge iff:
 *      - i != j (vertices belong to distinct clauses)
 *      - l_i,a != -l_j,b (literals are not contradictory).
 *   3. Target clique size k = m.
 * 
 * Time Complexity:
 *   Reduction construction: O(m^2) where m is the number of clauses.
 * 
 * Space Complexity:
 *   O(m^2) for graph adjacency matrix/list.
 */
public class ThreeSATToClique {

    public static class ClauseVertex {
        public final int id;
        public final int clauseIndex;
        public final int literal;
        public final String literalName;

        public ClauseVertex(int id, int clauseIndex, int literal, String literalName) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literal = literal;
            this.literalName = literalName;
        }

        @Override
        public String toString() {
            return String.format("[V%2d: C%d: %s%s]", id, clauseIndex + 1,
                    literal < 0 ? "~" : "+", literalName);
        }
    }

    public static class ReductionGraph {
        public final List<ClauseVertex> vertices;
        public final boolean[][] adj;
        public final int targetK;

        public ReductionGraph(List<ClauseVertex> vertices, boolean[][] adj, int targetK) {
            this.vertices = vertices;
            this.adj = adj;
            this.targetK = targetK;
        }

        public int getVertexCount() { return vertices.size(); }
        public int getEdgeCount() {
            int count = 0;
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = i + 1; j < vertices.size(); j++) {
                    if (adj[i][j]) count++;
                }
            }
            return count;
        }
    }

    /**
     * Executes the polynomial reduction from 3-SAT to CLIQUE.
     */
    public static ReductionGraph reduce(ThreeSAT threeSat) {
        List<List<Integer>> clauses = threeSat.getClauses();
        int m = clauses.size();
        List<ClauseVertex> vertices = new ArrayList<>();

        int vId = 0;
        for (int i = 0; i < m; i++) {
            List<Integer> clause = clauses.get(i);
            for (int lit : clause) {
                String name = ThreeSAT.VARIABLE_NAMES.getOrDefault(Math.abs(lit), "V" + Math.abs(lit));
                vertices.add(new ClauseVertex(vId++, i, lit, name));
            }
        }

        int totalV = vertices.size();
        boolean[][] adj = new boolean[totalV][totalV];

        // Add edges between vertices from DIFFERENT clauses that are NOT contradictory
        for (int i = 0; i < totalV; i++) {
            for (int j = i + 1; j < totalV; j++) {
                ClauseVertex u = vertices.get(i);
                ClauseVertex v = vertices.get(j);

                if (u.clauseIndex != v.clauseIndex && u.literal != -v.literal) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                }
            }
        }

        return new ReductionGraph(vertices, adj, m);
    }

    /**
     * Finds a clique of target size k using backtracking search.
     */
    public static List<ClauseVertex> findCliqueOfSizeK(ReductionGraph graph, int k) {
        List<Integer> currentClique = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        findCliqueRecursive(graph, 0, currentClique, k, result);

        List<ClauseVertex> clique = new ArrayList<>();
        for (int idx : result) {
            clique.add(graph.vertices.get(idx));
        }
        return clique;
    }

    private static boolean findCliqueRecursive(ReductionGraph g, int startIdx, List<Integer> current, int k, List<Integer> result) {
        if (current.size() == k) {
            result.addAll(current);
            return true;
        }

        int n = g.getVertexCount();
        for (int i = startIdx; i < n; i++) {
            if (current.size() + (n - i) < k) return false; // Prune

            // Check if vertex i connects to all vertices in current clique
            boolean canAdd = true;
            for (int u : current) {
                if (!g.adj[u][i]) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                current.add(i);
                if (findCliqueRecursive(g, i + 1, current, k, result)) {
                    return true;
                }
                current.remove(current.size() - 1);
            }
        }
        return false;
    }
}
