package test;

import model.Task;
import model.Resource;
import scheduling.Scheduler;

public class FullPipelineTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "             FULL PIPELINE TEST");

        System.out.println(
                "==============================================");

        /*
         * Create scheduler.
         */
        Scheduler scheduler =
                new Scheduler(10, 5);

        /*
         * T01 - KMP Pattern Search
         */
        Task t1 =
                new Task(
                        "T01",
                        "Pattern Search",
                        "D01",
                        "KMP",
                        "HIGH",
                        2,
                        100,
                        2
                );

        /*
         * T02 - Edit Distance
         */
        Task t2 =
                new Task(
                        "T02",
                        "Document Similarity",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        200,
                        2
                );

        /*
         * T03 - Rabin-Karp
         */
        Task t3 =
                new Task(
                        "T03",
                        "Keyword Search",
                        "D01",
                        "Rabin-Karp",
                        "HIGH",
                        1,
                        100,
                        2
                );

        /*
         * T04 - Suffix Array
         *
         * This task depends on T01, T02 and T03.
         */
        Task t4 =
                new Task(
                        "T04",
                        "Text Indexing",
                        "D01",
                        "Suffix Array",
                        "HIGH",
                        3,
                        300,
                        2
                );

        /*
         * T05 - Sequence Alignment
         *
         * This task depends on T04.
         */
        Task t5 =
                new Task(
                        "T05",
                        "Sequence Alignment",
                        "D01",
                        "Sequence Alignment",
                        "MEDIUM",
                        2,
                        200,
                        2
                );

        /*
         * Add tasks to scheduler.
         */
        scheduler.addTask(t1);
        scheduler.addTask(t2);
        scheduler.addTask(t3);
        scheduler.addTask(t4);
        scheduler.addTask(t5);

        /*
         * Create resources.
         */
        Resource r1 =
                new Resource(
                        "R01",
                        "Text Processing Worker 1",
                        4,
                        2000
                );

        Resource r2 =
                new Resource(
                        "R02",
                        "Text Processing Worker 2",
                        2,
                        1000
                );

        Resource r3 =
                new Resource(
                        "R03",
                        "Analysis Worker",
                        4,
                        4000
                );

        /*
         * Add resources.
         */
        scheduler.addResource(r1);
        scheduler.addResource(r2);
        scheduler.addResource(r3);

        /*
         * Dependency graph:
         *
         *             ┌──> T04 ──> T05
         *             │
         * T01 ────────┤
         * T02 ────────┤
         * T03 ────────┘
         *
         * T01, T02 and T03 can execute
         * independently and in parallel.
         *
         * T04 starts only after all three
         * dependencies complete.
         *
         * T05 starts only after T04 completes.
         */

        scheduler.addDependency(
                "T01",
                "T04");

        scheduler.addDependency(
                "T02",
                "T04");

        scheduler.addDependency(
                "T03",
                "T04");

        scheduler.addDependency(
                "T04",
                "T05");

        /*
         * Display initial configuration.
         */
        System.out.println(
                "\nINITIAL TASK CONFIGURATION");

        System.out.println(
                "----------------------------------------------");

        scheduler.displayTasks();

        System.out.println(
                "\nINITIAL RESOURCE CONFIGURATION");

        System.out.println(
                "----------------------------------------------");

        scheduler.displayResources();

        /*
         * Generate and execute the complete schedule.
         */
        System.out.println(
                "\n==============================================");

        System.out.println(
                "        STARTING FULL PIPELINE");

        System.out.println(
                "==============================================");

        scheduler.generateSchedule();

        /*
         * Final validation.
         */
        System.out.println(
                "\n==============================================");

        System.out.println(
                "          FULL PIPELINE TEST RESULT");

        System.out.println(
                "==============================================");

        System.out.println(
                "Expected Pipeline:");

        System.out.println(
                "T01 -> KMP -> COMPLETED");

        System.out.println(
                "T02 -> Edit Distance -> COMPLETED");

        System.out.println(
                "T03 -> Rabin-Karp -> COMPLETED");

        System.out.println(
                "T04 -> Suffix Array -> COMPLETED");

        System.out.println(
                "T05 -> Sequence Alignment -> COMPLETED");

        System.out.println(
                "----------------------------------------------");

        if (t1.getStatus().equalsIgnoreCase("COMPLETED")
                && t2.getStatus().equalsIgnoreCase("COMPLETED")
                && t3.getStatus().equalsIgnoreCase("COMPLETED")
                && t4.getStatus().equalsIgnoreCase("COMPLETED")
                && t5.getStatus().equalsIgnoreCase("COMPLETED")) {

            System.out.println(
                    "SUCCESS: FULL PIPELINE COMPLETED.");

        } else {

            System.out.println(
                    "FAILURE: FULL PIPELINE DID NOT COMPLETE.");

            System.out.println(
                    "T01 Status : "
                    + t1.getStatus());

            System.out.println(
                    "T02 Status : "
                    + t2.getStatus());

            System.out.println(
                    "T03 Status : "
                    + t3.getStatus());

            System.out.println(
                    "T04 Status : "
                    + t4.getStatus());

            System.out.println(
                    "T05 Status : "
                    + t5.getStatus());
        }

        System.out.println(
                "==============================================");
    }
}