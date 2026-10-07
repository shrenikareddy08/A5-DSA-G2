package algorithms;

public class SuffixArray {

    /*
     * Builds the suffix array using a simple
     * comparison-based approach.
     *
     * Each suffix is represented by its starting
     * index. The indices are then sorted according
     * to the lexicographic order of their suffixes.
     *
     * No java.util.* is used because the project
     * engine requires manual data structures.
     */
    public int[] buildSuffixArray(String text) {

        if (text == null || text.length() == 0) {
            return new int[0];
        }

        int n = text.length();

        int[] suffixArray =
                new int[n];

        for (int i = 0; i < n; i++) {
            suffixArray[i] = i;
        }

        /*
         * Insertion sort is used to keep the
         * implementation completely manual.
         */
        for (int i = 1; i < n; i++) {

            int current =
                    suffixArray[i];

            int j = i - 1;

            while (j >= 0
                    && compareSuffixes(
                            text,
                            suffixArray[j],
                            current) > 0) {

                suffixArray[j + 1] =
                        suffixArray[j];

                j--;
            }

            suffixArray[j + 1] =
                    current;
        }

        return suffixArray;
    }

    /*
     * Compares two suffixes beginning at index1
     * and index2.
     *
     * Returns:
     * negative -> suffix1 comes first
     * zero     -> suffixes are equal
     * positive -> suffix2 comes first
     */
    private int compareSuffixes(
            String text,
            int index1,
            int index2) {

        int i = index1;
        int j = index2;

        while (i < text.length()
                && j < text.length()) {

            char c1 =
                    text.charAt(i);

            char c2 =
                    text.charAt(j);

            if (c1 < c2) {
                return -1;
            }

            if (c1 > c2) {
                return 1;
            }

            i++;
            j++;
        }

        if (i == text.length()
                && j == text.length()) {

            return 0;
        }

        if (i == text.length()) {
            return -1;
        }

        return 1;
    }

    /*
     * Builds the LCP array using the
     * suffix array.
     *
     * LCP[i] stores the longest common prefix
     * between:
     *
     * suffixArray[i]
     * and
     * suffixArray[i - 1]
     *
     * Therefore LCP[0] is 0.
     */
    public int[] buildLCP(
            String text,
            int[] suffixArray) {

        if (text == null
                || suffixArray == null
                || suffixArray.length == 0) {

            return new int[0];
        }

        int n =
                suffixArray.length;

        int[] lcp =
                new int[n];

        lcp[0] = 0;

        for (int i = 1; i < n; i++) {

            int first =
                    suffixArray[i - 1];

            int second =
                    suffixArray[i];

            int length = 0;

            while (first + length < text.length()
                    && second + length < text.length()
                    && text.charAt(
                            first + length)
                    == text.charAt(
                            second + length)) {

                length++;
            }

            lcp[i] = length;
        }

        return lcp;
    }

    /*
     * Returns the suffix string beginning at
     * the specified index.
     */
    public String getSuffix(
            String text,
            int index) {

        if (text == null
                || index < 0
                || index >= text.length()) {

            return "";
        }

        return text.substring(index);
    }
}