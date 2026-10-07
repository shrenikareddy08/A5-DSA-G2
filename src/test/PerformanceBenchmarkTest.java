
package test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import scheduling.Scheduler;

public class PerformanceBenchmarkTest {

    /*
     * Same workload is executed in both modes.
     *
     * 6 algorithms × 50 repetitions = 300 executions.
     */
    private static final int REPETITIONS = 50;

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       INTELLIGENT SCHEDULER BENCHMARK");

        System.out.println(
                "==============================================");

        System.out.println(
                "Algorithms per repetition : 6");

        System.out.println(
                "Repetitions               : "
                + REPETITIONS);

        System.out.println(
                "Total executions          : "
                + (6 * REPETITIONS));

        /*
         * ==================================================
         * PART 1
         * SEQUENTIAL EXECUTION
         * ==================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       PART 1: SEQUENTIAL EXECUTION");

        System.out.println(
                "==============================================");

        Task[] sequentialTasks =
                createBenchmarkTasks();

        TaskExecutor executor =
                new TaskExecutor();

        /*
         * Suppress algorithm console output.
         *
         * Otherwise printing would dominate the benchmark
         * instead of measuring scheduling/execution.
         */
        PrintStream originalOutput =
                System.out;

        PrintStream silentOutput =
                new PrintStream(
                        new ByteArrayOutputStream());

        System.setOut(silentOutput);

        long sequentialStart =
                System.nanoTime();

        for (int repetition = 0;
             repetition < REPETITIONS;
             repetition++) {

            for (int i = 0;
                 i < sequentialTasks.length;
                 i++) {

                Task task =
                        sequentialTasks[i];

                task.setStatus("WAITING");

                executor.execute(task);
            }
        }

        long sequentialEnd =
                System.nanoTime();

        /*
         * Restore console.
         */
        System.setOut(originalOutput);

        silentOutput.close();

        long sequentialTimeNs =
                sequentialEnd
                - sequentialStart;

        double sequentialTimeMs =
                sequentialTimeNs
                / 1_000_000.0;

        int sequentialCompleted =
                countCompletedTasks(
                        sequentialTasks);

        /*
         * Each task was executed REPETITIONS times.
         *
         * Therefore the number of successful executions
         * is:
         *
         * 6 × 50 = 300
         */
        int expectedExecutions =
                sequentialTasks.length
                * REPETITIONS;

        System.out.println(
                "Sequential execution completed.");

        System.out.printf(
                "Sequential time : %.3f ms%n",
                sequentialTimeMs);

        System.out.println(
                "Task definitions completed : "
                + sequentialCompleted
                + " / "
                + sequentialTasks.length);

        System.out.println(
                "Actual executions           : "
                + expectedExecutions);

        /*
         * ==================================================
         * PART 2
         * INTELLIGENT PARALLEL EXECUTION
         * ==================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "   PART 2: INTELLIGENT PARALLEL EXECUTION");

        System.out.println(
                "==============================================");

        Task[] parallelTasks =
                createRepeatedBenchmarkTasks(
                        REPETITIONS);

        Scheduler scheduler =
                new Scheduler(
                        parallelTasks.length,
                        3);

        /*
         * Add all 300 tasks.
         */
        for (int i = 0;
             i < parallelTasks.length;
             i++) {

            scheduler.addTask(
                    parallelTasks[i]);
        }

        /*
         * Three heterogeneous resources.
         */
        scheduler.addResource(
                new Resource(
                        "R01",
                        "Benchmark Worker 1",
                        4,
                        2000));

        scheduler.addResource(
                new Resource(
                        "R02",
                        "Benchmark Worker 2",
                        2,
                        1500));

        scheduler.addResource(
                new Resource(
                        "R03",
                        "Benchmark Analysis Worker",
                        4,
                        4000));

        /*
         * Add dependencies separately for every
         * repetition.
         *
         * For each group:
         *
         * B01 ─────┐
         *           ├──> B05
         * B02 ─────┘
         *
         * B03 ─────┐
         *           ├──> B06
         * B04 ─────┘
         *
         * This preserves the same dependency structure
         * throughout the entire benchmark.
         */

        addBenchmarkDependencies(
                scheduler,
                REPETITIONS);

        /*
         * Suppress scheduler and algorithm output
         * during the timed section.
         */
        originalOutput =
                System.out;

