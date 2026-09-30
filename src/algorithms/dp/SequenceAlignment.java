package algorithms.dp;

public class SequenceAlignment {
    private static final int MATCH = 2;
    private static final int MISMATCH = -1;
    private static final int GAP = -2;

    public int score(String a, String b) {
        int[][] dp = build(a, b);
        return dp[a.length()][b.length()];
    }

    private int[][] build(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) dp[i][0] = i * GAP;
        for (int j = 1; j <= b.length(); j++) dp[0][j] = j * GAP;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int diag = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? MATCH : MISMATCH);
                int up = dp[i - 1][j] + GAP;
                int left = dp[i][j - 1] + GAP;
                dp[i][j] = Math.max(diag, Math.max(up, left));
            }
        }
        return dp;
    }
}
