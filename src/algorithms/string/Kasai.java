package algorithms.string;

public class Kasai {
    public int[] buildLCP(String text, int[] sa) {
        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;

        int k = 0;
        for (int i = 0; i < n; i++) {
            int r = rank[i];
            if (r == n - 1) {
                k = 0;
                continue;
            }
            int j = sa[r + 1];
            while (i + k < n && j + k < n && text.charAt(i + k) == text.charAt(j + k)) k++;
            lcp[r] = k;
            if (k > 0) k--;
        }
        return lcp;
    }
}
