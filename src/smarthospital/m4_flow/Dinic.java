package smarthospital.m4_flow;

import java.util.*;

/**
 * Dinic's Algorithm for Large Hospital Resource Networks.
 * 
 * Purpose:
 *   High-performance maximum network flow algorithm using level graphs (BFS)
 *   and blocking flows (DFS) with current-edge work pointer optimizations.
 *   Solves large-scale hospital logistical distribution networks (Central Medical Store
 *   -> Regional Warehouses -> Department Hubs -> Wards/ICUs -> Patient Treatment Stations).
 *   Identifies saturated bottleneck channels (Min-Cut).
 * 
 * Input:
 *   Adjacency list directed network with edge capacities, source node, and sink node.
 * 
 * Output:
 *   Maximum resource flow and list of saturated bottleneck edges (Min-Cut).
 * 
 * Time Complexity:
 *   O(V^2 * E) for general capacities, and O(E * sqrt(V)) for unit-capacity networks.
 * 
 * Space Complexity:
 *   O(V + E) for level arrays, edge lists, and head pointer arrays.
 */
public class Dinic {

    public static class Edge {
        public final int to;
        public final int rev;
        public int cap;
        public int flow;
        public final int originalCap;

        public Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.flow = 0;
            this.originalCap = cap;
        }
    }

    private final int n;
    private final List<List<Edge>> adj;
    private final int[] level;
    private final int[] ptr;
    private final Map<Integer, String> nodeLabels = new HashMap<>();

    public Dinic(int n) {
        this.n = n;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        this.level = new int[n];
        this.ptr = new int[n];
    }

    public void setNodeLabel(int index, String label) {
        nodeLabels.put(index, label);
    }

    public String getNodeLabel(int index) {
        return nodeLabels.getOrDefault(index, "Node-" + index);
    }

    public void addEdge(int from, int to, int cap) {
        Edge a = new Edge(to, adj.get(to).size(), cap);
        Edge b = new Edge(from, adj.get(from).size(), 0);
        adj.get(from).add(a);
        adj.get(to).add(b);
    }

    private boolean bfs(int s, int t) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(s);

        while (!q.isEmpty()) {
            int v = q.poll();
            for (Edge edge : adj.get(v)) {
                if (edge.cap - edge.flow > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[v] + 1;
                    q.add(edge.to);
                }
            }
        }
        return level[t] != -1;
    }

    private int dfs(int v, int t, int pushed) {
        if (pushed == 0 || v == t) return pushed;

        for (int cid = ptr[v]; cid < adj.get(v).size(); cid++, ptr[v]++) {
            Edge edge = adj.get(v).get(cid);
            int tr = edge.to;

            if (level[v] + 1 != level[tr] || edge.cap - edge.flow == 0) continue;

            int trPushed = dfs(tr, t, Math.min(pushed, edge.cap - edge.flow));
            if (trPushed == 0) continue;

            edge.flow += trPushed;
            adj.get(tr).get(edge.rev).flow -= trPushed;
            return trPushed;
        }
        return 0;
    }

    public int maxFlow(int s, int t) {
        int flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfs(s, t, Integer.MAX_VALUE);
                if (pushed == 0) break;
                flow += pushed;
            }
        }
        return flow;
    }

    public static class CutEdge {
        public final String from;
        public final String to;
        public final int capacity;

        public CutEdge(String from, String to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s (Capacity: %d)", from, to, capacity);
        }
    }

    /**
     * Identifies the minimum cut (critical bottleneck edges that constrain maximum flow).
     */
    public List<CutEdge> getMinCutEdges(int s) {
        boolean[] reachable = new boolean[n];
        Queue<Integer> q = new ArrayDeque<>();
        q.add(s);
        reachable[s] = true;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (Edge e : adj.get(u)) {
                if (e.cap - e.flow > 0 && !reachable[e.to]) {
                    reachable[e.to] = true;
                    q.add(e.to);
                }
            }
        }

        List<CutEdge> cut = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            if (reachable[u]) {
                for (Edge e : adj.get(u)) {
                    if (!reachable[e.to] && e.originalCap > 0) {
                        cut.add(new CutEdge(getNodeLabel(u), getNodeLabel(e.to), e.originalCap));
                    }
                }
            }
        }
        return cut;
    }

    public List<List<Edge>> getAdj() {
        return adj;
    }
}
