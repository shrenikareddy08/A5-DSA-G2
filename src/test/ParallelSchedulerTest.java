package test;

import model.Task;
import model.Resource;
import scheduling.ParallelScheduler;

public class ParallelSchedulerTest {

    public static void main(String[] args) {

        ParallelScheduler scheduler =
                new ParallelScheduler(10, 5);

        // -------------------------------
        // TASKS
        // -------------------------------

        Task t1 = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                2,
                100,
                4
        );

        Task t2 = new Task(
                "T02",
                "Document Similarity",
                "D01",
                "Edit Distance",
                "MEDIUM",
                2,
                200,
                6
        );

        Task t3 = new Task(
                "T03",
                "Keyword Search",
                "D01",
                "Rabin-Karp",
                "HIGH",
                1,
                100,
                3
        );

        scheduler.addTask(t1);
        scheduler.addTask(t2);
        scheduler.addTask(t3);

        // -------------------------------
        // RESOURCES
        // -------------------------------

        Resource r1 = new Resource(
                "R01",
                "Text Worker 1",
                4,
                2000
        );

        Resource r2 = new Resource(
                "R02",
                "Text Worker 2",
                2,
                1000
        );

        Resource r3 = new Resource(
                "R03",
                "Analysis Worker",
                4,
                4000
        );

        scheduler.addResource(r1);
        scheduler.addResource(r2);
        scheduler.addResource(r3);

        // -------------------------------
        // PARALLEL ASSIGNMENT
        // -------------------------------

        scheduler.displayParallelAssignments();

        System.out.println("\n==============================================");
        System.out.println("              TEST COMPLETED");
        System.out.println("==============================================");
    }
}