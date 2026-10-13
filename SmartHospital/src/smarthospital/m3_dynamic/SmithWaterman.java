package smarthospital.m3_dynamic;

/**
 * Smith-Waterman Local Biological Sequence Alignment Algorithm.
 * 
 * Purpose:
 *   Finds optimal local alignments and conserved diagnostic motifs between two
 *   biological sequences (e.g., patient genetic mutations vs reference oncogene sequences).
 *   Applies zero-clipping to isolate regions of high similarity.
 * 
 * Input:
 *   seq1: First biological sequence.
 *   seq2: Second biological sequence.
 *   matchScore: Bonus for matching characters (default +2).
 *   mismatchPenalty: Penalty for substitution (default -1).
 *   gapPenalty: Penalty for gap insertion (default -2).
 * 
 * Output:
 *   LocalAlignmentResult containing maximum local score, aligned local segments, positions.
 * 
 * Time Complexity:
 *   O(m * n) where m = seq1.length(), n = seq2.length().
 * 
 * Space Complexity:
 *   O(m * n) for scoring table.
 */
public class SmithWaterman {

    public static class LocalAlignmentResult {
        public final int maxScore;
        public final String alignedSeq1;
        public final String alignedSeq2;
        public final String matchMarkers;
        public final int startSeq1;
        public final int endSeq1;
        public final int startSeq2;
        public final int endSeq2;
        public final double identityPercentage;

        public LocalAlignmentResult(int maxScore, String a1, String a2, String markers,
                                    int start1, int end1, int start2, int end2, double identity) {
            this.maxScore = maxScore;
            this.alignedSeq1 = a1;
            this.alignedSeq2 = a2;
            this.matchMarkers = markers;
            this.startSeq1 = start1;
            this.endSeq1 = end1;
            this.startSeq2 = start2;
            this.endSeq2 = end2;
            this.identityPercentage = identity;
        }

        @Override
        public String toString() {
            return String.format("Local Alignment Max Score: %d | Identity: %.2f%%\nSeq 1 [%d..%d]: %s\nMatches         : %s\nSeq 2 [%d..%d]: %s",
                    maxScore, identityPercentage, startSeq1, endSeq1, alignedSeq1, matchMarkers, startSeq2, endSeq2, alignedSeq2);
        }
    }

    public static LocalAlignmentResult align(String seq1, String seq2) {
        return align(seq1, seq2, 2, -1, -2);
    }

    public static LocalAlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        if (seq1 == null) seq1 = "";
        if (seq2 == null) seq2 = "";

        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        int maxScore = 0;
        int maxI = 0, maxJ = 0;

        // DP fill with zero-clipping
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int diag = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int up = dp[i - 1][j] + gapPenalty;
                int left = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(0, Math.max(diag, Math.max(up, left)));

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from maximum scoring cell until reaching 0
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        StringBuilder markers = new StringBuilder();

        int i = maxI, j = maxJ;
        int end1 = maxI, end2 = maxJ;
        int matches = 0;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int current = dp[i][j];
            int diag = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
            int up = dp[i - 1][j] + gapPenalty;

            if (current == diag) {
                char c1 = seq1.charAt(i - 1);
                char c2 = seq2.charAt(j - 1);
                align1.append(c1);
                align2.append(c2);
                if (c1 == c2) {
                    markers.append('|');
                    matches++;
                } else {
                    markers.append(':');
                }
                i--;
                j--;
            } else if (current == up) {
                align1.append(seq1.charAt(i - 1));
                align2.append('-');
                markers.append(' ');
                i--;
            } else {
                align1.append('-');
                align2.append(seq2.charAt(j - 1));
                markers.append(' ');
                j--;
            }
        }

        String finalA1 = align1.reverse().toString();
        String finalA2 = align2.reverse().toString();
        String finalMarkers = markers.reverse().toString();

        double identity = finalA1.isEmpty() ? 0.0 : ((double) matches / finalA1.length()) * 100.0;
        return new LocalAlignmentResult(maxScore, finalA1, finalA2, finalMarkers,
                i + 1, end1, j + 1, end2, Math.round(identity * 100.0) / 100.0);
    }
}
