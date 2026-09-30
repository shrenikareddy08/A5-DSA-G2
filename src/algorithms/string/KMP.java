package algorithms.string;

public class KMP {
    private int[] lps(String pattern) {
        int[] a = new int[pattern.length()];
        int len = 0;
        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                a[i++] = ++len;
            } else if (len > 0) {
                len = a[len - 1];
            } else {
                a[i++] = 0;
            }
        }
        return a;
    }

    public int[] search(String text, String pattern) {
        if (text == null || pattern == null || pattern.length() == 0) return new int[0];
        int[] p = lps(pattern);
        int[] temp = new int[text.length()];
        int count = 0, i = 0, j = 0;

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == pattern.length()) {
                    temp[count++] = i - j;
                    j = p[j - 1];
                }
            } else if (j > 0) {
                j = p[j - 1];
            } else {
                i++;
            }
        }

        int[] result = new int[count];
        for (int k = 0; k < count; k++) result[k] = temp[k];
        return result;
    }
}
