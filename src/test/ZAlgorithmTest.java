package test;

import algorithms.ZAlgorithm;

public class ZAlgorithmTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "             Z ALGORITHM TEST");

        System.out.println(
                "==============================================");

        String text =
                "ABABDABACDABABCABAB";

        String pattern =
                "ABABCABAB";

        System.out.println(
                "Text    : "
                + text);

        System.out.println(
                "Pattern : "
                + pattern);

        System.out.println(
                "----------------------------------------------");

        ZAlgorithm zAlgorithm =
                new ZAlgorithm();

        int[] positions =
                zAlgorithm.search(
                        text,
                        pattern);

        System.out.println(
                "Pattern occurrences:");

        if (positions.length == 0) {

            System.out.println(
                    "No match found.");

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
                "----------------------------------------------");

        if (positions.length == 1
                && positions[0] == 10) {

            System.out.println(
                    "SUCCESS: Z Algorithm test passed.");

        } else {

            System.out.println(
                    "FAILURE: Unexpected result.");
        }

        System.out.println(
                "==============================================");
    }
}