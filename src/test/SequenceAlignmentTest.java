package test;

import algorithms.SequenceAlignment;

public class SequenceAlignmentTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "          SEQUENCE ALIGNMENT TEST");

        System.out.println(
                "==============================================");

        String first =
                "GATTACA";

        String second =
                "GCATGCU";

        System.out.println(
                "First Sequence  : "
                + first);

        System.out.println(
                "Second Sequence : "
                + second);

        System.out.println(
                "----------------------------------------------");

        SequenceAlignment alignment =
                new SequenceAlignment();

        int score =
                alignment.getAlignmentScore(
                        first,
                        second);

        String[] result =
                alignment.getAlignment(
                        first,
                        second);

        System.out.println(
                "Alignment Score : "
                + score);

        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "Aligned Sequence 1 : "
                + result[0]);

        System.out.println(
                "Aligned Sequence 2 : "
                + result[1]);

        System.out.println(
                "----------------------------------------------");

        if (result[0].length()
                == result[1].length()) {

            System.out.println(
                    "SUCCESS: Sequence Alignment test passed.");

        } else {

            System.out.println(
                    "FAILURE: Alignment lengths do not match.");
        }

        System.out.println(
                "==============================================");
    }
}