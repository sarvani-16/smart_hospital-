package smarthospital.m4_flow;

import java.util.*;

/**
 * Ford-Fulkerson Algorithm for Medical Resource Distribution.
 * 
 * Purpose:
 *   Computes the maximum flow of essential medical supplies (oxygen, blood units,
 *   saline, pharmaceuticals) from a Central Hospital Depot through transit hubs
 *   to active patient departments (ICU, Emergency, Cardiology, Surgery).
 *   Uses DFS to identify augmenting paths in the residual network.
 * 
 * Input:
 *   Directed capacity graph representing hospital supply pipelines, source, and sink.
 * 
 * Output:
 *   Maximum flow value and residual capacity distribution.
 * 
 * Time Complexity:
 *   O(E * |f_max|) where E is number of edges and |f_max| is the maximum flow value.
 * 
 * Space Complexity:
 *   O(V^2) for the capacity/residual adjacency matrix.
 */
public class FordFulkerson {

    private final int numVertices;
    private final int[][] capacity;
    private final int[][] residual;
    private final Map<Integer, String> nodeNames = new HashMap<>();

    public FordFulkerson(int vertices) {
        this.numVertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.residual = new int[vertices][vertices];
    }

    public void setNodeName(int index, String name) {
        nodeNames.put(index, name);
    }

    public String getNodeName(int index) {
        return nodeNames.getOrDefault(index, "Node-" + index);
    }

    public void addEdge(int u, int v, int cap) {
        capacity[u][v] += cap;
        residual[u][v] += cap;
    }

    private boolean dfs(int s, int t, boolean[] visited, int[] parent) {
        visited[s] = true;
        if (s == t) return true;

        for (int v = 0; v < numVertices; v++) {
            if (!visited[v] && residual[s][v] > 0) {
                parent[v] = s;
                if (dfs(v, t, visited, parent)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int computeMaxFlow(int source, int sink) {
        // Reset residual to original capacity
        for (int i = 0; i < numVertices; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, numVertices);
        }

        int maxFlow = 0;
        int[] parent = new int[numVertices];

        while (true) {
            boolean[] visited = new boolean[numVertices];
            Arrays.fill(parent, -1);

            if (!dfs(source, sink, visited, parent)) {
                break; // No augmenting path found
            }

            // Find bottleneck capacity along path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }

            // Update residual capacities
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    public int[][] getResidual() {
        return residual;
    }

    public int[][] getCapacity() {
        return capacity;
    }

    public int getNumVertices() {
        return numVertices;
    }
}
