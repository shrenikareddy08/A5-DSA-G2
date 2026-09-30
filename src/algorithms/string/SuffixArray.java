package algorithms.string;

public class SuffixArray {
    public int[] build(String text) {
        int n = text.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;

        for (int i = 1; i < n; i++) {
            int key = order[i];
            int j = i - 1;
            while (j >= 0 && compareSuffixes(text, key, order[j]) < 0) {
                order[j + 1] = order[j];
                j--;
            }
            order[j + 1] = key;
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = order[i];
        return result;
    }

    private int compareSuffixes(String s, int a, int b) {
        int i = a, j = b;
        while (i < s.length() && j < s.length()) {
            char ca = s.charAt(i), cb = s.charAt(j);
            if (ca != cb) return ca - cb;
            i++; j++;
        }
        return (s.length() - a) - (s.length() - b);
    }
}
