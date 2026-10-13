package smarthospital.m2_suffix;

import java.util.*;

/**
 * Compressed Suffix Tree Implementation.
 * 
 * Purpose:
 *   Builds a full compressed Trie of all suffixes of clinical documents.
 *   Provides instant O(M) substring search, occurrence localization,
 *   and longest repeated phrase retrieval.
 * 
 * Input:
 *   Clinical text document.
 * 
 * Output:
 *   Boolean substring presence, list of start offsets, longest repeated phrase.
 * 
 * Time Complexity:
 *   Construction: O(N * min(N, Sigma))
 *   Substring Search: O(M) where M is pattern length.
 *   Occurrences Retrieval: O(M + K) where K is number of occurrences.
 * 
 * Space Complexity:
 *   O(N) nodes and edges.
 */
public class SuffixTree {

    public static class Node {
        final Map<Character, Edge> edges = new TreeMap<>();
        int suffixIndex = -1; // -1 for internal nodes, >= 0 for leaves
    }

    public static class Edge {
        final int start;
        final int end;
        final Node target;

        public Edge(int start, int end, Node target) {
            this.start = start;
            this.end = end;
            this.target = target;
        }

        public int length() {
            return end - start;
        }
    }

    private final String text;
    private final Node root;
    private int nodeCount = 1;

    public SuffixTree(String text) {
        // Append unique terminal sentinel to ensure every suffix ends at a leaf
        this.text = text.endsWith("$") ? text : text + "$";
        this.root = new Node();
        buildTree();
    }

    private void buildTree() {
        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(i);
        }
    }

    private void insertSuffix(int suffixStart) {
        Node current = root;
        int i = suffixStart;
        int n = text.length();

        while (i < n) {
            char ch = text.charAt(i);
            Edge edge = current.edges.get(ch);

            if (edge == null) {
                // Create new leaf node
                Node leaf = new Node();
                leaf.suffixIndex = suffixStart;
                current.edges.put(ch, new Edge(i, n, leaf));
                nodeCount++;
                return;
            }

            // Traverse existing edge
            int edgeLen = edge.length();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < n && 
                   text.charAt(edge.start + matchLen) == text.charAt(i + matchLen)) {
                matchLen++;
            }

            if (matchLen == edgeLen) {
                // Move down the edge
                current = edge.target;
                i += edgeLen;
            } else {
                // Split edge
                Node split = new Node();
                nodeCount++;

                Edge newBranch = new Edge(edge.start + matchLen, edge.end, edge.target);
                split.edges.put(text.charAt(edge.start + matchLen), newBranch);

                Node leaf = new Node();
                leaf.suffixIndex = suffixStart;
                nodeCount++;
                split.edges.put(text.charAt(i + matchLen), new Edge(i + matchLen, n, leaf));

                // Replace old edge with edge to split node
                current.edges.put(ch, new Edge(edge.start, edge.start + matchLen, split));
                return;
            }
        }
    }

    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Checks if pattern is a substring in O(M).
     */
    public boolean contains(String pattern) {
        return !findAllOccurrences(pattern).isEmpty();
    }

    /**
     * Finds all occurrence indices of pattern in the original text in O(M + K).
     */
    public List<Integer> findAllOccurrences(String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || pattern.isEmpty()) return occurrences;

        Node current = root;
        int i = 0;
        int m = pattern.length();

        while (i < m) {
            char ch = pattern.charAt(i);
            Edge edge = current.edges.get(ch);
            if (edge == null) return occurrences;

            int edgeLen = edge.length();
            int j = 0;
            while (j < edgeLen && i < m) {
                if (text.charAt(edge.start + j) != pattern.charAt(i)) {
                    return occurrences; // Mismatch
                }
                j++;
                i++;
            }

            if (i < m) {
                current = edge.target;
            } else {
                // Pattern matched completely within or at end of this edge
                collectLeaves(edge.target, occurrences);
                Collections.sort(occurrences);
                return occurrences;
            }
        }

        collectLeaves(current, occurrences);
        Collections.sort(occurrences);
        return occurrences;
    }

    private void collectLeaves(Node node, List<Integer> leaves) {
        if (node.suffixIndex != -1) {
            leaves.add(node.suffixIndex);
        }
        for (Edge edge : node.edges.values()) {
            collectLeaves(edge.target, leaves);
        }
    }

    /**
     * Finds longest repeated substring by locating deepest internal node.
     */
    public String longestRepeatedSubstring() {
        int[] maxDepth = new int[]{0};
        String[] bestStr = new String[]{""};
        dfsLongest(root, "", maxDepth, bestStr);
        return bestStr[0];
    }

    private void dfsLongest(Node node, String path, int[] maxDepth, String[] bestStr) {
        // Only internal nodes with >= 2 children represent repeated substrings
        if (node.edges.size() >= 2 && path.length() > maxDepth[0] && !path.contains("$")) {
            maxDepth[0] = path.length();
            bestStr[0] = path;
        }
        for (Edge edge : node.edges.values()) {
            String edgeStr = text.substring(edge.start, edge.end);
            dfsLongest(edge.target, path + edgeStr, maxDepth, bestStr);
        }
    }
}
