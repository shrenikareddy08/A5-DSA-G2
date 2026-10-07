package test;

import model.Resource;
import model.Task;
import scheduling.HEFTSchedulerV3;

public class HEFTSchedulerV3Test {

    private static final String DOCUMENT = "D01";

    public static void main(String[] args) {

        System.out.println("\n============================================================");
        System.out.println("              HEFT V3 RUNTIME-AWARE TEST");
        System.out.println("============================================================");

        HEFTSchedulerV3 scheduler = new HEFTSchedulerV3(6, 3);

        System.out.println("\nRESOURCES CREATED");
        System.out.println("-----------------");

        scheduler.addResource(new Resource("R01", "Fast CPU", 4, 4096));
        scheduler.addResource(new Resource("R02", "Balanced CPU", 2, 2048));
        scheduler.addResource(new Resource("R03", "Compact CPU", 1, 1024));

        System.out.println("R01 -> CPU: 4 | Memory: 4096 MB");
        System.out.println("R02 -> CPU: 2 | Memory: 2048 MB");
        System.out.println("R03 -> CPU: 1 | Memory: 1024 MB");

        System.out.println("\nTASKS CREATED");
        System.out.println("-------------");

        scheduler.addTask(new Task(
                "V301", "KMP Pattern Search", DOCUMENT,
                "KMP", "HIGH", 1, 128, 1));

        scheduler.addTask(new Task(
                "V302", "Rabin-Karp Keyword Search", DOCUMENT,
                "Rabin-Karp", "HIGH", 1, 128, 1));

        scheduler.addTask(new Task(
                "V303", "Z Algorithm Analysis", DOCUMENT,
                "Z", "MEDIUM", 1, 128, 1));

        scheduler.addTask(new Task(
                "V304", "Suffix Array Indexing", DOCUMENT,
                "Suffix Array", "HIGH", 2, 256, 2));

        scheduler.addTask(new Task(
                "V305", "Edit Distance Similarity", DOCUMENT,
                "Edit Distance", "MEDIUM", 2, 256, 2));

        scheduler.addTask(new Task(
                "V306", "Sequence Alignment", DOCUMENT,
                "Sequence Alignment", "MEDIUM", 2, 256, 2));

        System.out.println("V301 -> KMP");
        System.out.println("V302 -> Rabin-Karp");
        System.out.println("V303 -> Z Algorithm");
        System.out.println("V304 -> Suffix Array");
        System.out.println("V305 -> Edit Distance");
        System.out.println("V306 -> Sequence Alignment");

        System.out.println("\nDEPENDENCIES CREATED");
        System.out.println("--------------------");

        scheduler.addDependency("V301", "V304");
        scheduler.addDependency("V302", "V304");
        scheduler.addDependency("V304", "V306");
        scheduler.addDependency("V303", "V305");

        System.out.println("V301 -> V304");
        System.out.println("V302 -> V304");
        System.out.println("V304 -> V306");
        System.out.println("V303 -> V305");

        scheduler.displayHEFTRanks();

        System.out.println("\n============================================================");
        System.out.println("              STARTING HEFT V3 EXECUTION");
        System.out.println("============================================================");

        scheduler.execute();

        System.out.println("\n============================================================");
        System.out.println("                 FINAL VALIDATION");
        System.out.println("============================================================");
        System.out.println("Total Tasks       : " + scheduler.getTaskCount());
        System.out.println("Completed Tasks   : " + scheduler.getCompletedTasks());
        System.out.println("Failed Tasks      : " + scheduler.getFailedTasks());
        System.out.println("Blocked Tasks     : " + scheduler.getBlockedTasks());
        System.out.println("Scheduling Rounds : " + scheduler.getScheduleRounds());
        System.out.println("Makespan          : " + scheduler.getMakespanMs() + " ms");
        System.out.println("============================================================");

        if (scheduler.getCompletedTasks() == 6
                && scheduler.getFailedTasks() == 0
                && scheduler.getBlockedTasks() == 0) {
            System.out.println("SUCCESS: HEFT V3 VALIDATION PASSED");
        } else {
            System.out.println("FAILURE: HEFT V3 VALIDATION FAILED");
        }

        System.out.println("============================================================");
        System.out.println("             LEARNED RUNTIME ESTIMATES");
        System.out.println("============================================================");

        printRuntime(scheduler, "V301", "KMP");
        printRuntime(scheduler, "V302", "Rabin-Karp");
        printRuntime(scheduler, "V303", "Z Algorithm");
        printRuntime(scheduler, "V304", "Suffix Array");
        printRuntime(scheduler, "V305", "Edit Distance");
        printRuntime(scheduler, "V306", "Sequence Alignment");

        System.out.println("============================================================");
        System.out.println("                    TEST COMPLETE");
        System.out.println("============================================================");
    }

    private static void printRuntime(
            HEFTSchedulerV3 scheduler,
            String taskId,
            String name) {

        System.out.printf(
                "%s %-18s : %.3f ms%n",
                taskId,
                name,
                scheduler.getRuntimeEstimate(taskId));
    }
}
