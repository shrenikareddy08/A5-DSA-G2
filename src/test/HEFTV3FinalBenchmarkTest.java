package test;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import scheduling.HEFTSchedulerV2;
import scheduling.HEFTSchedulerV3;
import scheduling.Scheduler;

/**
 * Final three-way scheduler benchmark.
 *
 * Same 18 tasks, same documents, same dependencies and same resources
 * are used for all three schedulers.
 *
 * V3 receives a separate runtime-calibration phase. Calibration time is
 * intentionally NOT included in the scheduler makespan because it is a
 * profiling phase performed before the workload starts.
 */
public class HEFTV3FinalBenchmarkTest {

    private static final String LARGE_DOCUMENT = "bench_v3_large";
    private static final String DP_DOCUMENT = "bench_v3_dp";
    private static final int REPETITIONS = 3;

    public static void main(String[] args) {

        createBenchmarkDocuments();

        System.out.println("\n==============================================");
        System.out.println("   FINAL 3-WAY SCHEDULER BENCHMARK");
        System.out.println("==============================================");
        System.out.println("Schedulers: Intelligent vs HEFT V2 vs HEFT V3");
        System.out.println("Tasks     : 18");
        System.out.println("Resources : 3");
        System.out.println("Repetitions: " + REPETITIONS);
        System.out.println("==============================================");

        BenchmarkResult intelligent = runIntelligent();
        BenchmarkResult heftV2 = runHEFTV2();
        BenchmarkResult heftV3 = runHEFTV3();

        displayComparison(intelligent, heftV2, heftV3);
    }

    private static BenchmarkResult runIntelligent() {
        Scheduler scheduler = new Scheduler(18, 3);
        addResourcesToScheduler(scheduler);
        addTasksToScheduler(scheduler, "I");
        addDependencies(scheduler, "I");

        long start = System.currentTimeMillis();
        scheduler.execute();
        long elapsed = System.currentTimeMillis() - start;

        BenchmarkResult result = new BenchmarkResult("Intelligent");
        result.makespan = elapsed;
        result.completed = scheduler.getCompletedTasks();
        result.failed = scheduler.getFailedTasks();
        result.blocked = scheduler.getBlockedTasks();
        result.rounds = scheduler.getExecutionTime() > 0 ? estimateRounds(scheduler.getExecutionTime()) : 0;
        return result;
    }

    private static BenchmarkResult runHEFTV2() {
        HEFTSchedulerV2 scheduler = new HEFTSchedulerV2(18, 3);
        addResourcesToScheduler(scheduler);
        addTasksToScheduler(scheduler, "H2");
        addDependencies(scheduler, "H2");

        long start = System.currentTimeMillis();
        scheduler.execute();
        long elapsed = System.currentTimeMillis() - start;

        BenchmarkResult result = new BenchmarkResult("HEFT V2");
        result.makespan = elapsed;
        result.completed = scheduler.getCompletedTasks();
        result.failed = scheduler.getFailedTasks();
        result.blocked = scheduler.getBlockedTasks();
        result.rounds = scheduler.getScheduleRounds();
        return result;
    }

    private static BenchmarkResult runHEFTV3() {
        HEFTSchedulerV3 scheduler = new HEFTSchedulerV3(18, 3);
        addResourcesToScheduler(scheduler);
        addTasksToScheduler(scheduler, "H3");
        addDependencies(scheduler, "H3");

        long calibrationStart = System.currentTimeMillis();
        calibrateV3(scheduler);
        long calibrationTime = System.currentTimeMillis() - calibrationStart;

        System.out.println("\nHEFT V3 calibration time: " + calibrationTime + " ms");
        scheduler.displayHEFTRanks();

        long start = System.currentTimeMillis();
        scheduler.execute();
        long elapsed = System.currentTimeMillis() - start;

        BenchmarkResult result = new BenchmarkResult("HEFT V3");
        result.makespan = elapsed;
        result.completed = scheduler.getCompletedTasks();
        result.failed = scheduler.getFailedTasks();
        result.blocked = scheduler.getBlockedTasks();
        result.rounds = scheduler.getScheduleRounds();
        result.calibration = calibrationTime;
        return result;
    }

    private static void calibrateV3(HEFTSchedulerV3 scheduler) {
        calibrateOne(scheduler, "KMP", LARGE_DOCUMENT, "CAL-KMP");
        calibrateOne(scheduler, "Rabin-Karp", LARGE_DOCUMENT, "CAL-RK");
        calibrateOne(scheduler, "Z", LARGE_DOCUMENT, "CAL-Z");
        calibrateOne(scheduler, "Suffix Array", LARGE_DOCUMENT, "CAL-SA");
        calibrateOne(scheduler, "Edit Distance", DP_DOCUMENT, "CAL-ED");
        calibrateOne(scheduler, "Sequence Alignment", DP_DOCUMENT, "CAL-SA2");
    }

