package scheduling;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import structures.Graph;

public class HEFTSchedulerV2 {

    private Task[] tasks;
    private Resource[] resources;

    private int taskCount;
    private int resourceCount;

    private Graph dependencyGraph;

    private double[] upwardRanks;
    private int[] resourceAssignments;
    private double[] plannedStartTimes;
    private double[] plannedFinishTimes;

    private boolean[] completed;
    private boolean[] failed;
    private boolean[] blocked;

    private long startTime;
    private long endTime;

    private int completedTasks;
    private int failedTasks;
    private int blockedTasks;
    private int scheduleRounds;

    private double totalCpuTime;
    private double totalMemoryTime;

    public HEFTSchedulerV2(int maxTasks, int maxResources) {

        tasks = new Task[maxTasks];
        resources = new Resource[maxResources];

        upwardRanks = new double[maxTasks];
        resourceAssignments = new int[maxTasks];

        plannedStartTimes = new double[maxTasks];
        plannedFinishTimes = new double[maxTasks];

        completed = new boolean[maxTasks];
        failed = new boolean[maxTasks];
        blocked = new boolean[maxTasks];

        taskCount = 0;
        resourceCount = 0;

        dependencyGraph = new Graph(maxTasks);

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;
        scheduleRounds = 0;

        totalCpuTime = 0.0;
        totalMemoryTime = 0.0;

        for (int i = 0; i < maxTasks; i++) {
            resourceAssignments[i] = -1;
            upwardRanks[i] = -1.0;
            plannedStartTimes[i] = 0.0;
            plannedFinishTimes[i] = 0.0;
        }
    }

    public void addTask(Task task) {

        if (task == null) {
            return;
        }

        if (taskCount >= tasks.length) {
            System.out.println("ERROR: Maximum task capacity reached.");
            return;
        }

        tasks[taskCount] = task;
        dependencyGraph.addTask(task.getTaskId());

        taskCount++;
    }

    public void addResource(Resource resource) {

        if (resource == null) {
            return;
        }

        if (resourceCount >= resources.length) {
            System.out.println("ERROR: Maximum resource capacity reached.");
            return;
        }

        resources[resourceCount] = resource;
        resourceCount++;
    }

    public void addDependency(String predecessorId, String successorId) {

        dependencyGraph.addDependency(predecessorId, successorId);
    }

    public Graph getDependencyGraph() {

        return dependencyGraph;
    }

    /*
     * ============================================================
     * UPWARD RANK
     * ============================================================
     *
     * HEFT upward rank:
     *
     * rank(i) = average computation cost(i)
     *           + max(rank(successor))
     *
     * In our implementation the task's estimated duration is used
     * as the computation cost.
     *
     * Communication cost is assumed to be zero because this project
     * executes tasks inside the same scheduling environment.
     */

    public void calculateUpwardRanks() {

        for (int i = 0; i < taskCount; i++) {
            upwardRanks[i] = calculateRank(i, new boolean[taskCount]);
        }
    }

    private double calculateRank(int taskIndex, boolean[] visiting) {

        if (upwardRanks[taskIndex] >= 0.0) {
            return upwardRanks[taskIndex];
        }

        if (visiting[taskIndex]) {
            return tasks[taskIndex].getEstimatedDuration();
        }

        visiting[taskIndex] = true;

        double maximumSuccessorRank = 0.0;

        for (int j = 0; j < taskCount; j++) {

            if (dependencyGraph.hasDependency(
                    tasks[taskIndex].getTaskId(),
                    tasks[j].getTaskId())) {

                double successorRank =
                        calculateRank(j, visiting);

                if (successorRank > maximumSuccessorRank) {
                    maximumSuccessorRank = successorRank;
                }
            }
        }

        visiting[taskIndex] = false;

        upwardRanks[taskIndex] =
                tasks[taskIndex].getEstimatedDuration()
                + maximumSuccessorRank;

        return upwardRanks[taskIndex];
    }

    /*
     * ============================================================
     * HEFT ORDER
     * ============================================================
     *
     * Tasks are ordered by decreasing upward rank.
     *
     * Manual sorting is used so that the scheduling engine does not
     * depend on java.util.*.
     */

