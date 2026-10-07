package test;

import algorithms.KMP;

public class KMPTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "             KMP ALGORITHM TEST");

        System.out.println(
                "==============================================");

        String text =
                "ABABDABACDABABCABAB";

        String pattern =
                "ABABCABAB";

        System.out.println(
                "Text    : " + text);

        System.out.println(
                "Pattern : " + pattern);

        KMP kmp =
                new KMP();

        /*
         * Display LPS array.
         */
        kmp.displayLPS(pattern);

        /*
         * Search pattern.
         */
        int[] positions =
                kmp.search(
                        text,
                        pattern);

        System.out.println(
                "\nPattern occurrences:");

        if (positions.length == 0) {

            System.out.println(
                    "Pattern not found.");

        } else {

            for (int i = 0;
                    i < positions.length;
                    i++) {

                System.out.println(
                        "Match "
                        + (i + 1)
                        + " at index "
                        + positions[i]);
            }
        }

        System.out.println(
                "\n==============================================");

        System.out.println(
                "              KMP TEST COMPLETED");

        System.out.println(
                "==============================================");
    }
}