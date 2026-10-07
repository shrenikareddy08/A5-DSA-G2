package algorithms;

public class KMP {

    /*
     * Builds the LPS (Longest Prefix Suffix) array.
     *
     * LPS[i] tells us:
     * "How much of the pattern can be reused
     *  after a mismatch?"
     */
    private int[] buildLPS(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            if (pattern.charAt(i)
                    == pattern.charAt(length)) {

                length++;

                lps[i] = length;

                i++;

            } else {

                if (length != 0) {

                    length =
                            lps[length - 1];

                } else {

                    lps[i] = 0;

                    i++;
                }
            }
        }

        return lps;
    }

    /*
     * Searches for all occurrences of pattern
     * inside the given text.
     *
     * Returns the starting positions.
     */
    public int[] search(
            String text,
            String pattern) {

        if (text == null
                || pattern == null
                || pattern.length() == 0
                || text.length() == 0) {

            return new int[0];
        }

        int[] lps =
                buildLPS(pattern);

        /*
         * Maximum possible matches
         * cannot be greater than text length.
         */
        int[] positions =
                new int[text.length()];

        int matchCount = 0;

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            if (text.charAt(i)
                    == pattern.charAt(j)) {

                i++;
                j++;

                /*
                 * Complete pattern matched.
                 */
                if (j == pattern.length()) {

                    positions[matchCount] =
                            i - j;

                    matchCount++;

                    /*
                     * Continue searching for
                     * overlapping matches.
                     */
                    j = lps[j - 1];
                }

            } else {

                if (j != 0) {

                    j =
                            lps[j - 1];

                } else {

                    i++;
                }
            }
        }

        /*
         * Create an array containing only
         * the actual match positions.
         */
        int[] result =
                new int[matchCount];

        for (int k = 0;
                k < matchCount;
                k++) {

            result[k] =
                    positions[k];
        }

        return result;
    }

    /*
     * Displays the LPS array.
     *
     * Useful for demonstrating the
     * DSA working during the project demo.
     */
    public void displayLPS(
            String pattern) {

        int[] lps =
                buildLPS(pattern);

        System.out.println(
                "\nPattern : "
                + pattern);

        System.out.print(
                "LPS     : ");

        for (int i = 0;
                i < lps.length;
                i++) {

            System.out.print(
                    lps[i] + " ");
        }

        System.out.println();
    }
}