package smarthospital.m1_string;

import java.util.*;

/**
 * Aho-Corasick Multi-Pattern String Matching Automaton.
 * 
 * Purpose:
 *   Simultaneously detects multiple medical keywords (e.g., critical drug allergies,
 *   severe symptom alerts, high-risk diagnoses such as "sepsis", "arrhythmia",
 *   "penicillin allergy", "ketoacidosis") in hospital medical records and lab documents
 *   in a single linear pass over the text.
 * 
 * Input:
 *   A set of medical keyword patterns and target clinical text.
 * 
 * Output:
 *   A mapping of each matched keyword to its match positions (start index).
 * 
 * Time Complexity:
 *   Automaton Construction: O(M * Sigma) where M is the sum of keyword lengths and Sigma is alphabet size.
 *   Text Search: O(N + K) where N = text.length() and K is the total number of keyword occurrences.
 * 
 * Space Complexity:
 *   O(M * Sigma) to store the trie nodes, transition tables, failure links, and output links.
 */
public class AhoCorasick {

    public static class MatchResult {
        public final String keyword;
        public final int startIndex;
        public final int endIndex;

        public MatchResult(String keyword, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return String.format("'%s' at [%d..%d]", keyword, startIndex, endIndex);
        }
    }

    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        final List<String> output = new ArrayList<>();
    }

    private final Node root;
    private boolean isBuilt = false;

    public AhoCorasick() {
        this.root = new Node();
    }

    public AhoCorasick(List<String> keywords) {
        this.root = new Node();
        for (String kw : keywords) {
            addKeyword(kw);
        }
        buildAutomaton();
    }

    /**
     * Adds a keyword pattern to the trie.
     */
    public void addKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        String kw = keyword.trim().toLowerCase();
        Node current = root;
        for (char c : kw.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new Node());
        }
        current.output.add(keyword.trim());
        isBuilt = false;
    }

    /**
     * Builds failure transitions using Breadth-First Search (BFS).
     */
    public void buildAutomaton() {
        Queue<Node> queue = new ArrayDeque<>();

        // Level 1 children fail to root
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                Node fallback = current.fail;
                while (fallback != null && !fallback.children.containsKey(ch)) {
                    fallback = fallback.fail;
                }

                if (fallback != null) {
                    child.fail = fallback.children.get(ch);
                } else {
                    child.fail = root;
                }

                // Collect matched patterns from failure node (dictionary output links)
                if (child.fail != null) {
                    child.output.addAll(child.fail.output);
                }

                queue.add(child);
            }
        }
        isBuilt = true;
    }

    /**
     * Searches text for all keyword occurrences in O(N + K) time.
     */
    public List<MatchResult> searchInText(String text) {
        if (!isBuilt) {
            buildAutomaton();
        }

        List<MatchResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) return results;

        String lowerText = text.toLowerCase();
        Node current = root;

        for (int i = 0; i < lowerText.length(); i++) {
            char ch = lowerText.charAt(i);

            while (current != null && !current.children.containsKey(ch)) {
                current = current.fail;
            }

            if (current == null) {
                current = root;
                continue;
            }

            current = current.children.get(ch);

            if (!current.output.isEmpty()) {
                for (String matchedKw : current.output) {
                    int start = i - matchedKw.length() + 1;
                    results.add(new MatchResult(matchedKw, start, i));
                }
            }
        }
        return results;
    }
}
