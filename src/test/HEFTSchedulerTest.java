package test;

import model.Resource;
import model.Task;
import scheduling.HEFTScheduler;

public class HEFTSchedulerTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "          HEFT SCHEDULER TEST");

        System.out.println(
                "==============================================");


        /*
         * --------------------------------------------------
         * CREATE TASKS
         * --------------------------------------------------
         */

        Task t01 =
                new Task(
                        "H01",
                        "KMP Pattern Search",
                        "D01",
                        "KMP",
                        "HIGH",
                        1,
                        128,
                        1);


        Task t02 =
                new Task(
                        "H02",
                        "Rabin-Karp Search",
                        "D01",
                        "Rabin-Karp",
                        "HIGH",
                        1,
                        128,
                        2);


        Task t03 =
                new Task(
                        "H03",
                        "Z Structure Analysis",
                        "D01",
                        "Z Algorithm",
                        "MEDIUM",
                        1,
                        128,
                        3);


        Task t04 =
                new Task(
                        "H04",
                        "Suffix Array Indexing",
                        "D01",
                        "Suffix Array",
                        "HIGH",
                        2,
                        256,
                        4);


        Task t05 =
                new Task(
                        "H05",
                        "Edit Distance",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        256,
                        5);


        Task t06 =
                new Task(
                        "H06",
                        "Sequence Alignment",
                        "D01",
                        "Sequence Alignment",
                        "MEDIUM",
                        2,
                        256,
                        6);


        /*
         * --------------------------------------------------
         * CREATE HEFT SCHEDULER
         * --------------------------------------------------
         */

        HEFTScheduler scheduler =
                new HEFTScheduler(
                        6,
                        3);


        /*
         * --------------------------------------------------
         * ADD TASKS
         * --------------------------------------------------
         */

        scheduler.addTask(t01);
        scheduler.addTask(t02);
        scheduler.addTask(t03);
        scheduler.addTask(t04);
        scheduler.addTask(t05);
        scheduler.addTask(t06);


        /*
         * --------------------------------------------------
         * ADD RESOURCES
         * --------------------------------------------------
         */

        scheduler.addResource(
                new Resource(
                        "R01",
                        "High CPU Resource",
                        4,
                        4096));


        scheduler.addResource(
                new Resource(
                        "R02",
                        "Standard Resource",
                        2,
                        2048));


        scheduler.addResource(
                new Resource(
                        "R03",
                        "Analysis Resource",
                        4,
                        4096));


        /*
         * --------------------------------------------------
         * DEPENDENCY GRAPH
         *
         * H01 ----\
         *          \
         *           H04 ----\
         *          /         \
         * H02 ----/           H06
         *
         * H03 ----------------/
         *
         * H03 -> H05
         * H02 -> H05
         * --------------------------------------------------
         */

        scheduler.addDependency(
                "H01",
                "H04");

        scheduler.addDependency(
                "H02",
                "H04");

        scheduler.addDependency(
                "H03",
                "H05");

        scheduler.addDependency(
                "H02",
                "H05");

        scheduler.addDependency(
                "H04",
                "H06");

        scheduler.addDependency(
                "H05",
                "H06");


        /*
         * --------------------------------------------------
         * CALCULATE RANKS
         * --------------------------------------------------
         */

        scheduler.calculateUpwardRanks();

        scheduler.displayHEFTRanks();


        /*
         * --------------------------------------------------
         * CREATE RESOURCE PLAN
         * --------------------------------------------------
         */

        scheduler.createHEFTPlan();

        scheduler.displayHEFTPlan();


        /*
         * --------------------------------------------------
         * BASIC RANK VALIDATION
         * --------------------------------------------------
         *
         * H01 and H02 should have higher rank than
         * completely independent leaf-style tasks
         * because they contribute to downstream work.
         */

        double rankH01 =
                scheduler.getUpwardRank("H01");

        double rankH02 =
                scheduler.getUpwardRank("H02");

        double rankH06 =
                scheduler.getUpwardRank("H06");


        System.out.println(
                "\n==============================================");

        System.out.println(
                "             RANK VALIDATION");

        System.out.println(
                "==============================================");

        System.out.printf(
                "H01 Rank : %.2f%n",
                rankH01);

        System.out.printf(
                "H02 Rank : %.2f%n",
                rankH02);

        System.out.printf(
                "H06 Rank : %.2f%n",
                rankH06);


        boolean rankPassed =
                rankH01 > 0.0
                && rankH02 > 0.0
                && rankH06 > 0.0;


        if (rankPassed) {

            System.out.println(
                    "SUCCESS: Upward rank calculation passed.");

        } else {

            System.out.println(
                    "FAILURE: Upward rank calculation failed.");
        }


        /*
         * --------------------------------------------------
         * RESOURCE PLAN VALIDATION
         * --------------------------------------------------
         */

        String resourceH01 =
                scheduler.getAssignedResource(
                        "H01");

        String resourceH06 =
                scheduler.getAssignedResource(
                        "H06");


        System.out.println(
                "\n==============================================");

        System.out.println(
                "          RESOURCE PLAN VALIDATION");

        System.out.println(
                "==============================================");

        System.out.println(
                "H01 assigned resource : "
                + resourceH01);

        System.out.println(
                "H06 assigned resource : "
                + resourceH06);


        boolean resourcePassed =
                resourceH01 != null
                && resourceH06 != null;


        if (resourcePassed) {

            System.out.println(
                    "SUCCESS: HEFT resource assignment passed.");

        } else {

            System.out.println(
                    "FAILURE: HEFT resource assignment failed.");
        }


        /*
         * --------------------------------------------------
         * EXECUTE REAL PIPELINE
         * --------------------------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       REAL HEFT PIPELINE EXECUTION");

        System.out.println(
                "==============================================");


        /*
         * Reset all tasks because the scheduler will
         * execute them from WAITING state.
         */

        t01.setStatus("WAITING");
        t02.setStatus("WAITING");
        t03.setStatus("WAITING");
        t04.setStatus("WAITING");
        t05.setStatus("WAITING");
        t06.setStatus("WAITING");


        scheduler.execute();


        /*
         * --------------------------------------------------
         * FINAL VALIDATION
         * --------------------------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             FINAL VALIDATION");

        System.out.println(
                "==============================================");

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


        boolean executionPassed =
                scheduler.getCompletedTasks()
                == 6
                && scheduler.getFailedTasks()
                == 0
                && scheduler.getBlockedTasks()
                == 0;


        if (rankPassed
                && resourcePassed
                && executionPassed) {

            System.out.println(
                    "\nSUCCESS: HEFT scheduler test passed.");

        } else {

            System.out.println(
                    "\nFAILURE: HEFT scheduler test failed.");
        }


        System.out.println(
                "==============================================");
    }
}