package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import scheduling.Scheduler;

public class LargeWorkloadBenchmarkTest {

    /*
     * ============================================================
     * BENCHMARK CONFIGURATION
     * ============================================================
     */

    private static final int REPETITIONS = 3;

    /*
     * Large enough to create meaningful work for:
     *
     * KMP
     * Rabin-Karp
     * Z Algorithm
     * Suffix Array
     */
    private static final int LARGE_DOCUMENT_SIZE = 10000;

    /*
     * Edit Distance is O(n²), so we deliberately keep
     * this workload smaller.
     */
    private static final int DP_DOCUMENT_SIZE = 300;

    private static final String LARGE_DOCUMENT_ID =
            "BENCH_LARGE";

    private static final String DP_DOCUMENT_ID =
            "BENCH_DP";


    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       LARGE WORKLOAD PERFORMANCE BENCHMARK");

        System.out.println(
                "==============================================");

        System.out.println(
                "Repetitions                : "
                + REPETITIONS);

        System.out.println(
                "Large workload size        : "
                + LARGE_DOCUMENT_SIZE
                + " characters");

        System.out.println(
                "DP workload size           : "
                + DP_DOCUMENT_SIZE
                + " characters");

        System.out.println(
                "Total task executions      : "
                + (6 * REPETITIONS));


        /*
         * ============================================================
         * STEP 1
         * GENERATE TEMPORARY WORKLOAD
         * ============================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       STEP 1: GENERATING WORKLOAD");

        System.out.println(
                "==============================================");

        boolean documentsCreated =
                createBenchmarkDocuments();

        if (!documentsCreated) {

            System.out.println(
                    "FAILURE: Could not create benchmark documents.");

            return;
        }

        System.out.println(
                "Large benchmark document created.");

        System.out.println(
                "DP benchmark document created.");


        /*
         * ============================================================
         * STEP 2
         * SEQUENTIAL EXECUTION
         * ============================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       PART 1: SEQUENTIAL EXECUTION");

        System.out.println(
                "==============================================");

        Task[] sequentialTasks =
                createSequentialTasks();

        TaskExecutor executor =
                new TaskExecutor();

        PrintStream originalOutput =
                System.out;

        PrintStream silentOutput =
                new PrintStream(
                        new ByteArrayOutputStream());

        /*
         * Prevent algorithm console output from affecting
         * benchmark timing.
         */
        System.setOut(silentOutput);

        long sequentialStart =
                System.nanoTime();

        for (int repetition = 1;
             repetition <= REPETITIONS;
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

        System.setOut(originalOutput);

        silentOutput.close();

        double sequentialTimeMs =
                (sequentialEnd
                - sequentialStart)
                / 1_000_000.0;

        int sequentialCompleted =
                countCompleted(
                        sequentialTasks);

        int totalExecutions =
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
                + totalExecutions);


        /*
         * ============================================================
         * STEP 3
         * INTELLIGENT PARALLEL EXECUTION
         * ============================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "   PART 2: INTELLIGENT PARALLEL EXECUTION");

        System.out.println(
                "==============================================");

        Task[] parallelTasks =
                createParallelTasks();

        Scheduler scheduler =
                new Scheduler(
                        parallelTasks.length,
                        3);


        /*
         * Add every task.
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
                        "High CPU Worker",
                        4,
                        4096));

        scheduler.addResource(
                new Resource(
                        "R02",
                        "Standard Worker",
                        2,
                        2048));

        scheduler.addResource(
                new Resource(
                        "R03",
                        "Analysis Worker",
                        4,
                        4096));


        /*
         * Add dependencies.
         */

        addDependencies(
                scheduler,
                REPETITIONS);


        /*
         * Suppress scheduler console output during
         * performance measurement.
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

        System.setOut(originalOutput);

        silentOutput.close();

        double parallelTimeMs =
                (parallelEnd
                - parallelStart)
                / 1_000_000.0;


        /*
         * Scheduler statistics.
         */

        int parallelCompleted =
                scheduler.getCompletedTasks();

        int parallelFailed =
                scheduler.getFailedTasks();

        int parallelBlocked =
                scheduler.getBlockedTasks();


        System.out.println(
                "Intelligent parallel execution completed.");

        System.out.printf(
                "Parallel time : %.3f ms%n",
                parallelTimeMs);

        System.out.println(
                "Completed     : "
                + parallelCompleted);

        System.out.println(
                "Failed        : "
                + parallelFailed);

        System.out.println(
                "Blocked       : "
                + parallelBlocked);


        /*
         * ============================================================
         * STEP 4
         * PERFORMANCE CALCULATIONS
         * ============================================================
         */

        double speedup =
                sequentialTimeMs
                / parallelTimeMs;

        double timeSaved =
                ((sequentialTimeMs
                - parallelTimeMs)
                / sequentialTimeMs)
                * 100.0;