    private int[] createHEFTOrder() {

        int[] order = new int[taskCount];

        for (int i = 0; i < taskCount; i++) {
            order[i] = i;
        }

        for (int i = 0; i < taskCount - 1; i++) {

            int best = i;

            for (int j = i + 1; j < taskCount; j++) {

                if (upwardRanks[order[j]]
                        > upwardRanks[order[best]]) {

                    best = j;
                }
            }

            if (best != i) {

                int temp = order[i];
                order[i] = order[best];
                order[best] = temp;
            }
        }

        return order;
    }

    /*
     * ============================================================
     * RESOURCE EXECUTION TIME
     * ============================================================
     *
     * A resource with more CPU capacity is treated as faster.
     *
     * This is a project-level heterogeneous-resource model.
     */

    private double estimateExecutionTime(
            Task task,
            Resource resource) {

        int maximumCpu = 1;

        for (int i = 0; i < resourceCount; i++) {

            if (resources[i].getTotalCpu() > maximumCpu) {
                maximumCpu = resources[i].getTotalCpu();
            }
        }

        double speedFactor =
                (double) resource.getTotalCpu()
                / (double) maximumCpu;

        if (speedFactor <= 0.0) {
            speedFactor = 1.0;
        }

        double executionTime =
                (double) task.getEstimatedDuration()
                / speedFactor;

        return executionTime;
    }

    /*
     * ============================================================
     * PREDECESSOR FINISH TIME
     * ============================================================
     */

    private double getMaximumPredecessorFinishTime(int taskIndex) {

        double maximum = 0.0;

        for (int i = 0; i < taskCount; i++) {

            if (dependencyGraph.hasDependency(
                    tasks[i].getTaskId(),
                    tasks[taskIndex].getTaskId())) {

                if (plannedFinishTimes[i] > maximum) {
                    maximum = plannedFinishTimes[i];
                }
            }
        }

        return maximum;
    }

    /*
     * ============================================================
     * HEFT PLAN
     * ============================================================
     *
     * For every task:
     *
     * EST = max(
     *      resource available time,
     *      predecessor finish time
     * )
     *
     * EFT = EST + execution time
     *
     * The resource producing the smallest EFT is selected.
     */

    public void createHEFTPlan() {

        calculateUpwardRanks();

        int[] order = createHEFTOrder();

        double[] resourceAvailableTime =
                new double[resourceCount];

        for (int i = 0; i < resourceCount; i++) {
            resourceAvailableTime[i] = 0.0;
        }

        for (int position = 0;
             position < order.length;
             position++) {

            int taskIndex = order[position];

            Task task = tasks[taskIndex];

            int bestResource = -1;
            double bestFinish = Double.MAX_VALUE;
            double bestStart = 0.0;

            double predecessorFinish =
                    getMaximumPredecessorFinishTime(taskIndex);

            for (int r = 0; r < resourceCount; r++) {

                Resource resource = resources[r];

                if (!resource.canRunTask(task)) {
                    continue;
                }

                double executionTime =
                        estimateExecutionTime(task, resource);

                double start =
                        resourceAvailableTime[r];

                if (predecessorFinish > start) {
                    start = predecessorFinish;
                }

                double finish =
                        start + executionTime;

                if (finish < bestFinish) {

                    bestFinish = finish;
                    bestStart = start;
                    bestResource = r;
                }
            }

            if (bestResource >= 0) {

                resourceAssignments[taskIndex] =
                        bestResource;

                plannedStartTimes[taskIndex] =
                        bestStart;

                plannedFinishTimes[taskIndex] =
                        bestFinish;

                resourceAvailableTime[bestResource] =
                        bestFinish;
            }
            else {

                resourceAssignments[taskIndex] = -1;

                plannedStartTimes[taskIndex] = -1.0;
                plannedFinishTimes[taskIndex] = -1.0;
            }
        }
    }

    /*
     * ============================================================
     * DISPLAY RANKS
     * ============================================================
     */

    public void displayHEFTRanks() {

        System.out.println();
        System.out.println("==================================================");
        System.out.println("             HEFT V2 UPWARD RANKS");
        System.out.println("==================================================");

        int[] order = createHEFTOrder();

        for (int i = 0; i < order.length; i++) {

            int index = order[i];

            System.out.printf(
                    "%s | %-30s | Rank = %.2f%n",
                    tasks[index].getTaskId(),
                    tasks[index].getTaskName(),
                    upwardRanks[index]);
        }
    }

    /*
     * ============================================================
     * DISPLAY PLAN
     * ============================================================
     */