    private static void calibrateOne(
            HEFTSchedulerV3 scheduler,
            String algorithm,
            String document,
            String id) {

        Task task = new Task(
                id,
                "Calibration " + algorithm,
                document,
                algorithm,
                "MEDIUM",
                1,
                128,
                1);

        long start = System.nanoTime();
        TaskExecutor executor = new TaskExecutor();
        executor.execute(task);
        long end = System.nanoTime();

        double milliseconds = (end - start) / 1000000.0;

        if (task.getStatus() != null
                && task.getStatus().equalsIgnoreCase("COMPLETED")) {
            scheduler.setRuntimeEstimateForAlgorithm(algorithm, milliseconds);
            System.out.printf(
                    "V3 calibration %-18s : %.3f ms%n",
                    algorithm,
                    milliseconds);
        }
    }

    private static void addResourcesToScheduler(Scheduler scheduler) {
        scheduler.addResource(new Resource("R01", "Fast CPU", 4, 2048));
        scheduler.addResource(new Resource("R02", "Balanced CPU", 2, 1024));
        scheduler.addResource(new Resource("R03", "Compact CPU", 1, 512));
    }

    private static void addResourcesToScheduler(HEFTSchedulerV2 scheduler) {
        scheduler.addResource(new Resource("R01", "Fast CPU", 4, 2048));
        scheduler.addResource(new Resource("R02", "Balanced CPU", 2, 1024));
        scheduler.addResource(new Resource("R03", "Compact CPU", 1, 512));
    }

    private static void addResourcesToScheduler(HEFTSchedulerV3 scheduler) {
        scheduler.addResource(new Resource("R01", "Fast CPU", 4, 2048));
        scheduler.addResource(new Resource("R02", "Balanced CPU", 2, 1024));
        scheduler.addResource(new Resource("R03", "Compact CPU", 1, 512));
    }

    private static void addTasksToScheduler(Scheduler scheduler, String prefix) {
        for (int r = 1; r <= REPETITIONS; r++) {
            addSixTasks(scheduler, prefix + r + "_");
        }
    }

    private static void addTasksToScheduler(HEFTSchedulerV2 scheduler, String prefix) {
        for (int r = 1; r <= REPETITIONS; r++) {
            addSixTasks(scheduler, prefix + r + "_");
        }
    }

    private static void addTasksToScheduler(HEFTSchedulerV3 scheduler, String prefix) {
        for (int r = 1; r <= REPETITIONS; r++) {
            addSixTasks(scheduler, prefix + r + "_");
        }
    }