        double sequentialThroughput =
                totalExecutions
                / (sequentialTimeMs / 1000.0);

        double parallelThroughput =
                parallelCompleted
                / (parallelTimeMs / 1000.0);


        /*
         * ============================================================
         * FINAL RESULTS
         * ============================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       LARGE WORKLOAD BENCHMARK RESULTS");

        System.out.println(
                "==============================================");

        System.out.println(
                "Large document size       : "
                + LARGE_DOCUMENT_SIZE
                + " characters");

        System.out.println(
                "DP document size          : "
                + DP_DOCUMENT_SIZE
                + " characters");

        System.out.println(
                "Total task executions     : "
                + totalExecutions);

        System.out.printf(
                "Sequential time           : %.3f ms%n",
                sequentialTimeMs);

        System.out.printf(
                "Parallel time             : %.3f ms%n",
                parallelTimeMs);

        System.out.printf(
                "Speedup                   : %.2fx%n",
                speedup);

        System.out.printf(
                "Time saved                : %.2f%%%n",
                timeSaved);

        System.out.printf(
                "Sequential throughput     : %.2f tasks/sec%n",
                sequentialThroughput);

        System.out.printf(
                "Parallel throughput       : %.2f tasks/sec%n",
                parallelThroughput);

        System.out.println(
                "Parallel completed        : "
                + parallelCompleted);

        System.out.println(
                "Parallel failed           : "
                + parallelFailed);

        System.out.println(
                "Parallel blocked          : "
                + parallelBlocked);

        System.out.println(
                "==============================================");


        /*
         * ============================================================
         * VALIDATION
         * ============================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             VALIDATION");

        System.out.println(
                "==============================================");

        boolean passed = true;


        if (parallelCompleted
                != parallelTasks.length) {

            passed = false;

            System.out.println(
                    "FAIL: Not all parallel tasks completed.");
        }


        if (parallelFailed != 0) {

            passed = false;

            System.out.println(
                    "FAIL: Parallel failures detected.");
        }


        if (parallelBlocked != 0) {

            passed = false;

            System.out.println(
                    "FAIL: Parallel blocked tasks detected.");
        }


        if (sequentialTimeMs <= 0.0) {

            passed = false;

            System.out.println(
                    "FAIL: Invalid sequential timing.");
        }


        if (parallelTimeMs <= 0.0) {

            passed = false;

            System.out.println(
                    "FAIL: Invalid parallel timing.");
        }


        if (passed) {

            System.out.println(
                    "SUCCESS: Large workload benchmark completed.");

        } else {

            System.out.println(
                    "FAILURE: Large workload benchmark failed.");
        }

        System.out.println(
                "==============================================");


        /*
         * ============================================================
         * CLEANUP
         * ============================================================
         */

        cleanupBenchmarkDocuments();

        System.out.println(
                "\nTemporary benchmark documents removed.");

