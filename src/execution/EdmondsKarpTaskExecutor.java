package execution;

import algorithms.EdmondsKarp;
import model.Task;

public class EdmondsKarpTaskExecutor {

    public void execute(Task task) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       EDMONDS-KARP TASK EXECUTION");

        System.out.println(
                "==============================================");

        System.out.println(
                "Task ID   : "
                + task.getTaskId());

        System.out.println(
                "Task Name : "
                + task.getTaskName());

        System.out.println(
                "Algorithm : "
                + task.getAlgorithm());

        System.out.println(
                "Document  : "
                + task.getDocumentId());

        System.out.println(
                "----------------------------------------------");

        /*
         * Network:
         *
         *       10          4
         *  0 --------> 1 --------> 3
         *  |            |
         *  | 10         | 8
         *  v            v
         *  2 --------> 4 --------> 3
         *       9          10
         *
         * 0 = Source
         * 3 = Sink
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

        EdmondsKarp algorithm =
                new EdmondsKarp();

        long startTime =
                System.currentTimeMillis();

        EdmondsKarp.FlowResult result =
                algorithm.maxFlow(
                        capacity,
                        source,
                        sink);

        long endTime =
                System.currentTimeMillis();

        task.setStatus("COMPLETED");

        System.out.println(
                "\nNETWORK FLOW RESULT");

        System.out.println(
                "----------------------------------------------");

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
                "Maximum Flow       : "
                + result.getMaxFlow());

        System.out.println(
                "Augmenting Paths   : "
                + result.getAugmentingPaths());

        System.out.println(
                "Expected Flow      : 14");

        System.out.println(
                "----------------------------------------------");

        if (result.getMaxFlow() == 14) {

            System.out.println(
                    "Flow Validation    : PASSED");

        } else {

            System.out.println(
                    "Flow Validation    : FAILED");
        }

        System.out.println(
                "Execution Time     : "
                + (endTime - startTime)
                + " ms");

        System.out.println(
                "Task Status        : "
                + task.getStatus());

        System.out.println(
                "==============================================");
    }
}