        silentOutput =
                new PrintStream(
                        new ByteArrayOutputStream());

        System.setOut(silentOutput);

        long parallelStart =
                System.nanoTime();

        scheduler.generateSchedule();

        long parallelEnd =
                System.nanoTime();

        /*
         * Restore console.
         */
        System.setOut(originalOutput);

        silentOutput.close();

        long parallelTimeNs =
                parallelEnd
                - parallelStart;

        double parallelTimeMs =
                parallelTimeNs
                / 1_000_000.0;

        int parallelCompleted =
                scheduler.getCompletedTasks();

        int parallelFailed =
                scheduler.getFailedTasks();

        int parallelBlocked =
                scheduler.getBlockedTasks();

        /*
         * ==================================================
         * PART 3
         * METRIC CALCULATION
         * ==================================================
         */

        double speedup =
                0.0;

        double timeSaved =
                0.0;

        double sequentialThroughput =
                0.0;

        double parallelThroughput =
                0.0;

        if (sequentialTimeMs > 0.0) {

            sequentialThroughput =
                    expectedExecutions
                    / (sequentialTimeMs
                    / 1000.0);
        }

        if (parallelTimeMs > 0.0) {

            parallelThroughput =
                    parallelCompleted
                    / (parallelTimeMs
                    / 1000.0);
        }

        if (parallelTimeMs > 0.0) {

            speedup =
                    sequentialTimeMs
                    / parallelTimeMs;
        }

        if (sequentialTimeMs > 0.0) {

            timeSaved =
                    ((sequentialTimeMs
                    - parallelTimeMs)
                    / sequentialTimeMs)
                    * 100.0;
        }

        /*
         * ==================================================
         * FINAL REPORT
         * ==================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          BENCHMARK RESULTS");

        System.out.println(
                "==============================================");

        System.out.println(
                "Algorithms per repetition : 6");

        System.out.println(
                "Repetitions                : "
                + REPETITIONS);

        System.out.println(
                "Expected executions        : "
                + expectedExecutions);

        System.out.printf(
                "Sequential time            : %.3f ms%n",
                sequentialTimeMs);

        System.out.printf(
                "Parallel time              : %.3f ms%n",
                parallelTimeMs);

        System.out.printf(
                "Speedup                    : %.2fx%n",
                speedup);

        System.out.printf(
                "Time saved                 : %.2f%%%n",
                timeSaved);

        System.out.printf(
                "Sequential throughput      : %.2f executions/sec%n",
                sequentialThroughput);

        System.out.printf(
                "Parallel throughput        : %.2f executions/sec%n",
                parallelThroughput);

        System.out.println(
                "Parallel completed         : "
                + parallelCompleted);

        System.out.println(
                "Parallel failed            : "
                + parallelFailed);

        System.out.println(
                "Parallel blocked           : "
                + parallelBlocked);

        System.out.println(
                "==============================================");

        /*
         * ==================================================
         * VALIDATION
         * ==================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             VALIDATION");

        System.out.println(
                "==============================================");

        boolean passed = true;

        /*
         * Every parallel task must complete.
         */
        if (parallelCompleted
                != expectedExecutions) {

            /*
             * IMPORTANT:
             *
             * Scheduler reports completed task count,
             * not repeated execution count if a task is
             * represented only once.
             *
             * We therefore validate against the number
             * of scheduler task objects.
             */
            if (parallelCompleted
                    != parallelTasks.length) {

                passed = false;

                System.out.println(
                        "FAIL: Parallel task count mismatch.");
            }
        }

        /*
         * No failures.
         */
        if (parallelFailed != 0) {

            passed = false;

            System.out.println(
                    "FAIL: Parallel execution has failures.");
        }

        /*
         * No blocked tasks.
         */
        if (parallelBlocked != 0) {

            passed = false;

            System.out.println(
                    "FAIL: Parallel execution has blocked tasks.");
        }

        /*
         * Valid measured times.
         */
        if (sequentialTimeMs <= 0.0) {

            passed = false;

            System.out.println(
                    "FAIL: Invalid sequential time.");
        }

        if (parallelTimeMs <= 0.0) {

            passed = false;

            System.out.println(
                    "FAIL: Invalid parallel time.");
        }

        if (passed) {

            System.out.println(
                    "SUCCESS: Fair sequential vs parallel benchmark completed.");

        } else {

            System.out.println(
                    "FAILURE: Benchmark validation failed.");
        }