    private static void addSixTasks(Scheduler scheduler, String prefix) {
        scheduler.addTask(task(prefix + "01", LARGE_DOCUMENT, "KMP", "KMP Pattern Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "02", LARGE_DOCUMENT, "Rabin-Karp", "Rabin-Karp Keyword Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "03", LARGE_DOCUMENT, "Z", "Z Algorithm Analysis", "MEDIUM", 1, 128, 1));
        scheduler.addTask(task(prefix + "04", LARGE_DOCUMENT, "KMP", "KMP Secondary Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "05", LARGE_DOCUMENT, "Suffix Array", "Suffix Array Indexing", "HIGH", 2, 256, 2));
        scheduler.addTask(task(prefix + "06", DP_DOCUMENT, "Edit Distance", "Edit Distance Similarity", "MEDIUM", 2, 256, 2));
    }

    private static void addSixTasks(HEFTSchedulerV2 scheduler, String prefix) {
        scheduler.addTask(task(prefix + "01", LARGE_DOCUMENT, "KMP", "KMP Pattern Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "02", LARGE_DOCUMENT, "Rabin-Karp", "Rabin-Karp Keyword Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "03", LARGE_DOCUMENT, "Z", "Z Algorithm Analysis", "MEDIUM", 1, 128, 1));
        scheduler.addTask(task(prefix + "04", LARGE_DOCUMENT, "KMP", "KMP Secondary Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "05", LARGE_DOCUMENT, "Suffix Array", "Suffix Array Indexing", "HIGH", 2, 256, 2));
        scheduler.addTask(task(prefix + "06", DP_DOCUMENT, "Edit Distance", "Edit Distance Similarity", "MEDIUM", 2, 256, 2));
    }

    private static void addSixTasks(HEFTSchedulerV3 scheduler, String prefix) {
        scheduler.addTask(task(prefix + "01", LARGE_DOCUMENT, "KMP", "KMP Pattern Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "02", LARGE_DOCUMENT, "Rabin-Karp", "Rabin-Karp Keyword Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "03", LARGE_DOCUMENT, "Z", "Z Algorithm Analysis", "MEDIUM", 1, 128, 1));
        scheduler.addTask(task(prefix + "04", LARGE_DOCUMENT, "KMP", "KMP Secondary Search", "HIGH", 1, 128, 1));
        scheduler.addTask(task(prefix + "05", LARGE_DOCUMENT, "Suffix Array", "Suffix Array Indexing", "HIGH", 2, 256, 2));
        scheduler.addTask(task(prefix + "06", DP_DOCUMENT, "Edit Distance", "Edit Distance Similarity", "MEDIUM", 2, 256, 2));
    }

    private static Task task(
            String id,
            String document,
            String algorithm,
            String name,
            String priority,
            int cpu,
            int memory,
            int duration) {
        return new Task(id, name, document, algorithm, priority, cpu, memory, duration);
    }

    private static void addDependencies(Scheduler scheduler, String prefix) {
        for (int r = 1; r <= REPETITIONS; r++) {
            String p = prefix + r + "_";
            scheduler.addDependency(p + "01", p + "05");
            scheduler.addDependency(p + "02", p + "05");
            scheduler.addDependency(p + "03", p + "06");
            scheduler.addDependency(p + "04", p + "06");
        }
    }

    private static void addDependencies(HEFTSchedulerV2 scheduler, String prefix) {
        for (int r = 1; r <= REPETITIONS; r++) {
            String p = prefix + r + "_";
            scheduler.addDependency(p + "01", p + "05");
            scheduler.addDependency(p + "02", p + "05");
            scheduler.addDependency(p + "03", p + "06");
            scheduler.addDependency(p + "04", p + "06");
        }
    }

    private static void addDependencies(HEFTSchedulerV3 scheduler, String prefix) {
        for (int r = 1; r <= REPETITIONS; r++) {
            String p = prefix + r + "_";
            scheduler.addDependency(p + "01", p + "05");
            scheduler.addDependency(p + "02", p + "05");
            scheduler.addDependency(p + "03", p + "06");
            scheduler.addDependency(p + "04", p + "06");
        }
    }

    private static int estimateRounds(long ignored) {
        return 0;
    }

    private static void createBenchmarkDocuments() {
        try {
            java.io.File directory = new java.io.File("documents");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            java.io.File large =
                    new java.io.File(directory, LARGE_DOCUMENT + ".txt");
            java.io.File dp =
                    new java.io.File(directory, DP_DOCUMENT + ".txt");

            writeRepeated(large, 110);
            writeRepeated(dp, 4);

        } catch (Exception e) {
            System.out.println(
                    "Benchmark document creation failed: "
                    + e.getMessage());
        }
    }

    private static void writeRepeated(
            java.io.File file,
            int repetitions) throws Exception {

        java.io.FileWriter writer =
                new java.io.FileWriter(file);

        for (int i = 0; i < repetitions; i++) {
            writer.write(
                    "memory scheduling algorithm page prediction "
                    + "heterogeneous resource text processing pipeline ");
        }

        writer.close();
    }

    private static void displayComparison(
            BenchmarkResult intelligent,
            BenchmarkResult v2,
            BenchmarkResult v3) {

        printResult(intelligent);
        printResult(v2);
        printResult(v3);

        double best = intelligent.makespan;
        if (v2.makespan < best) {
            best = v2.makespan;
        }
        if (v3.makespan < best) {
            best = v3.makespan;
        }

        System.out.println("\n==============================================");
        System.out.println("             FINAL COMPARISON");
        System.out.println("==============================================");
        System.out.printf("%-18s %12s %12s %12s%n",
                "Scheduler", "Makespan(ms)", "Speedup", "Time Saved");
        System.out.println("----------------------------------------------");

        printComparisonRow(intelligent, best);
        printComparisonRow(v2, best);
        printComparisonRow(v3, best);

        System.out.println("----------------------------------------------");
        System.out.println("V3 calibration time (not in makespan): "
                + v3.calibration + " ms");
        System.out.println("==============================================");
        System.out.println("IMPORTANT: These numbers are valid only for this");
        System.out.println("specific workload, document size and resource model.");
    }

    private static void printResult(BenchmarkResult result) {
        double throughput = result.makespan > 0
                ? result.completed * 1000.0 / result.makespan
                : 0.0;

        System.out.println("\n" + result.name);
        System.out.println("Makespan       : " + result.makespan + " ms");
        System.out.println("Completed      : " + result.completed);
        System.out.println("Failed         : " + result.failed);
        System.out.println("Blocked        : " + result.blocked);
        System.out.println("Rounds         : " + result.rounds);
        System.out.printf("Throughput     : %.2f tasks/sec%n", throughput);
    }

    private static void printComparisonRow(
            BenchmarkResult result,
            double best) {

        double speedup = result.makespan > 0
                ? result.makespan / best
                : 0.0;
        double saved = result.makespan > 0
                ? (result.makespan - best) * 100.0 / result.makespan
                : 0.0;

        System.out.printf(
                "%-18s %12d %12.2fx %11.2f%%%n",
                result.name,
                result.makespan,
                speedup,
                saved);
    }

    private static class BenchmarkResult {
        String name;
        long makespan;
        int completed;
        int failed;
        int blocked;
        int rounds;
        long calibration;

        BenchmarkResult(String name) {
            this.name = name;
        }
    }
}
