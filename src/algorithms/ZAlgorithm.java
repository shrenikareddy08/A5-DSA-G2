package algorithms;

public class ZAlgorithm {

    public int[] search(String text, String pattern) {

        if (text == null || pattern == null) {
            return new int[0];
        }

        if (pattern.length() == 0
                || text.length() == 0
                || pattern.length() > text.length()) {
            return new int[0];
        }

        String combined =
                pattern + "$" + text;

        int length = combined.length();

        int[] z =
                new int[length];

        int left = 0;
        int right = 0;

        for (int i = 1; i < length; i++) {

            if (i <= right) {

                int remaining =
                        right - i + 1;

                int previous =
                        z[i - left];

                if (previous < remaining) {
                    z[i] = previous;
                } else {
                    z[i] = remaining;
                }
            }

            while (i + z[i] < length
                    && combined.charAt(z[i])
                    == combined.charAt(i + z[i])) {

                z[i]++;
            }

            if (i + z[i] - 1 > right) {

                left = i;

                right =
                        i + z[i] - 1;
            }
        }

        int[] temporary =
                new int[text.length()];

        int count = 0;

        for (int i = pattern.length() + 1;
                i < length;
                i++) {

            if (z[i] == pattern.length()) {

                int textIndex =
                        i - pattern.length() - 1;

                temporary[count] =
                        textIndex;

                count++;
            }
        }

        int[] result =
                new int[count];

        for (int i = 0; i < count; i++) {
            result[i] = temporary[i];
        }

        return result;
    }
}