    public void displayHEFTPlan() {

        System.out.println();
        System.out.println("==================================================");
        System.out.println("              HEFT V2 STATIC PLAN");
        System.out.println("==================================================");

        int[] order = createHEFTOrder();

        for (int i = 0; i < order.length; i++) {

            int index = order[i];

            int resourceIndex =
                    resourceAssignments[index];

            if (resourceIndex >= 0) {

                System.out.printf(
                        "%s -> %s | Rank %.2f | EST %.2f | EFT %.2f%n",
                        tasks[index].getTaskId(),
                        resources[resourceIndex].getResourceId(),
                        upwardRanks[index],
                        plannedStartTimes[index],
                        plannedFinishTimes[index]);
            }
            else {

                System.out.printf(
                        "%s -> NO RESOURCE | Rank %.2f%n",
                        tasks[index].getTaskId(),
                        upwardRanks[index]);
            }
        }
    }

    /*
     * ============================================================
     * DEPENDENCY CHECK
     * ============================================================
     */

    private boolean areDependenciesCompleted(int taskIndex) {

        for (int i = 0; i < taskCount; i++) {

            if (dependencyGraph.hasDependency(
                    tasks[i].getTaskId(),
                    tasks[taskIndex].getTaskId())) {

                if (!completed[i]) {
                    return false;
                }

                if (failed[i] || blocked[i]) {
                    return false;
                }
            }
        }

        return true;
    }

    /*
     * ============================================================
     * CHECK WHETHER A TASK HAS PREDECESSOR FAILURE
     * ============================================================
     */