        System.out.println(
                "Benchmark finished.");
    }


    /*
     * ============================================================
     * CREATE BENCHMARK DOCUMENTS
     * ============================================================
     */

    private static boolean createBenchmarkDocuments() {

        try {

            File folder =
                    new File("documents");

            if (!folder.exists()) {

                if (!folder.mkdirs()) {

                    return false;
                }
            }


            /*
             * LARGE DOCUMENT
             */

            File largeFile =
                    new File(
                            folder,
                            LARGE_DOCUMENT_ID
                            + ".txt");

            FileWriter largeWriter =
                    new FileWriter(
                            largeFile);


            for (int i = 0;
                 i < LARGE_DOCUMENT_SIZE;
                 i++) {

                if (i % 97 == 0) {

                    largeWriter.write(
                            "memory ");

                } else if (i % 53 == 0) {

                    largeWriter.write(
                            "system ");

                } else if (i % 41 == 0) {

                    largeWriter.write(
                            "resource ");

                } else if (i % 29 == 0) {

                    largeWriter.write(
                            "pipeline ");

                } else {

                    largeWriter.write(
                            "text ");
                }
            }

            largeWriter.close();


            /*
             * DP DOCUMENT
             */

            File dpFile =
                    new File(
                            folder,
                            DP_DOCUMENT_ID
                            + ".txt");

            FileWriter dpWriter =
                    new FileWriter(
                            dpFile);


            for (int i = 0;
                 i < DP_DOCUMENT_SIZE;
                 i++) {

                if (i % 37 == 0) {

                    dpWriter.write(
                            "memory ");

                } else if (i % 19 == 0) {

                    dpWriter.write(
                            "algorithm ");

                } else if (i % 11 == 0) {

                    dpWriter.write(
                            "resource ");

                } else {

                    dpWriter.write(
                            "analysis ");
                }
            }

            dpWriter.close();

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Benchmark document creation error: "
                    + e.getMessage());

            return false;
        }
    }


    /*
     * ============================================================
     * SEQUENTIAL TASK DEFINITIONS
     * ============================================================
     */

    private static Task[] createSequentialTasks() {

        Task[] tasks =
                new Task[6];


        tasks[0] =
                new Task(
                        "SEQ01",
                        "Large Pattern Search",
                        LARGE_DOCUMENT_ID,
                        "KMP",
                        "HIGH",
                        1,
                        512,
                        1);


        tasks[1] =
                new Task(
                        "SEQ02",
                        "Large Keyword Search",
                        LARGE_DOCUMENT_ID,
                        "Rabin-Karp",
                        "HIGH",
                        1,
                        512,
                        1);


        tasks[2] =
                new Task(
                        "SEQ03",
                        "Large Structure Analysis",
                        LARGE_DOCUMENT_ID,
                        "Z Algorithm",
                        "MEDIUM",
                        1,
                        512,
                        1);


        tasks[3] =
                new Task(
                        "SEQ04",
                        "Large Pattern Search",
                        LARGE_DOCUMENT_ID,
                        "KMP",
                        "HIGH",
                        1,
                        512,
                        1);


        tasks[4] =
                new Task(
                        "SEQ05",
                        "Large Text Indexing",
                        LARGE_DOCUMENT_ID,
                        "Suffix Array",
                        "HIGH",
                        2,
                        1024,
                        2);


        tasks[5] =
                new Task(
                        "SEQ06",
                        "Document Similarity",
                        DP_DOCUMENT_ID,
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        1024,
                        3);


        return tasks;
    }


    /*
     * ============================================================
     * PARALLEL TASK DEFINITIONS
     * ============================================================
     */

    private static Task[] createParallelTasks() {

        Task[] tasks =
                new Task[
                        6 * REPETITIONS];

        int index = 0;


        for (int repetition = 1;
             repetition <= REPETITIONS;
             repetition++) {

            String suffix =
                    String.valueOf(
                            repetition);


            tasks[index++] =
                    new Task(
                            "B01_" + suffix,
                            "Large Pattern Search",
                            LARGE_DOCUMENT_ID,
                            "KMP",
                            "HIGH",
                            1,
                            512,
                            1);


            tasks[index++] =
                    new Task(
                            "B02_" + suffix,
                            "Large Keyword Search",
                            LARGE_DOCUMENT_ID,
                            "Rabin-Karp",
                            "HIGH",
                            1,
                            512,
                            1);


            tasks[index++] =
                    new Task(
                            "B03_" + suffix,
                            "Large Structure Analysis",
                            LARGE_DOCUMENT_ID,
                            "Z Algorithm",
                            "MEDIUM",
                            1,
                            512,
                            1);


            tasks[index++] =
                    new Task(
                            "B04_" + suffix,
                            "Large Pattern Search",
                            LARGE_DOCUMENT_ID,
                            "KMP",
                            "HIGH",
                            1,
                            512,
                            1);


            tasks[index++] =
                    new Task(
                            "B05_" + suffix,
                            "Large Text Indexing",
                            LARGE_DOCUMENT_ID,
                            "Suffix Array",
                            "HIGH",
                            2,
                            1024,
                            2);


            tasks[index++] =
                    new Task(
                            "B06_" + suffix,
                            "Document Similarity",
                            DP_DOCUMENT_ID,
                            "Edit Distance",
                            "MEDIUM",
                            2,
                            1024,
                            3);
        }


        return tasks;
    }


    /*
     * ============================================================
     * DEPENDENCY STRUCTURE
     * ============================================================
     */

    private static void addDependencies(
            Scheduler scheduler,
            int repetitions) {

        for (int repetition = 1;
             repetition <= repetitions;
             repetition++) {

            String suffix =
                    String.valueOf(
                            repetition);


            /*
             * B01 + B02
             *       ↓
             *      B05
             */

            scheduler.addDependency(
                    "B01_" + suffix,
                    "B05_" + suffix);

            scheduler.addDependency(
                    "B02_" + suffix,
                    "B05_" + suffix);


            /*
             * B03 + B04
             *       ↓
             *      B06
             */

            scheduler.addDependency(
                    "B03_" + suffix,
                    "B06_" + suffix);

            scheduler.addDependency(
                    "B04_" + suffix,
                    "B06_" + suffix);
        }
    }


    /*
     * ============================================================
     * COUNT COMPLETED
     * ============================================================
     */

    private static int countCompleted(
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


    /*
     * ============================================================
     * CLEANUP
     * ============================================================
     */

    private static void cleanupBenchmarkDocuments() {

        File largeFile =
                new File(
                        "documents/"
                        + LARGE_DOCUMENT_ID
                        + ".txt");

        File dpFile =
                new File(
                        "documents/"
                        + DP_DOCUMENT_ID
                        + ".txt");


        if (largeFile.exists()) {

            largeFile.delete();
        }


        if (dpFile.exists()) {

            dpFile.delete();
        }
    }
}