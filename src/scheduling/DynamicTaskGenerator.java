package scheduling;

import model.Task;

public class DynamicTaskGenerator {

    private static final int TASKS_PER_DOCUMENT = 6;

    /*
     * Original API.
     *
     * This method keeps the existing DynamicTaskGeneratorTest
     * working exactly as before.
     *
     * It analyzes the supplied documents first and then creates
     * tasks using the real workload characteristics.
     */
    public Task[] generateTasks(String[] documentIds, int maxTasks) {

        if (documentIds == null || documentIds.length == 0) {
            return new Task[0];
        }

        WorkloadAnalyzer analyzer =
                new WorkloadAnalyzer();

        WorkloadAnalyzer.WorkloadProfile[] profiles =
                analyzer.analyzeAllDocuments();

        if (profiles == null || profiles.length == 0) {
            return new Task[0];
        }

        return generateTasks(profiles, maxTasks);
    }

    /*
     * Workload-aware task generation.
     *
     * CPU, memory and duration are derived from the actual
     * WorkloadAnalyzer profile and adjusted according to
     * algorithm complexity.
     */
    public Task[] generateTasks(
            WorkloadAnalyzer.WorkloadProfile[] profiles,
            int maxTasks) {

        if (profiles == null || profiles.length == 0) {
            return new Task[0];
        }

        int requiredTasks =
                profiles.length * TASKS_PER_DOCUMENT;

        int taskCount = requiredTasks;

        if (maxTasks > 0 && maxTasks < taskCount) {
            taskCount = maxTasks;
        }

        Task[] tasks =
                new Task[taskCount];

        int index = 0;
        int taskNumber = 1;

        for (int i = 0;
             i < profiles.length && index < taskCount;
             i++) {

            WorkloadAnalyzer.WorkloadProfile profile =
                    profiles[i];

            /*
             * KMP
             */
            if (index < taskCount) {

                tasks[index++] =
                        createKMPTask(
                                profile,
                                taskNumber++);
            }

            /*
             * Rabin-Karp
             */
            if (index < taskCount) {

                tasks[index++] =
                        createRabinKarpTask(
                                profile,
                                taskNumber++);
            }

            /*
             * Edit Distance
             */
            if (index < taskCount) {

                tasks[index++] =
                        createEditDistanceTask(
                                profile,
                                taskNumber++);
            }

            /*
             * Z Algorithm
             */
            if (index < taskCount) {

                tasks[index++] =
                        createZAlgorithmTask(
                                profile,
                                taskNumber++);
            }

            /*
             * Suffix Array
             */
            if (index < taskCount) {

                tasks[index++] =
                        createSuffixArrayTask(
                                profile,
                                taskNumber++);
            }

            /*
             * Sequence Alignment
             */
            if (index < taskCount) {

                tasks[index++] =
                        createSequenceAlignmentTask(
                                profile,
                                taskNumber++);
            }
        }

        return tasks;
    }

    /*
     * KMP is linear-time and memory efficient.
     */
    private Task createKMPTask(
            WorkloadAnalyzer.WorkloadProfile profile,
            int number) {

        int cpu =
                calculateLinearCpu(
                        profile.getEstimatedCpu());

        int memory =
                calculateLinearMemory(
                        profile.getEstimatedMemory());

        int duration =
                calculateLinearDuration(
                        profile.getEstimatedDuration());

        return new Task(
                createTaskId(number),
                "Pattern Search",
                profile.getDocumentId(),
                "KMP",
                "HIGH",
                cpu,
                memory,
                duration
        );
    }

    /*
     * Rabin-Karp is also approximately linear on normal
     * workloads but performs hashing.
     */
    private Task createRabinKarpTask(
            WorkloadAnalyzer.WorkloadProfile profile,
            int number) {

        int cpu =
                calculateLinearCpu(
                        profile.getEstimatedCpu());

        int memory =
                calculateLinearMemory(
                        profile.getEstimatedMemory());

        int duration =
                calculateLinearDuration(
                        profile.getEstimatedDuration());

        return new Task(
                createTaskId(number),
                "Keyword Search",
                profile.getDocumentId(),
                "Rabin-Karp",
                "HIGH",
                cpu,
                memory,
                duration
        );
    }

    /*
     * Edit Distance uses a dynamic-programming matrix.
     *
     * Therefore it receives additional memory and CPU demand.
     */
    private Task createEditDistanceTask(
            WorkloadAnalyzer.WorkloadProfile profile,
            int number) {

        int cpu =
                increaseCpu(
                        profile.getEstimatedCpu(),
                        1);

        int memory =
                increaseMemory(
                        profile.getEstimatedMemory(),
                        64);

        int duration =
                increaseDuration(
                        profile.getEstimatedDuration(),
                        1);

        return new Task(
                createTaskId(number),
                "Document Similarity",
                profile.getDocumentId(),
                "Edit Distance",
                "MEDIUM",
                cpu,
                memory,
                duration
        );
    }

