package test;

import model.Task;
import structures.CustomPriorityQueue;

public class PriorityQueueTest {

    public static void main(String[] args) {

        CustomPriorityQueue queue =
                new CustomPriorityQueue(10);

        Task t1 = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "LOW",
                1,
                100,
                4
        );

        Task t2 = new Task(
                "T02",
                "Document Similarity",
                "D01",
                "EDIT_DISTANCE",
                "HIGH",
                2,
                200,
                6
        );

        Task t3 = new Task(
                "T03",
                "Keyword Search",
                "D02",
                "AHO_CORASICK",
                "MEDIUM",
                1,
                150,
                3
        );

        System.out.println("\n");
        System.out.println("==============================================");
        System.out.println("       CUSTOM PRIORITY QUEUE TEST");
        System.out.println("==============================================");

        System.out.println("\nInserting tasks...");

        queue.insert(t1);
        queue.insert(t2);
        queue.insert(t3);

        queue.displayQueue();

        System.out.println("\nHighest priority task:");

        Task highest = queue.peek();

        if (highest != null) {
            System.out.println(
                    highest.getTaskId()
                    + " - "
                    + highest.getTaskName()
                    + " - "
                    + highest.getPriority()
            );
        }

        System.out.println("\nRemoving tasks in priority order:");

        while (!queue.isEmpty()) {

            Task task = queue.removeHighestPriority();

            System.out.println(
                    task.getTaskId()
                    + " -> "
                    + task.getPriority()
            );
        }

        System.out.println("\n==============================================");
        System.out.println("              TEST COMPLETED");
        System.out.println("==============================================");
    }
}