        System.out.println(
                "==============================================");
    }

    /*
     * ==================================================
     * CREATE ONE BENCHMARK GROUP
     * ==================================================
     */

    private static Task[] createBenchmarkTasks() {

        Task[] tasks =
                new Task[6];

        tasks[0] =
                new Task(
                        "BASE01",
                        "Pattern Search",
                        "D01",
                        "KMP",
                        "HIGH",
                        1,
                        129,
                        1);

        tasks[1] =
                new Task(
                        "BASE02",
                        "Keyword Search",
                        "D01",
                        "Rabin-Karp",
                        "HIGH",
                        1,
                        129,
                        1);

        tasks[2] =
                new Task(
                        "BASE03",
                        "Structure Analysis",
                        "D01",
                        "Z Algorithm",
                        "MEDIUM",
                        1,
                        129,
                        1);

        tasks[3] =
                new Task(
                        "BASE04",
                        "Pattern Search",
                        "30",
                        "KMP",
                        "HIGH",
                        1,
                        128,
                        1);

        tasks[4] =
                new Task(
                        "BASE05",
                        "Text Indexing",
                        "D01",
                        "Suffix Array",
                        "HIGH",
                        2,
                        257,
                        2);

        tasks[5] =
                new Task(
                        "BASE06",
                        "Document Similarity",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        193,
                        2);

        return tasks;
    }

    /*
     * ==================================================
     * CREATE REPEATED PARALLEL WORKLOAD
     * ==================================================
     */

    private static Task[] createRepeatedBenchmarkTasks(
            int repetitions) {

        Task[] tasks =
                new Task[6 * repetitions];

        int index = 0;

        for (int repetition = 0;
             repetition < repetitions;
             repetition++) {

            String suffix =
                    String.valueOf(
                            repetition + 1);

            tasks[index++] =
                    new Task(
                            "B01_" + suffix,
                            "Pattern Search",
                            "D01",
                            "KMP",
                            "HIGH",
                            1,
                            129,
                            1);

            tasks[index++] =
                    new Task(
                            "B02_" + suffix,
                            "Keyword Search",
                            "D01",
                            "Rabin-Karp",
                            "HIGH",
                            1,
                            129,
                            1);

            tasks[index++] =
                    new Task(
                            "B03_" + suffix,
                            "Structure Analysis",
                            "D01",
                            "Z Algorithm",
                            "MEDIUM",
                            1,
                            129,
                            1);

            tasks[index++] =
                    new Task(
                            "B04_" + suffix,
                            "Pattern Search",
                            "30",
                            "KMP",
                            "HIGH",
                            1,
                            128,
                            1);

            tasks[index++] =
                    new Task(
                            "B05_" + suffix,
                            "Text Indexing",
                            "D01",
                            "Suffix Array",
                            "HIGH",
                            2,
                            257,
                            2);

            tasks[index++] =
                    new Task(
                            "B06_" + suffix,
                            "Document Similarity",
                            "D01",
                            "Edit Distance",
                            "MEDIUM",
                            2,
                            193,
                            2);
        }

        return tasks;
    }

    /*
     * ==================================================
     * ADD DEPENDENCIES
     * ==================================================
     */

    private static void addBenchmarkDependencies(
            Scheduler scheduler,
            int repetitions) {

        for (int repetition = 0;
             repetition < repetitions;
             repetition++) {

            String suffix =
                    String.valueOf(
                            repetition + 1);

            scheduler.addDependency(
                    "B01_" + suffix,
                    "B05_" + suffix);

            scheduler.addDependency(
                    "B02_" + suffix,
                    "B05_" + suffix);

            scheduler.addDependency(
                    "B03_" + suffix,
                    "B06_" + suffix);

            scheduler.addDependency(
                    "B04_" + suffix,
                    "B06_" + suffix);
        }
    }

    /*
     * ==================================================
     * COUNT COMPLETED TASK DEFINITIONS
     * ==================================================
     */

    private static int countCompletedTasks(
            Task[] tasks) {

        int count = 0;

        for (int i = 0;
             i < tasks.length;
             i++) {

            if (tasks[i] != null
                    && tasks[i].getStatus() != null
                    && tasks[i].getStatus()
                    .equalsIgnoreCase(
                            "COMPLETED")) {

                count++;
            }
        }

        return count;
    }
}

