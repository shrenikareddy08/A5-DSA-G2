package test;

import model.Task;
import model.Resource;
import scheduling.WorkloadAnalyzer;
import scheduling.DynamicTaskGenerator;
import scheduling.Scheduler;

public class DynamicIntelligentPipelineTest {

    public static void main(String[] args) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "     DYNAMIC INTELLIGENT PIPELINE TEST");

        System.out.println(
                "==============================================");

        /*
         * ==================================================
         * STEP 1: REAL DOCUMENT DISCOVERY
         * ==================================================
         */

        System.out.println(
                "\n[STEP 1] REAL DOCUMENT DISCOVERY");

        WorkloadAnalyzer analyzer =
                new WorkloadAnalyzer();

        WorkloadAnalyzer.WorkloadProfile[] profiles =
                analyzer.analyzeAllDocuments();

        if (profiles == null
                || profiles.length == 0) {

            System.out.println(
                    "FAILURE: No documents discovered.");

            return;
        }

        System.out.println(
                "Documents discovered: "
                + profiles.length);

        /*
         * ==================================================
         * STEP 2: DYNAMIC TASK GENERATION
         * ==================================================
         */

        System.out.println(
                "\n[STEP 2] DYNAMIC TASK GENERATION");

        DynamicTaskGenerator generator =
                new DynamicTaskGenerator();

        Task[] tasks =
                generator.generateTasks(
                        profiles,
                        20);

        if (tasks == null
                || tasks.length == 0) {

            System.out.println(
                    "FAILURE: No tasks generated.");

            return;
        }

        generator.displayGeneratedTasks(
                tasks);

        System.out.println(
                "\nDynamically generated tasks: "
                + tasks.length);

        /*
         * ==================================================
         * STEP 3: CREATE SCHEDULER
         * ==================================================
         */

        System.out.println(
                "\n[STEP 3] INTELLIGENT SCHEDULER CREATION");

        Scheduler scheduler =
                new Scheduler(
                        tasks.length,
                        3);

        /*
         * ==================================================
         * STEP 4: ADD DYNAMIC TASKS
         * ==================================================
         */

        System.out.println(
                "\n[STEP 4] ADDING DYNAMIC TASKS");

        for (int i = 0;
             i < tasks.length;
             i++) {

            scheduler.addTask(
                    tasks[i]);
        }

        /*
         * ==================================================
         * STEP 5: CREATE PROCESSING RESOURCES
         * ==================================================
         *
         * Resources are intentionally heterogeneous.
         *
         * R01 = general processing worker
         * R02 = smaller worker
         * R03 = analysis worker
         *
         * Memory is sufficient for the current workload
         * so that the successful integration demo does
         * not deadlock.
         */

        System.out.println(
                "\n[STEP 5] RESOURCE INITIALIZATION");

        Resource r01 =
                new Resource(
                        "R01",
                        "Text Processing Worker 1",
                        4,
                        2000);

        Resource r02 =
                new Resource(
                        "R02",
                        "Text Processing Worker 2",
                        2,
                        1500);

        Resource r03 =
                new Resource(
                        "R03",
                        "Analysis Worker",
                        4,
                        4000);

        scheduler.addResource(
                r01);

        scheduler.addResource(
                r02);

        scheduler.addResource(
                r03);

        /*
         * ==================================================
         * STEP 6: DISPLAY INITIAL STATE
         * ==================================================
         */

        System.out.println(
                "\n[STEP 6] INITIAL PIPELINE STATE");

        scheduler.displayTasks();

        scheduler.displayResources();

        /*
         * ==================================================
         * STEP 7: CREATE MEANINGFUL DEPENDENCIES
         * ==================================================
         *
         * For every document:
         *
         * KMP and Rabin-Karp are independent searches.
         *
         * Z Algorithm is independent analysis.
         *
         * Edit Distance depends on the search/analysis
         * stage.
         *
         * Sequence Alignment depends on Edit Distance.
         *
         * Suffix Array depends on the completed text
         * analysis stage.
         *
         * Because DynamicTaskGenerator creates six tasks
         * per document in this order:
         *
         * 0 = KMP
         * 1 = Rabin-Karp
         * 2 = Edit Distance
         * 3 = Z Algorithm
         * 4 = Suffix Array
         * 5 = Sequence Alignment
         */

        System.out.println(
                "\n[STEP 7] BUILDING DEPENDENCY GRAPH");

        for (int i = 0;
             i < tasks.length;
             i += 6) {

            if (i + 5 >= tasks.length) {
                break;
            }

            Task kmp =
                    tasks[i];

            Task rabinKarp =
                    tasks[i + 1];

            Task editDistance =
                    tasks[i + 2];

            Task zAlgorithm =
                    tasks[i + 3];

            Task suffixArray =
                    tasks[i + 4];

            Task sequenceAlignment =
                    tasks[i + 5];

            /*
             * Search/analysis tasks execute independently.
             */

            /*
             * Edit Distance requires the initial
             * document analysis/search stage.
             */
            scheduler.addDependency(
                    kmp.getTaskId(),
                    editDistance.getTaskId());

            scheduler.addDependency(
                    rabinKarp.getTaskId(),
                    editDistance.getTaskId());

            scheduler.addDependency(
                    zAlgorithm.getTaskId(),
                    editDistance.getTaskId());

            /*
             * Suffix Array requires the document analysis
             * stage.
             */
            scheduler.addDependency(
                    zAlgorithm.getTaskId(),
                    suffixArray.getTaskId());

            /*
             * Sequence Alignment requires Edit Distance.
             */
            scheduler.addDependency(
                    editDistance.getTaskId(),
                    sequenceAlignment.getTaskId());
        }

        System.out.println(
                "Dependency graph created successfully.");

        /*
         * ==================================================
         * STEP 8: EXECUTE COMPLETE PIPELINE
         * ==================================================
         */

        System.out.println(
                "\n[STEP 8] EXECUTING COMPLETE PIPELINE");

        scheduler.generateSchedule();

        /*
         * ==================================================
         * STEP 9: FINAL VALIDATION
         * ==================================================
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          INTEGRATION VALIDATION");

        System.out.println(
                "==============================================");

        boolean allCompleted =
                true;

        for (int i = 0;
             i < tasks.length;
             i++) {

            if (!tasks[i].getStatus()
                    .equalsIgnoreCase("COMPLETED")) {

                allCompleted =
                        false;

                System.out.println(
                        tasks[i].getTaskId()
                        + " -> "
                        + tasks[i].getStatus());
            }
        }

        if (allCompleted) {

            System.out.println(
                    "Generated Tasks : "
                    + tasks.length);

            System.out.println(
                    "Completed Tasks : "
                    + tasks.length);

            System.out.println(
                    "Failed Tasks    : 0");

            System.out.println(
                    "Blocked Tasks   : 0");

            System.out.println(
                    "\nSUCCESS: Dynamic intelligent pipeline "
                    + "integration passed.");

        } else {

            System.out.println(
                    "\nFAILURE: Not all dynamically generated "
                    + "tasks completed.");
        }

        System.out.println(
                "==============================================");
    }
}