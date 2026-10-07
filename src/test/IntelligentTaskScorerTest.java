package test;

import model.Resource;
import model.Task;
import scheduling.IntelligentTaskScorer;

public class IntelligentTaskScorerTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       INTELLIGENT TASK SCORING TEST");

        System.out.println(
                "==============================================");

        Task highTask =
                new Task(
                        "T01",
                        "Pattern Search",
                        "D01",
                        "KMP",
                        "HIGH",
                        1,
                        128,
                        1
                );

        Task mediumTask =
                new Task(
                        "T02",
                        "Document Similarity",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        193,
                        2
                );

        Task lowTask =
                new Task(
                        "T03",
                        "Structure Analysis",
                        "30",
                        "Z Algorithm",
                        "LOW",
                        1,
                        128,
                        5
                );

        Task[] tasks = {
                highTask,
                mediumTask,
                lowTask
        };

        /*
         * Resource with enough CPU and memory for all
         * three tasks.
         */
        Resource resource =
                new Resource(
                        "R01",
                        "Processing Node",
                        4,
                        1024
                );

        IntelligentTaskScorer scorer =
                new IntelligentTaskScorer();

        scorer.displayScores(
                tasks,
                resource);

        /*
         * ------------------------------------------------
         * Find best task
         * ------------------------------------------------
         */

        Task bestTask =
                scorer.selectBestTask(
                        tasks,
                        resource);

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             BEST TASK SELECTION");

        System.out.println(
                "==============================================");

        if (bestTask != null) {

            double bestScore =
                    scorer.calculateScore(
                            bestTask,
                            resource);

            System.out.println(
                    "Best Task ID    : "
                    + bestTask.getTaskId());

            System.out.println(
                    "Best Task Name  : "
                    + bestTask.getTaskName());

            System.out.println(
                    "Algorithm       : "
                    + bestTask.getAlgorithm());

            System.out.println(
                    "Priority        : "
                    + bestTask.getPriority());

            System.out.printf(
                    "Intelligent Score: %.2f%n",
                    bestScore);

        } else {

            System.out.println(
                    "No executable task selected.");
        }

        /*
         * ------------------------------------------------
         * Validation
         * ------------------------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "              VALIDATION");

        System.out.println(
                "==============================================");

        if (bestTask != null
                && bestTask.getTaskId().equals("T01")) {

            System.out.println(
                    "Best Task Expected : T01");

            System.out.println(
                    "Best Task Selected : "
                    + bestTask.getTaskId());

            System.out.println(
                    "SUCCESS: Intelligent task scoring passed.");

        } else {

            System.out.println(
                    "FAILURE: Intelligent scoring selected "
                    + "an unexpected task.");
        }

        System.out.println(
                "==============================================");
    }
}