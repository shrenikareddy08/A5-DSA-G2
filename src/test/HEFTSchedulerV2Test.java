package test;

import model.Resource;
import model.Task;
import scheduling.HEFTSchedulerV2;

public class HEFTSchedulerV2Test {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("==================================================");
        System.out.println("        HEFT V2 SCHEDULER TEST");
        System.out.println("==================================================");

        HEFTSchedulerV2 scheduler =
                new HEFTSchedulerV2(20, 5);

        /*
         * --------------------------------------------------------
         * TASKS
         * --------------------------------------------------------
         */

        Task h01 = new Task(
                "V201",
                "KMP Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                1,
                128,
                1);

        Task h02 = new Task(
                "V202",
                "Rabin-Karp Keyword Search",
                "D01",
                "RABIN_KARP",
                "HIGH",
                1,
                128,
                2);

        Task h03 = new Task(
                "V203",
                "Z Algorithm Analysis",
                "D01",
                "Z",
                "MEDIUM",
                1,
                128,
                3);

        Task h04 = new Task(
                "V204",
                "Suffix Array Indexing",
                "D01",
                "SUFFIX_ARRAY",
                "HIGH",
                2,
                256,
                4);

        Task h05 = new Task(
                "V205",
                "Edit Distance Similarity",
                "D01",
                "EDIT_DISTANCE",
                "MEDIUM",
                2,
                256,
                5);

        Task h06 = new Task(
                "V206",
                "Sequence Alignment",
                "D01",
                "SEQUENCE_ALIGNMENT",
                "MEDIUM",
                2,
                256,
                6);

        scheduler.addTask(h01);
        scheduler.addTask(h02);
        scheduler.addTask(h03);
        scheduler.addTask(h04);
        scheduler.addTask(h05);
        scheduler.addTask(h06);

        /*
         * --------------------------------------------------------
         * RESOURCES
         * --------------------------------------------------------
         */

        Resource r01 = new Resource(
                "R01",
                "High CPU",
                4,
                4096);

        Resource r02 = new Resource(
                "R02",
                "Standard CPU",
                2,
                2048);

        Resource r03 = new Resource(
                "R03",
                "Analysis CPU",
                4,
                4096);

        scheduler.addResource(r01);
        scheduler.addResource(r02);
        scheduler.addResource(r03);

        /*
         * --------------------------------------------------------
         * DEPENDENCIES
         * --------------------------------------------------------
         *
         * V01 ──┐
         *       ├──> V04 ──┐
         * V02 ──┘          │
         *                  ├──> V06
         * V03 ──┐          │
         *       ├──> V05 ──┘
         * V02 ──┘
         */

        scheduler.addDependency("V201", "V204");
        scheduler.addDependency("V202", "V204");

        scheduler.addDependency("V203", "V205");
        scheduler.addDependency("V202", "V205");

        scheduler.addDependency("V204", "V206");
        scheduler.addDependency("V205", "V206");

        /*
         * --------------------------------------------------------
         * EXECUTE
         * --------------------------------------------------------
         */

        scheduler.execute();

        /*
         * --------------------------------------------------------
         * VALIDATION
         * --------------------------------------------------------
         */

        System.out.println();
        System.out.println("==================================================");
        System.out.println("              HEFT V2 VALIDATION");
        System.out.println("==================================================");

        boolean passed = true;

        if (scheduler.getCompletedTasks() != 6) {

            System.out.println(
                    "FAIL: Expected 6 completed tasks.");

            passed = false;
        }

        if (scheduler.getFailedTasks() != 0) {

            System.out.println(
                    "FAIL: Expected 0 failed tasks.");

            passed = false;
        }

        if (scheduler.getBlockedTasks() != 0) {

            System.out.println(
                    "FAIL: Expected 0 blocked tasks.");

            passed = false;
        }

        if (scheduler.getAssignedResource("V201")
                .equals("NONE")) {

            System.out.println(
                    "FAIL: V201 has no HEFT resource.");

            passed = false;
        }

        if (scheduler.getAssignedResource("V202")
                .equals("NONE")) {

            System.out.println(
                    "FAIL: V202 has no HEFT resource.");

            passed = false;
        }

        if (scheduler.getAssignedResource("V203")
                .equals("NONE")) {

            System.out.println(
                    "FAIL: V203 has no HEFT resource.");

            passed = false;
        }

        if (scheduler.getAssignedResource("V204")
                .equals("NONE")) {

            System.out.println(
                    "FAIL: V204 has no HEFT resource.");

            passed = false;
        }

        if (scheduler.getAssignedResource("V205")
                .equals("NONE")) {

            System.out.println(
                    "FAIL: V205 has no HEFT resource.");

            passed = false;
        }

        if (scheduler.getAssignedResource("V206")
                .equals("NONE")) {

            System.out.println(
                    "FAIL: V206 has no HEFT resource.");

            passed = false;
        }

        if (passed) {

            System.out.println(
                    "Completed Tasks : "
                    + scheduler.getCompletedTasks());

            System.out.println(
                    "Failed Tasks    : "
                    + scheduler.getFailedTasks());

            System.out.println(
                    "Blocked Tasks   : "
                    + scheduler.getBlockedTasks());

            System.out.println(
                    "Rounds          : "
                    + scheduler.getScheduleRounds());

            System.out.println(
                    "V201 Resource   : "
                    + scheduler.getAssignedResource("V201"));

            System.out.println(
                    "V202 Resource   : "
                    + scheduler.getAssignedResource("V202"));

            System.out.println(
                    "V203 Resource   : "
                    + scheduler.getAssignedResource("V203"));

            System.out.println(
                    "V204 Resource   : "
                    + scheduler.getAssignedResource("V204"));

            System.out.println(
                    "V205 Resource   : "
                    + scheduler.getAssignedResource("V205"));

            System.out.println(
                    "V206 Resource   : "
                    + scheduler.getAssignedResource("V206"));

            System.out.println();
            System.out.println(
                    "SUCCESS: HEFT V2 validation passed.");
        }
        else {

            System.out.println();
            System.out.println(
                    "FAILURE: HEFT V2 validation failed.");
        }

        System.out.println(
                "==================================================");
    }
}