    private boolean hasFailedPredecessor(int taskIndex) {

        for (int i = 0; i < taskCount; i++) {

            if (dependencyGraph.hasDependency(
                    tasks[i].getTaskId(),
                    tasks[taskIndex].getTaskId())) {

                if (failed[i] || blocked[i]) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * ============================================================
     * FIND READY TASK FOR RESOURCE
     * ============================================================
     *
     * IMPORTANT:
     *
     * The resource assignment comes from the precomputed HEFT plan.
     *
     * We do NOT choose another resource at runtime.
     */

    private int findReadyTaskForResource(int resourceIndex) {

        int bestTask = -1;
        double bestRank = -1.0;

        for (int i = 0; i < taskCount; i++) {

            if (completed[i]
                    || failed[i]
                    || blocked[i]) {
                continue;
            }

            if (resourceAssignments[i] != resourceIndex) {
                continue;
            }

            if (hasFailedPredecessor(i)) {

                blocked[i] = true;
                blockedTasks++;

                System.out.println(
                        "[HEFT V2] BLOCKED "
                        + tasks[i].getTaskId()
                        + " because a predecessor failed.");

                continue;
            }

            if (!areDependenciesCompleted(i)) {
                continue;
            }

            if (!resources[resourceIndex].canRunTask(tasks[i])) {
                continue;
            }

            if (upwardRanks[i] > bestRank) {

                bestRank = upwardRanks[i];
                bestTask = i;
            }
        }

        return bestTask;
    }

    /*
     * ============================================================
     * EXECUTION WORKER
     * ============================================================
     */

    private class HEFTWorker extends Thread {

        private int taskIndex;
        private int resourceIndex;
        private long executionTime;

        HEFTWorker(
                int taskIndex,
                int resourceIndex) {

            this.taskIndex = taskIndex;
            this.resourceIndex = resourceIndex;
            this.executionTime = 0L;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        @Override
        public void run() {

            long taskStart =
                    System.nanoTime();

            System.out.println(
                    "[HEFT V2 START] "
                    + tasks[taskIndex].getTaskId()
                    + " -> "
                    + resources[resourceIndex].getResourceId());

            try {
            	TaskExecutor executor = new TaskExecutor();
            	executor.execute(tasks[taskIndex]);

                if ("COMPLETED".equalsIgnoreCase(
                        tasks[taskIndex].getStatus())) {

                    completed[taskIndex] = true;

                    System.out.println(
                            "[HEFT V2 COMPLETE] "
                            + tasks[taskIndex].getTaskId()
                            + " -> "
                            + resources[resourceIndex]
                                    .getResourceId());
                }
                else {

                    failed[taskIndex] = true;

                    System.out.println(
                            "[HEFT V2 FAILED] "
                            + tasks[taskIndex].getTaskId());
                }

            }
            catch (Exception e) {

                failed[taskIndex] = true;

                System.out.println(
                        "[HEFT V2 ERROR] "
                        + tasks[taskIndex].getTaskId()
                        + " : "
                        + e.getMessage());
            }

            long taskEnd =
                    System.nanoTime();

            executionTime =
                    (taskEnd - taskStart) / 1_000_000L;

            if (executionTime <= 0L) {
                executionTime = 1L;
            }

            totalCpuTime +=
                    (double) tasks[taskIndex].getCpuRequired()
                    * (double) executionTime;

            totalMemoryTime +=
                    (double) tasks[taskIndex].getMemoryRequired()
                    * (double) executionTime;
        }
    }

    /*
     * ============================================================
     * EXECUTE
     * ============================================================
     */

    public void execute() {

        if (taskCount == 0) {

            System.out.println(
                    "HEFT V2: No tasks available.");

            return;
        }

        if (resourceCount == 0) {

            System.out.println(
                    "HEFT V2: No resources available.");

            return;
        }

        if (dependencyGraph.hasCycle()) {

            System.out.println(
                    "ERROR: Dependency graph contains a cycle.");

            return;
        }

        System.out.println();
        System.out.println("==================================================");
        System.out.println("       HEFT V2 STRICT PLAN EXECUTION");
        System.out.println("==================================================");

        /*
         * Step 1:
         * Build the complete HEFT plan.
         */
        createHEFTPlan();

        displayHEFTRanks();
        displayHEFTPlan();

        startTime = System.nanoTime();

        int remaining = taskCount;

        while (remaining > 0) {

            scheduleRounds++;

            System.out.println();
            System.out.println(
                    "---------------- HEFT V2 ROUND "
                    + scheduleRounds
                    + " ----------------");

            HEFTWorker[] workers =
                    new HEFTWorker[resourceCount];

            int workerCount = 0;

            /*
             * Each resource gets at most one task in a round.
             *
             * The task must:
             * 1. Belong to that resource according to HEFT.
             * 2. Have all dependencies completed.
             * 3. Fit CPU and memory requirements.
             */

            for (int r = 0; r < resourceCount; r++) {

                if (resources[r].isBusy()) {
                    continue;
                }

                int taskIndex =
                        findReadyTaskForResource(r);

                if (taskIndex < 0) {
                    continue;
                }

                resources[r].allocate(
                        tasks[taskIndex]);

                workers[workerCount] =
                        new HEFTWorker(
                                taskIndex,
                                r);

                workerCount++;

                System.out.println(
                        "[HEFT V2 ASSIGN] "
                        + tasks[taskIndex].getTaskId()
                        + " -> "
                        + resources[r].getResourceId()
                        + " | Rank "
                        + String.format(
                                "%.2f",
                                upwardRanks[taskIndex]));
            }

            /*
             * If no worker could be created, determine whether
             * tasks are blocked or whether the system is waiting
             * for a resource/dependency state.
             */

            if (workerCount == 0) {

                boolean markedBlocked = false;

                for (int i = 0; i < taskCount; i++) {

                    if (completed[i]
                            || failed[i]
                            || blocked[i]) {
                        continue;
                    }

                    if (hasFailedPredecessor(i)) {

                        blocked[i] = true;
                        blockedTasks++;

                        markedBlocked = true;

                        System.out.println(
                                "[HEFT V2 BLOCK] "
                                + tasks[i].getTaskId());
                    }
                }

                if (markedBlocked) {
                    continue;
                }

                /*
                 * All resources may be temporarily occupied.
                 *
                 * Normally this path should not occur because
                 * each round joins all workers before continuing.
                 */

                System.out.println(
                        "HEFT V2: No ready task can be dispatched.");

                break;
            }

            /*
             * Start all selected tasks.
             */

            for (int i = 0; i < workerCount; i++) {
                workers[i].start();
            }

            /*
             * Wait for all selected tasks.
             */

            for (int i = 0; i < workerCount; i++) {

                try {
                    workers[i].join();
                }
                catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    System.out.println(
                            "HEFT V2 interrupted.");
                }
            }

            /*
             * Release resources.
             */

            for (int i = 0; i < workerCount; i++) {

                int taskIndex =
                        workers[i].taskIndex;

                int resourceIndex =
                        workers[i].resourceIndex;

                resources[resourceIndex].release(
                        tasks[taskIndex]);

                if (failed[taskIndex]) {
                    failedTasks++;
                }
                else if (completed[taskIndex]) {
                    completedTasks++;
                }
            }

            remaining = 0;

            for (int i = 0; i < taskCount; i++) {

                if (!completed[i]
                        && !failed[i]
                        && !blocked[i]) {

                    remaining++;
                }
            }
        }

        /*
         * Mark remaining tasks as blocked if execution stopped
         * before they could be scheduled.
         */

        for (int i = 0; i < taskCount; i++) {

            if (!completed[i]
                    && !failed[i]
                    && !blocked[i]) {

                blocked[i] = true;
                blockedTasks++;
            }
        }

        endTime = System.nanoTime();

        displayPerformance();
    }

    /*
     * ============================================================
     * PERFORMANCE METRICS
     * ============================================================
     */

    public void displayPerformance() {

        double executionTimeMs =
                (double) (endTime - startTime)
                / 1_000_000.0;

        if (executionTimeMs <= 0.0) {
            executionTimeMs = 0.001;
        }

        double throughput =
                (double) completedTasks
                / (executionTimeMs / 1000.0);

        int totalCpuCapacity = 0;
        int totalMemoryCapacity = 0;

        for (int i = 0; i < resourceCount; i++) {

            totalCpuCapacity +=
                    resources[i].getTotalCpu();

            totalMemoryCapacity +=
                    resources[i].getTotalMemory();
        }

        double cpuUtilization = 0.0;

        if (totalCpuCapacity > 0) {

            cpuUtilization =
                    (totalCpuTime / 1000.0)
                    / (
                        (executionTimeMs / 1000.0)
                        * totalCpuCapacity
                      )
                    * 100.0;
        }

        double memoryUtilization = 0.0;

        if (totalMemoryCapacity > 0) {

            memoryUtilization =
                    (totalMemoryTime / 1000.0)
                    / (
                        (executionTimeMs / 1000.0)
                        * totalMemoryCapacity
                      )
                    * 100.0;
        }

        if (cpuUtilization > 100.0) {
            cpuUtilization = 100.0;
        }

        if (memoryUtilization > 100.0) {
            memoryUtilization = 100.0;
        }

        System.out.println();
        System.out.println("==================================================");
        System.out.println("          HEFT V2 PERFORMANCE REPORT");
        System.out.println("==================================================");

        System.out.println(
                "Total Tasks       : " + taskCount);

        System.out.println(
                "Completed Tasks   : " + completedTasks);

        System.out.println(
                "Failed Tasks      : " + failedTasks);

        System.out.println(
                "Blocked Tasks     : " + blockedTasks);

        System.out.println(
                "Scheduling Rounds : " + scheduleRounds);

        System.out.printf(
                "Makespan          : %.3f ms%n",
                executionTimeMs);

        System.out.printf(
                "Throughput        : %.2f tasks/sec%n",
                throughput);

        System.out.printf(
                "CPU Utilization   : %.2f%%%n",
                cpuUtilization);

        System.out.printf(
                "Memory Utilization: %.2f%%%n",
                memoryUtilization);

        System.out.println(
                "--------------------------------------------------");

        if (completedTasks == taskCount) {

            System.out.println(
                    "SUCCESS: HEFT V2 completed all tasks.");
        }
        else {

            System.out.println(
                    "WARNING: HEFT V2 did not complete all tasks.");
        }

        System.out.println(
                "==================================================");
    }

    /*
     * ============================================================
     * ACCESSORS
     * ============================================================
     */

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public int getBlockedTasks() {
        return blockedTasks;
    }

    public int getScheduleRounds() {
        return scheduleRounds;
    }

    public double getUpwardRank(String taskId) {

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i].getTaskId().equals(taskId)) {
                return upwardRanks[i];
            }
        }

        return -1.0;
    }

    public String getAssignedResource(String taskId) {

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i].getTaskId().equals(taskId)) {

                int resourceIndex =
                        resourceAssignments[i];

                if (resourceIndex >= 0) {
                    return resources[resourceIndex]
                            .getResourceId();
                }

                return "NONE";
            }
        }

        return "UNKNOWN";
    }

    public double getPlannedStartTime(String taskId) {

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i].getTaskId().equals(taskId)) {
                return plannedStartTimes[i];
            }
        }

        return -1.0;
    }

    public double getPlannedFinishTime(String taskId) {

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i].getTaskId().equals(taskId)) {
                return plannedFinishTimes[i];
            }
        }

        return -1.0;
    }

    public double getMakespanMs() {

        if (endTime <= startTime) {
            return 0.0;
        }

        return (double) (endTime - startTime)
                / 1_000_000.0;
    }
}