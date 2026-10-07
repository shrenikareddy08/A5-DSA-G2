package test;

import algorithms.EditDistance;

public class EditDistanceTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "             EDIT DISTANCE TEST");

        System.out.println(
                "==============================================");

        String first =
                "kitten";

        String second =
                "sitting";

        System.out.println(
                "First String  : "
                + first);

        System.out.println(
                "Second String : "
                + second);

        System.out.println(
                "----------------------------------------------");

        EditDistance editDistance =
                new EditDistance();

        int distance =
                editDistance.calculate(
                        first,
                        second);

        System.out.println(
                "Edit Distance  : "
                + distance);

        System.out.println(
                "----------------------------------------------");

        if (distance == 3) {

            System.out.println(
                    "SUCCESS: Edit Distance test passed.");

        } else {

            System.out.println(
                    "FAILURE: Unexpected edit distance.");

        }

        System.out.println(
                "==============================================");
    }
}