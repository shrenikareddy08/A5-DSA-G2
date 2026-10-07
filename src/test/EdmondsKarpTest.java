package test;

import algorithms.EdmondsKarp;

public class EdmondsKarpTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "          EDMONDS-KARP TEST");

        System.out.println(
                "==============================================");

        /*
         * Graph:
         *
         *        10          4
         *   0 --------> 1 --------> 3
         *   |            |
         *   | 10         | 8
         *   v            v
         *   2 --------> 4 --------> 3
         *        9           10
         *
         * Source = 0
         * Sink   = 3
         *
         * Expected maximum flow = 14
         */

        int[][] capacity = {

                {0, 10, 10, 0, 0},

                {0, 0, 0, 4, 8},

                {0, 0, 0, 0, 9},

                {0, 0, 0, 0, 0},

                {0, 0, 0, 10, 0}
        };

        int source = 0;
        int sink = 3;

        System.out.println(
                "Number of Vertices : "
                + capacity.length);

        System.out.println(
                "Source             : "
                + source);

        System.out.println(
                "Sink               : "
                + sink);

        System.out.println(
                "----------------------------------------------");

        EdmondsKarp algorithm =
                new EdmondsKarp();

        EdmondsKarp.FlowResult result =
                algorithm.maxFlow(
                        capacity,
                        source,
                        sink);

        System.out.println(
                "Maximum Flow       : "
                + result.getMaxFlow());

        System.out.println(
                "Augmenting Paths   : "
                + result.getAugmentingPaths());

        System.out.println(
                "----------------------------------------------");

        /*
         * Expected maximum flow:
         *
         * Path 1:
         * 0 -> 1 -> 3 = 4
         *
         * Path 2:
         * 0 -> 1 -> 4 -> 3 = 6
         *
         * Path 3:
         * 0 -> 2 -> 4 -> 3 = 4
         *
         * Total = 14
         */

        if (result.getMaxFlow() == 14) {

            System.out.println(
                    "SUCCESS: Edmonds-Karp test passed.");

        } else {

            System.out.println(
                    "FAILURE: Expected maximum flow 14 "
                    + "but received "
                    + result.getMaxFlow());
        }

        System.out.println(
                "==============================================");
    }
}