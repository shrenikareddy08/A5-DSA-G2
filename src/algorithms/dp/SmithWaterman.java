package algorithms.dp;

public class SmithWaterman {
    public int score(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        int best = 0;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int diag = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 2 : -1);
                int up = dp[i - 1][j] - 2;
                int left = dp[i][j - 1] - 2;
                dp[i][j] = Math.max(0, Math.max(diag, Math.max(up, left)));
                if (dp[i][j] > best) best = dp[i][j];
            }
        }
        return best;
    }
}
