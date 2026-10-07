package test;

import algorithms.NetworkFlowResourceAllocator;

public class NetworkFlowResourceAllocatorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "      NETWORK FLOW RESOURCE ALLOCATION");

        System.out.println(
                "==============================================");

        /*
         * Tasks:
         *
         * T01 -> R01, R03
         * T02 -> R02, R03
         * T03 -> R01
         * T04 -> R02
         *
         * Resources:
         *
         * R01
         * R02
         * R03
         *
         * Since there are only 3 resources,
         * at most 3 tasks can execute in
         * this scheduling round.
         */

        int[][] compatibility = {

                /*
                 *        R01 R02 R03
                 */
                {1, 0, 1},   // T01
                {0, 1, 1},   // T02
                {1, 0, 0},   // T03
                {0, 1, 0}    // T04
        };

        NetworkFlowResourceAllocator allocator =
                new NetworkFlowResourceAllocator();

        NetworkFlowResourceAllocator.AllocationResult
                result =
                allocator.allocate(
                        compatibility);

        System.out.println(
                "Number of Tasks     : "
                + compatibility.length);

        System.out.println(
                "Number of Resources : "
                + compatibility[0].length);

        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "Maximum Assignments : "
                + result.getMaximumAssignments());

        System.out.println(
                "----------------------------------------------");

        int[] assignment =
                result.getTaskToResource();

        for (int task = 0;
                task < assignment.length;
                task++) {

            if (assignment[task] == -1) {

                System.out.println(
                        "T0"
                        + (task + 1)
                        + " -> NOT ASSIGNED");

            } else {

                System.out.println(
                        "T0"
                        + (task + 1)
                        + " -> R0"
                        + (assignment[task] + 1));
            }
        }

        System.out.println(
                "----------------------------------------------");

        if (result.getMaximumAssignments() == 3) {

            System.out.println(
                    "SUCCESS: Network-flow resource "
                    + "allocation test passed.");

        } else {

            System.out.println(
                    "FAILURE: Expected 3 assignments "
                    + "but received "
                    + result.getMaximumAssignments());
        }

        System.out.println(
                "==============================================");
    }
}