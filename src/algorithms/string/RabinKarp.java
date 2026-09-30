package algorithms.string;

public class RabinKarp {
    private static final long BASE = 257;
    private static final long MOD = 1000000007L;

    public int[] search(String text, String pattern) {
        if (text == null || pattern == null || pattern.length() == 0 || pattern.length() > text.length())
            return new int[0];

        int[] temp = new int[text.length()];
        int count = 0;
        long ph = 0, th = 0, power = 1;

        for (int i = 0; i < pattern.length(); i++) {
            ph = (ph * BASE + pattern.charAt(i)) % MOD;
            th = (th * BASE + text.charAt(i)) % MOD;
            if (i < pattern.length() - 1) power = power * BASE % MOD;
        }

        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            if (ph == th) {
                boolean same = true;
                for (int j = 0; j < pattern.length(); j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        same = false; break;
                    }
                }
                if (same) temp[count++] = i;
            }

            if (i < text.length() - pattern.length()) {
                th = (th - text.charAt(i) * power) % MOD;
                if (th < 0) th += MOD;
                th = (th * BASE + text.charAt(i + pattern.length())) % MOD;
            }
        }

        int[] result = new int[count];
        for (int i = 0; i < count; i++) result[i] = temp[i];
        return result;
    }
}
