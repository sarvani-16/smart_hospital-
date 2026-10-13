package smarthospital.m4_flow;

import java.util.*;

/**
 * Maximum Bipartite Matching for Doctor-Patient / Doctor-Case Assignment.
 * 
 * Purpose:
 *   Solves optimal doctor assignment to acute patient cases requiring specialized care.
 *   Left Partition (U): Available specialized doctors.
 *   Right Partition (V): Acute patient cases requiring consultation/intervention.
 *   An edge exists if a doctor's specialty and availability match the case requirements.
 *   Implemented via the Hopcroft-Karp algorithm.
 * 
 * Input:
 *   Bipartite graph with doctor nodes and patient case nodes.
 * 
 * Output:
 *   Maximum cardinality matching (Doctor <-> Case pairings), unassigned doctors, unserviced cases.
 * 
 * Time Complexity:
 *   O(E * sqrt(V)) where V is total vertices and E is total compatibility edges.
 * 
 * Space Complexity:
 *   O(V + E) for adjacency lists, distance arrays, and match arrays.
 */
public class BipartiteMatching {

    private final List<String> leftNodes = new ArrayList<>();   // Doctors
    private final List<String> rightNodes = new ArrayList<>();  // Patient Cases
    private final List<List<Integer>> adj = new ArrayList<>();  // leftIndex -> list of rightIndices

    public void addDoctor(String doctorName) {
        leftNodes.add(doctorName);
        adj.add(new ArrayList<>());
    }

    public void addCase(String caseName) {
        rightNodes.add(caseName);
    }

    public void addCompatibility(int doctorIdx, int caseIdx) {
        if (doctorIdx >= 0 && doctorIdx < leftNodes.size() && caseIdx >= 0 && caseIdx < rightNodes.size()) {
            adj.get(doctorIdx).add(caseIdx);
        }
    }

    public static class MatchPair {
        public final String doctor;
        public final String patientCase;
        public final int doctorIdx;
        public final int caseIdx;

        public MatchPair(String doctor, String patientCase, int doctorIdx, int caseIdx) {
            this.doctor = doctor;
            this.patientCase = patientCase;
            this.doctorIdx = doctorIdx;
            this.caseIdx = caseIdx;
        }

        @Override
        public String toString() {
            return String.format("%-25s <---> %s", doctor, patientCase);
        }
    }

    public static class MatchingResult {
        public final int maxMatchingSize;
        public final List<MatchPair> matches;
        public final List<String> unassignedDoctors;
        public final List<String> unservicedCases;
        public final int[] pairU; // doctor -> case (or -1)
        public final int[] pairV; // case -> doctor (or -1)

        public MatchingResult(int size, List<MatchPair> matches, List<String> unassignedDocs,
                              List<String> unservicedCases, int[] pairU, int[] pairV) {
            this.maxMatchingSize = size;
            this.matches = matches;
            this.unassignedDoctors = unassignedDocs;
            this.unservicedCases = unservicedCases;
            this.pairU = pairU;
            this.pairV = pairV;
        }
    }

    /**
     * Executes Hopcroft-Karp Maximum Bipartite Matching in O(E * sqrt(V)).
     */
    public MatchingResult solve() {
        int uSize = leftNodes.size();
        int vSize = rightNodes.size();

        int[] pairU = new int[uSize];
        int[] pairV = new int[vSize];
        Arrays.fill(pairU, -1);
        Arrays.fill(pairV, -1);

        int[] dist = new int[uSize];
        int matching = 0;

        while (bfs(uSize, pairU, pairV, dist)) {
            for (int u = 0; u < uSize; u++) {
                if (pairU[u] == -1) {
                    if (dfs(u, pairU, pairV, dist)) {
                        matching++;
                    }
                }
            }
        }

        List<MatchPair> matches = new ArrayList<>();
        List<String> unassignedDocs = new ArrayList<>();
        for (int u = 0; u < uSize; u++) {
            if (pairU[u] != -1) {
                matches.add(new MatchPair(leftNodes.get(u), rightNodes.get(pairU[u]), u, pairU[u]));
            } else {
                unassignedDocs.add(leftNodes.get(u));
            }
        }

        List<String> unservicedCases = new ArrayList<>();
        for (int v = 0; v < vSize; v++) {
            if (pairV[v] == -1) {
                unservicedCases.add(rightNodes.get(v));
            }
        }

        return new MatchingResult(matching, matches, unassignedDocs, unservicedCases, pairU, pairV);
    }

    private boolean bfs(int uSize, int[] pairU, int[] pairV, int[] dist) {
        Queue<Integer> q = new ArrayDeque<>();
        for (int u = 0; u < uSize; u++) {
            if (pairU[u] == -1) {
                dist[u] = 0;
                q.add(u);
            } else {
                dist[u] = Integer.MAX_VALUE;
            }
        }

        int distNil = Integer.MAX_VALUE;

        while (!q.isEmpty()) {
            int u = q.poll();
            if (dist[u] < distNil) {
                for (int v : adj.get(u)) {
                    int nextU = pairV[v];
                    if (nextU == -1) {
                        distNil = dist[u] + 1;
                    } else if (dist[nextU] == Integer.MAX_VALUE) {
                        dist[nextU] = dist[u] + 1;
                        q.add(nextU);
                    }
                }
            }
        }
        return distNil != Integer.MAX_VALUE;
    }

    private boolean dfs(int u, int[] pairU, int[] pairV, int[] dist) {
        for (int v : adj.get(u)) {
            int nextU = pairV[v];
            if (nextU == -1 || (dist[nextU] == dist[u] + 1 && dfs(nextU, pairU, pairV, dist))) {
                pairU[u] = v;
                pairV[v] = u;
                return true;
            }
        }
        dist[u] = Integer.MAX_VALUE;
        return false;
    }

    public List<String> getLeftNodes() { return leftNodes; }
    public List<String> getRightNodes() { return rightNodes; }
    public List<List<Integer>> getAdj() { return adj; }
}
