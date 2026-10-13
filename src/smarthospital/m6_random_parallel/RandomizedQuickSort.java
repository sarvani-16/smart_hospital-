package smarthospital.m6_random_parallel;

import java.util.*;

/**
 * Randomized QuickSort for Medical Record Ranking.
 * 
 * Purpose:
 *   Sorts large collections of hospital medical records, patient vitals, and diagnostic histories
 *   by various clinical criteria (patient age, admission timestamp, urgency score).
 *   Uses randomized pivot selection to ensure expected O(N log N) performance and eliminate
 *   adversarial worst-case quadratic O(N^2) degradation on pre-sorted medical logs.
 * 
 * Input:
 *   List of items and a comparator.
 * 
 * Output:
 *   Sorted list, comparison counter, and execution duration.
 * 
 * Time Complexity:
 *   Expected Time: O(N log N)
 *   Worst-case Time: O(N^2) (probability exponentially small)
 * 
 * Space Complexity:
 *   O(log N) auxiliary recursion stack.
 */
public class RandomizedQuickSort {

    private static final Random RNG = new Random();

    public static class SortStats {
        public long comparisons = 0;
        public long durationNanos = 0;
    }

    public static <T> SortStats sort(List<T> list, Comparator<T> comparator) {
        SortStats stats = new SortStats();
        if (list == null || list.size() <= 1) return stats;

        long start = System.nanoTime();
        quickSort(list, 0, list.size() - 1, comparator, stats);
        stats.durationNanos = System.nanoTime() - start;
        return stats;
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comp, SortStats stats) {
        if (low < high) {
            int p = randomizedPartition(list, low, high, comp, stats);
            quickSort(list, low, p - 1, comp, stats);
            quickSort(list, p + 1, high, comp, stats);
        }
    }

    private static <T> int randomizedPartition(List<T> list, int low, int high, Comparator<T> comp, SortStats stats) {
        // Choose random pivot uniformly between [low, high]
        int randomPivotIdx = low + RNG.nextInt(high - low + 1);
        Collections.swap(list, randomPivotIdx, high);

        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            stats.comparisons++;
            if (comp.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}
