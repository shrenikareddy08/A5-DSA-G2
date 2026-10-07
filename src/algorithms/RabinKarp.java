package algorithms;

public class RabinKarp {

    private static final int PRIME = 101;
    private static final int BASE = 256;

    public int[] search(String text, String pattern) {

        if (text == null || pattern == null) {
            return new int[0];
        }

        if (pattern.length() == 0 || text.length() == 0) {
            return new int[0];
        }

        if (pattern.length() > text.length()) {
            return new int[0];
        }

        int patternLength = pattern.length();
        int textLength = text.length();

        int patternHash = 0;
        int textHash = 0;
        int highOrder = 1;

        for (int i = 0; i < patternLength - 1; i++) {
            highOrder =
                    (highOrder * BASE) % PRIME;
        }

        for (int i = 0; i < patternLength; i++) {

            patternHash =
                    (BASE * patternHash
                    + pattern.charAt(i))
                    % PRIME;

            textHash =
                    (BASE * textHash
                    + text.charAt(i))
                    % PRIME;
        }

        int[] temporary =
                new int[textLength];

        int count = 0;

        for (int i = 0;
                i <= textLength - patternLength;
                i++) {

            if (patternHash == textHash) {

                boolean match = true;

                for (int j = 0;
                        j < patternLength;
                        j++) {

                    if (text.charAt(i + j)
                            != pattern.charAt(j)) {

                        match = false;
                        break;
                    }
                }

                if (match) {
                    temporary[count] = i;
                    count++;
                }
            }

            if (i < textLength - patternLength) {

                textHash =
                        (BASE *
                        (textHash
                        - text.charAt(i)
                        * highOrder)
                        + text.charAt(i + patternLength))
                        % PRIME;

                if (textHash < 0) {
                    textHash += PRIME;
                }
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