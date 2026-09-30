package algorithms.string;

public class ZAlgorithm {
    public int[] build(String s) {
        int[] z = new int[s.length()];
        int left = 0, right = 0;
        for (int i = 1; i < s.length(); i++) {
            if (i <= right) z[i] = Math.min(right - i + 1, z[i - left]);
            while (i + z[i] < s.length() && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }
        return z;
    }

    public int[] search(String text, String pattern) {
        String combined = pattern + "$" + text;
        int[] z = build(combined);
        int[] temp = new int[text.length()];
        int count = 0;
        for (int i = pattern.length() + 1; i < combined.length(); i++) {
            if (z[i] == pattern.length()) temp[count++] = i - pattern.length() - 1;
        }
        int[] result = new int[count];
        for (int i = 0; i < count; i++) result[i] = temp[i];
        return result;
    }
}