    /*
     * Z Algorithm is linear-time and requires a small
     * auxiliary array.
     */
    private Task createZAlgorithmTask(
            WorkloadAnalyzer.WorkloadProfile profile,
            int number) {

        int cpu =
                calculateLinearCpu(
                        profile.getEstimatedCpu());

        int memory =
                calculateLinearMemory(
                        profile.getEstimatedMemory());

        int duration =
                calculateLinearDuration(
                        profile.getEstimatedDuration());

        return new Task(
                createTaskId(number),
                "Structure Analysis",
                profile.getDocumentId(),
                "Z Algorithm",
                "MEDIUM",
                cpu,
                memory,
                duration
        );
    }

    /*
     * Suffix Array requires additional indexing structures.
     */
    private Task createSuffixArrayTask(
            WorkloadAnalyzer.WorkloadProfile profile,
            int number) {

        int cpu =
                increaseCpu(
                        profile.getEstimatedCpu(),
                        1);

        int memory =
                increaseMemory(
                        profile.getEstimatedMemory(),
                        128);

        int duration =
                increaseDuration(
                        profile.getEstimatedDuration(),
                        1);

        return new Task(
                createTaskId(number),
                "Text Indexing",
                profile.getDocumentId(),
                "Suffix Array",
                "HIGH",
                cpu,
                memory,
                duration
        );
    }

    /*
     * Sequence Alignment uses dynamic programming and therefore
     * has higher computational and memory requirements.
     */
    private Task createSequenceAlignmentTask(
            WorkloadAnalyzer.WorkloadProfile profile,
            int number) {

        int cpu =
                increaseCpu(
                        profile.getEstimatedCpu(),
                        1);

        int memory =
                increaseMemory(
                        profile.getEstimatedMemory(),
                        128);

        int duration =
                increaseDuration(
                        profile.getEstimatedDuration(),
                        1);

        return new Task(
                createTaskId(number),
                "Sequence Analysis",
                profile.getDocumentId(),
                "Sequence Alignment",
                "MEDIUM",
                cpu,
                memory,
                duration
        );
    }

    /*
     * Linear algorithms use the workload analyzer's
     * estimated CPU requirement directly.
     */
    private int calculateLinearCpu(int baseCpu) {

        if (baseCpu < 1) {
            return 1;
        }

        if (baseCpu > 4) {
            return 4;
        }

        return baseCpu;
    }

    /*
     * Prevent unrealistically small memory requirements.
     */
    private int calculateLinearMemory(int baseMemory) {

        if (baseMemory < 128) {
            return 128;
        }

        if (baseMemory > 4096) {
            return 4096;
        }

        return baseMemory;
    }

    /*
     * Prevent zero-duration tasks.
     */
    private int calculateLinearDuration(int baseDuration) {

        if (baseDuration < 1) {
            return 1;
        }

        return baseDuration;
    }

    /*
     * Increase CPU demand while keeping it within
     * the supported scheduling range.
     */
    private int increaseCpu(
            int baseCpu,
            int additionalCpu) {

        int cpu =
                baseCpu + additionalCpu;

        if (cpu < 1) {
            return 1;
        }

        if (cpu > 4) {
            return 4;
        }

        return cpu;
    }

    /*
     * Increase memory demand while keeping it bounded.
     */
    private int increaseMemory(
            int baseMemory,
            int additionalMemory) {

        int memory =
                baseMemory + additionalMemory;

        if (memory < 128) {
            return 128;
        }

        if (memory > 4096) {
            return 4096;
        }

        return memory;
    }

    /*
     * Increase estimated execution time for algorithms
     * with higher computational complexity.
     */
    private int increaseDuration(
            int baseDuration,
            int additionalDuration) {

        int duration =
                baseDuration + additionalDuration;

        if (duration < 1) {
            return 1;
        }

        return duration;
    }

    private String createTaskId(int number) {

        if (number < 10) {
            return "DYN0" + number;
        }

        return "DYN" + number;
    }

    public void displayGeneratedTasks(Task[] tasks) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          GENERATED DYNAMIC TASKS");

        System.out.println(
                "==============================================");

        if (tasks == null || tasks.length == 0) {

            System.out.println(
                    "No tasks generated.");

            System.out.println(
                    "==============================================");

            return;
        }

        for (int i = 0; i < tasks.length; i++) {

            Task task = tasks[i];

            System.out.println(
                    "\nTask ID       : "
                    + task.getTaskId());

            System.out.println(
                    "Task Name     : "
                    + task.getTaskName());

            System.out.println(
                    "Document      : "
                    + task.getDocumentId());

            System.out.println(
                    "Algorithm     : "
                    + task.getAlgorithm());

            System.out.println(
                    "Priority      : "
                    + task.getPriority());

            System.out.println(
                    "CPU Required  : "
                    + task.getCpuRequired());

            System.out.println(
                    "Memory        : "
                    + task.getMemoryRequired()
                    + " MB");

            System.out.println(
                    "Duration      : "
                    + task.getEstimatedDuration()
                    + " sec");

            System.out.println(
                    "Status        : "
                    + task.getStatus());

            System.out.println(
                    "----------------------------------------------");
        }

        System.out.println(
                "\nTotal Dynamic Tasks : "
                + tasks.length);

        System.out.println(
                "==============================================");
    }
}