package smarthospital.m2_suffix;

import java.util.*;

/**
 * Suffix Automaton (DAWG - Directed Acyclic Word Graph).
 * 
 * Purpose:
 *   Minimal deterministic finite automaton representing all suffixes of clinical text.
 *   Provides optimal O(M) substring queries, count of occurrences, and distinct substring counting
 *   with at most 2N - 1 states and 3N - 4 transitions.
 * 
 * Input:
 *   Clinical document or medical notes string.
 * 
 * Output:
 *   Instant substring verification, occurrence counts, total distinct substrings.
 * 
 * Time Complexity:
 *   Construction: O(N) linear time using online state extension.
 *   Substring Query: O(M) where M is pattern length.
 *   Occurrence Count: O(M) after O(N) precomputation.
 * 
 * Space Complexity:
 *   O(N) states and transitions (at most 2N states).
 */
public class SuffixAutomaton {

    public static class State {
        int len;
        int link;
        final Map<Character, Integer> next = new HashMap<>();
        long cnt = 0; // Number of endpos occurrences

        State(int len, int link) {
            this.len = len;
            this.link = link;
        }
    }

    private final State[] st;
    private int sz;
    private int last;
    private final String text;

    public SuffixAutomaton(String text) {
        this.text = text;
        int n = text.length();
        this.st = new State[Math.max(2, 2 * n)];
        init();
        for (int i = 0; i < n; i++) {
            extend(text.charAt(i));
        }
        computeOccurrenceCounts();
    }

    private void init() {
        st[0] = new State(0, -1);
        sz = 1;
        last = 0;
    }

    private void extend(char c) {
        int cur = sz++;
        st[cur] = new State(st[last].len + 1, 0);
        st[cur].cnt = 1; // Direct occurrence

        int p = last;
        while (p != -1 && !st[p].next.containsKey(c)) {
            st[p].next.put(c, cur);
            p = st[p].link;
        }

        if (p == -1) {
            st[cur].link = 0;
        } else {
            int q = st[p].next.get(c);
            if (st[p].len + 1 == st[q].len) {
                st[cur].link = q;
            } else {
                int clone = sz++;
                st[clone] = new State(st[p].len + 1, st[q].link);
                st[clone].next.putAll(st[q].next);
                st[clone].cnt = 0; // Cloned state does not add new original position

                while (p != -1 && st[p].next.get(c) == q) {
                    st[p].next.put(c, clone);
                    p = st[p].link;
                }
                st[q].link = clone;
                st[cur].link = clone;
            }
        }
        last = cur;
    }

    private void computeOccurrenceCounts() {
        // Sort states by length in descending order
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < sz; i++) order.add(i);
        order.sort((a, b) -> Integer.compare(st[b].len, st[a].len));

        for (int u : order) {
            if (st[u].link != -1) {
                st[st[u].link].cnt += st[u].cnt;
            }
        }
    }

    /**
     * Checks if pattern is a substring in O(M).
     */
    public boolean contains(String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        int cur = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (!st[cur].next.containsKey(c)) return false;
            cur = st[cur].next.get(c);
        }
        return true;
    }

    /**
     * Returns total count of occurrences of pattern in text in O(M).
     */
    public long countOccurrences(String pattern) {
        if (pattern == null || pattern.isEmpty()) return 0;
        int cur = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (!st[cur].next.containsKey(c)) return 0;
            cur = st[cur].next.get(c);
        }
        return st[cur].cnt;
    }

    /**
     * Counts total distinct substrings in O(N) states: Sum(st[v].len - st[st[v].link].len)
     */
    public long countDistinctSubstrings() {
        long total = 0;
        for (int i = 1; i < sz; i++) {
            total += st[i].len - st[st[i].link].len;
        }
        return total;
    }

    public int getStateCount() {
        return sz;
    }

    public String getText() {
        return text;
    }
}
