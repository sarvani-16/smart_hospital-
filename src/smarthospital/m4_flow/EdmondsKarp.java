package smarthospital.m4_flow;

import java.util.*;

/**
 * Edmonds-Karp Algorithm for Hospital Resource Networks.
 * 
 * Purpose:
 *   Calculates maximum network flow using Breadth-First Search (BFS) to find
 *   the shortest augmenting path (in terms of number of edges) at each step.
 *   Guarantees polynomial time termination independent of capacities.
 *   Models distribution of emergency beds, oxygen, and critical supplies.
 * 
 * Input:
 *   Capacity network, source vertex, and sink vertex.
 * 
 * Output:
 *   Max flow value and list of augmenting path iterations.
 * 
 * Time Complexity:
 *   O(V * E^2) where V is the number of vertices and E is the number of edges.
 * 
 * Space Complexity:
 *   O(V^2) for adjacency matrix or O(V + E) for queue and parent arrays.
 */
public class EdmondsKarp {

    public static class FlowResult {
        public final int maxFlow;
        public final List<String> iterationLogs;

        public FlowResult(int maxFlow, List<String> iterationLogs) {
            this.maxFlow = maxFlow;
            this.iterationLogs = iterationLogs;
        }
    }

    public static FlowResult computeMaxFlow(int[][] capacity, int source, int sink, Map<Integer, String> nodeNames) {
        int n = capacity.length;
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, n);
        }

        int maxFlow = 0;
        int[] parent = new int[n];
        List<String> logs = new ArrayList<>();
        int iteration = 0;

        while (true) {
            Arrays.fill(parent, -1);
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(source);
            parent[source] = source;

            while (!queue.isEmpty() && parent[sink] == -1) {
                int u = queue.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] > 0) {
                        parent[v] = u;
                        queue.add(v);
                    }
                }
            }

            if (parent[sink] == -1) {
                break; // No more augmenting paths
            }

            iteration++;
            // Find bottleneck capacity
            int pathFlow = Integer.MAX_VALUE;
            List<Integer> path = new ArrayList<>();
            for (int v = sink; v != source; v = parent[v]) {
                path.add(v);
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }
            path.add(source);
            Collections.reverse(path);

            // Update residual network
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            maxFlow += pathFlow;

            // Log path description
            StringBuilder pathStr = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) pathStr.append(" -> ");
                int u = path.get(i);
                pathStr.append(nodeNames != null && nodeNames.containsKey(u) ? nodeNames.get(u) : "N" + u);
            }
            logs.add(String.format("Step %2d: Augment +%d units along: %s", iteration, pathFlow, pathStr));
        }

        return new FlowResult(maxFlow, logs);
    }
}
