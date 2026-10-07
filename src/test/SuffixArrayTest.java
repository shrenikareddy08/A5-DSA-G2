package test;

import algorithms.SuffixArray;

public class SuffixArrayTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "           SUFFIX ARRAY + LCP TEST");

        System.out.println(
                "==============================================");

        String text =
                "banana";

        System.out.println(
                "Text : "
                + text);

        System.out.println(
                "----------------------------------------------");

        SuffixArray suffixArray =
                new SuffixArray();

        int[] suffixes =
                suffixArray.buildSuffixArray(text);

        int[] lcp =
                suffixArray.buildLCP(
                        text,
                        suffixes);

        System.out.println(
                "Suffix Array:");

        System.out.println(
                "----------------------------------------------");

        for (int i = 0;
                i < suffixes.length;
                i++) {

            System.out.println(
                    "SA[" + i + "] = "
                    + suffixes[i]
                    + "    "
                    + text.substring(
                            suffixes[i]));
        }

        System.out.println(
                "\nLCP Array:");

        System.out.println(
                "----------------------------------------------");

        for (int i = 0;
                i < lcp.length;
                i++) {

            System.out.println(
                    "LCP[" + i + "] = "
                    + lcp[i]);
        }

        System.out.println(
                "----------------------------------------------");

        /*
         * Expected suffix array for "banana":
         *
         * 5 -> a
         * 3 -> ana
         * 1 -> anana
         * 0 -> banana
         * 4 -> na
         * 2 -> nana
         */
        int[] expectedSuffixArray =
                {5, 3, 1, 0, 4, 2};

        boolean suffixArrayCorrect =
                true;

        for (int i = 0;
                i < expectedSuffixArray.length;
                i++) {

            if (suffixes[i]
                    != expectedSuffixArray[i]) {

                suffixArrayCorrect =
                        false;

                break;
            }
        }

        /*
         * Expected LCP:
         *
         * 0
         * 1
         * 3
         * 0
         * 0
         * 2
         */
        int[] expectedLCP =
                {0, 1, 3, 0, 0, 2};

        boolean lcpCorrect =
                true;

        for (int i = 0;
                i < expectedLCP.length;
                i++) {

            if (lcp[i]
                    != expectedLCP[i]) {

                lcpCorrect =
                        false;

                break;
            }
        }

        System.out.println();

        if (suffixArrayCorrect
                && lcpCorrect) {

            System.out.println(
                    "SUCCESS: Suffix Array + "
                    + "LCP test passed.");

        } else {

            System.out.println(
                    "FAILURE: Suffix Array + "
                    + "LCP test failed.");
        }

        System.out.println(
                "==============================================");
    }
}