package smarthospital.m3_dynamic;

/**
 * Needleman-Wunsch Global Biological Sequence Alignment Algorithm.
 * 
 * Purpose:
 *   Performs global alignment between two biological sequences (e.g., patient DNA/RNA
 *   marker sequences or pathogen gene snippets like viral spike proteins or bacterial 16S rRNA).
 *   Maximizes overall alignment score and reconstructs the full alignment via backtracking.
 * 
 * Input:
 *   seq1: First biological sequence (e.g. Reference Strain).
 *   seq2: Second biological sequence (e.g. Patient Sample).
 *   matchScore: Bonus for identical nucleotide/amino-acid (default +1).
 *   mismatchPenalty: Penalty for substitution (default -1).
 *   gapPenalty: Penalty for insertion of an indel gap (default -2).
 * 
 * Output:
 *   AlignmentResult containing optimal alignment score, aligned strings, identity percentage.
 * 
 * Time Complexity:
 *   O(m * n) where m = seq1.length(), n = seq2.length().
 * 
 * Space Complexity:
 *   O(m * n) for scoring and traceback matrices.
 */
public class NeedlemanWunsch {

    public static class AlignmentResult {
        public final int score;
        public final String alignedSeq1;
        public final String alignedSeq2;
        public final String matchMarkers;
        public final double identityPercentage;

        public AlignmentResult(int score, String alignedSeq1, String alignedSeq2, String matchMarkers, double identity) {
            this.score = score;
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
            this.matchMarkers = matchMarkers;
            this.identityPercentage = identity;
        }

        @Override
        public String toString() {
            return String.format("Global Alignment Score: %d | Identity: %.2f%%\nSeq 1: %s\nMatch: %s\nSeq 2: %s",
                    score, identityPercentage, alignedSeq1, matchMarkers, alignedSeq2);
        }
    }

    /**
     * Executes Needleman-Wunsch global alignment with default parameters (+1 match, -1 mismatch, -2 gap).
     */
    public static AlignmentResult align(String seq1, String seq2) {
        return align(seq1, seq2, 1, -1, -2);
    }

    /**
     * Executes Needleman-Wunsch global alignment with custom scoring parameters.
     */
    public static AlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        if (seq1 == null) seq1 = "";
        if (seq2 == null) seq2 = "";

        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Initialization
        for (int i = 0; i <= m; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= n; j++) dp[0][j] = j * gapPenalty;

        // DP Recurrence
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int diag = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int up = dp[i - 1][j] + gapPenalty;
                int left = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(diag, Math.max(up, left));
            }
        }

        // Traceback from (m, n) to (0, 0)
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        StringBuilder markers = new StringBuilder();

        int i = m, j = n;
        int matches = 0;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty)) {
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
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
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
        return new AlignmentResult(dp[m][n], finalA1, finalA2, finalMarkers, Math.round(identity * 100.0) / 100.0);
    }
}
