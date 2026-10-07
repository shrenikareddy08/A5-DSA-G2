package algorithms;

public class SequenceAlignment {

    private static final int MATCH_SCORE = 2;
    private static final int MISMATCH_SCORE = -1;
    private static final int GAP_SCORE = -2;

    public int getAlignmentScore(String first, String second) {

        if (first == null) {
            first = "";
        }

        if (second == null) {
            second = "";
        }

        int m = first.length();
        int n = second.length();

        int[][] dp =
                new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            dp[i][0] =
                    dp[i - 1][0] + GAP_SCORE;
        }

        for (int j = 1; j <= n; j++) {
            dp[0][j] =
                    dp[0][j - 1] + GAP_SCORE;
        }

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                int diagonalScore;

                if (first.charAt(i - 1)
                        == second.charAt(j - 1)) {

                    diagonalScore =
                            dp[i - 1][j - 1]
                            + MATCH_SCORE;

                } else {

                    diagonalScore =
                            dp[i - 1][j - 1]
                            + MISMATCH_SCORE;
                }

                int upScore =
                        dp[i - 1][j]
                        + GAP_SCORE;

                int leftScore =
                        dp[i][j - 1]
                        + GAP_SCORE;

                int best =
                        diagonalScore;

                if (upScore > best) {
                    best = upScore;
                }

                if (leftScore > best) {
                    best = leftScore;
                }

                dp[i][j] = best;
            }
        }

        return dp[m][n];
    }

    public String[] getAlignment(String first,
                                 String second) {

        if (first == null) {
            first = "";
        }

        if (second == null) {
            second = "";
        }

        int m = first.length();
        int n = second.length();

        int[][] dp =
                new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            dp[i][0] =
                    dp[i - 1][0] + GAP_SCORE;
        }

        for (int j = 1; j <= n; j++) {
            dp[0][j] =
                    dp[0][j - 1] + GAP_SCORE;
        }

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                int diagonalScore;

                if (first.charAt(i - 1)
                        == second.charAt(j - 1)) {

                    diagonalScore =
                            dp[i - 1][j - 1]
                            + MATCH_SCORE;

                } else {

                    diagonalScore =
                            dp[i - 1][j - 1]
                            + MISMATCH_SCORE;
                }

                int upScore =
                        dp[i - 1][j]
                        + GAP_SCORE;

                int leftScore =
                        dp[i][j - 1]
                        + GAP_SCORE;

                int best =
                        diagonalScore;

                if (upScore > best) {
                    best = upScore;
                }

                if (leftScore > best) {
                    best = leftScore;
                }

                dp[i][j] = best;
            }
        }

        char[] alignedFirst =
                new char[m + n];

        char[] alignedSecond =
                new char[m + n];

        int position =
                m + n - 1;

        int i = m;
        int j = n;

        while (i > 0 || j > 0) {

            if (i > 0 && j > 0) {

                int score;

                if (first.charAt(i - 1)
                        == second.charAt(j - 1)) {

                    score = MATCH_SCORE;

                } else {

                    score = MISMATCH_SCORE;
                }

                if (dp[i][j]
                        == dp[i - 1][j - 1] + score) {

                    alignedFirst[position] =
                            first.charAt(i - 1);

                    alignedSecond[position] =
                            second.charAt(j - 1);

                    position--;
                    i--;
                    j--;

                    continue;
                }
            }

            if (i > 0
                    && dp[i][j]
                    == dp[i - 1][j] + GAP_SCORE) {

                alignedFirst[position] =
                        first.charAt(i - 1);

                alignedSecond[position] =
                        '-';

                position--;
                i--;

            } else {

                alignedFirst[position] =
                        '-';

                alignedSecond[position] =
                        second.charAt(j - 1);

                position--;
                j--;
            }
        }

        int alignmentLength =
                m + n - position - 1;

        String resultFirst =
                new String(
                        alignedFirst,
                        position + 1,
                        alignmentLength);

        String resultSecond =
                new String(
                        alignedSecond,
                        position + 1,
                        alignmentLength);

        return new String[] {
                resultFirst,
                resultSecond
        };
    }
}