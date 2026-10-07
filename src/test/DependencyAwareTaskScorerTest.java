package test;

import model.Resource;
import model.Task;
import scheduling.DependencyAwareTaskScorer;
import structures.Graph;

public class DependencyAwareTaskScorerTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "   DEPENDENCY-AWARE INTELLIGENT SCORING TEST");

        System.out.println(
                "==============================================");

        /*
         * ------------------------------------------------
         * CREATE TASKS
         * ------------------------------------------------
         */

        Task t01 =
                new Task(
                        "T01",
                        "Document Analysis",
                        "D01",
                        "KMP",
                        "HIGH",
                        1,
                        128,
                        1
                );

        Task t02 =
                new Task(
                        "T02",
                        "Keyword Search",
                        "D01",
                        "Rabin-Karp",
                        "HIGH",
                        1,
                        128,
                        1
                );

        Task t03 =
                new Task(
                        "T03",
                        "Document Similarity",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        193,
                        2
                );

        /*
         * T03 depends on T01 and T02.
         */
        Task[] tasks = {
                t01,
                t02,
                t03
        };

        /*
         * ------------------------------------------------
         * CREATE DEPENDENCY GRAPH
         * ------------------------------------------------
         */

        Graph graph =
                new Graph(10);

        graph.addTask("T01");
        graph.addTask("T02");
        graph.addTask("T03");

        graph.addDependency(
                "T01",
                "T03");

        graph.addDependency(
                "T02",
                "T03");

        /*
         * ------------------------------------------------
         * CREATE RESOURCE
         * ------------------------------------------------
         */

        Resource resource =
                new Resource(
                        "R01",
                        "Processing Node",
                        4,
                        1024
                );

        /*
         * ------------------------------------------------
         * SCORER
         * ------------------------------------------------
         */

        DependencyAwareTaskScorer scorer =
                new DependencyAwareTaskScorer();

        scorer.displayScores(
                tasks,
                graph,
                resource);

        /*
         * ------------------------------------------------
         * TEST BEFORE DEPENDENCIES COMPLETE
         * ------------------------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       TEST 1: DEPENDENCIES NOT READY");

        System.out.println(
                "==============================================");

        Task selectedBefore =
                scorer.selectBestRunnableTask(
                        tasks,
                        graph,
                        resource);

        if (selectedBefore != null) {

            System.out.println(
                    "Selected Task : "
                    + selectedBefore.getTaskId());

        } else {

            System.out.println(
                    "No dependent task selected.");
        }

        /*
         * T03 must NOT be selected because T01/T02
         * have not completed.
         */

        boolean t03Blocked =
                !scorer.isRunnable(
                        t03,
                        tasks,
                        graph,
                        resource);

        System.out.println(
                "T03 Runnable  : "
                + !t03Blocked);

        if (t03Blocked) {

            System.out.println(
                    "SUCCESS: Dependency correctly blocks T03.");

        } else {

            System.out.println(
                    "FAILURE: T03 ran before prerequisites.");
        }

        /*
         * ------------------------------------------------
         * COMPLETE PREREQUISITES
         * ------------------------------------------------
         */

        t01.setStatus("COMPLETED");
        t02.setStatus("COMPLETED");

        /*
         * ------------------------------------------------
         * TEST AFTER DEPENDENCIES COMPLETE
         * ------------------------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       TEST 2: DEPENDENCIES COMPLETE");

        System.out.println(
                "==============================================");

        boolean dependenciesReady =
                scorer.areDependenciesSatisfied(
                        t03,
                        tasks,
                        graph);

        System.out.println(
                "T01 Status : "
                + t01.getStatus());

        System.out.println(
                "T02 Status : "
                + t02.getStatus());

        System.out.println(
                "T03 Status : "
                + t03.getStatus());

        System.out.println(
                "T03 Dependencies Ready : "
                + dependenciesReady);

        boolean t03Runnable =
                scorer.isRunnable(
                        t03,
                        tasks,
                        graph,
                        resource);

        System.out.println(
                "T03 Runnable : "
                + t03Runnable);

        Task selectedAfter =
                scorer.selectBestRunnableTask(
                        tasks,
                        graph,
                        resource);

        if (selectedAfter != null) {

            System.out.println(
                    "Selected Task : "
                    + selectedAfter.getTaskId());

        } else {

            System.out.println(
                    "No runnable task selected.");
        }

        /*
         * ------------------------------------------------
         * FINAL VALIDATION
         * ------------------------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "              VALIDATION");

        System.out.println(
                "==============================================");

        if (t03Blocked
                && dependenciesReady
                && t03Runnable
                && selectedAfter != null
                && selectedAfter.getTaskId()
                        .equals("T03")) {

            System.out.println(
                    "Dependency Blocking : PASSED");

            System.out.println(
                    "Dependency Release  : PASSED");

            System.out.println(
                    "Runnable Selection  : PASSED");

            System.out.println(
                    "SUCCESS: Dependency-aware intelligent "
                    + "scoring passed.");

        } else {

            System.out.println(
                    "FAILURE: Dependency-aware scoring "
                    + "validation failed.");
        }

        System.out.println(
                "==============================================");
    }
}