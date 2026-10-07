package algorithms;

public class EditDistance {

    public int calculate(String first, String second) {

        if (first == null) {
            first = "";
        }

        if (second == null) {
            second = "";
        }

        int m = first.length();
        int n = second.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                if (first.charAt(i - 1)
                        == second.charAt(j - 1)) {

                    dp[i][j] =
                            dp[i - 1][j - 1];

                } else {

                    int insert =
                            dp[i][j - 1];

                    int delete =
                            dp[i - 1][j];

                    int replace =
                            dp[i - 1][j - 1];

                    int minimum = insert;

                    if (delete < minimum) {
                        minimum = delete;
                    }

                    if (replace < minimum) {
                        minimum = replace;
                    }

                    dp[i][j] =
                            1 + minimum;
                }
            }
        }

        return dp[m][n];
    }
}