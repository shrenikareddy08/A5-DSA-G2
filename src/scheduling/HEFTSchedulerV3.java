package scheduling;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import structures.Graph;

/**
 * HEFT V3 - Runtime-Aware Dynamic HEFT.
 *
 * This is a project-adapted extension of classical HEFT.
 * Classical HEFT uses an estimated computation cost to build an
 * upward-rank order and then selects the processor with minimum EFT.
 * V3 keeps the HEFT priority idea but makes processor selection and
 * execution-cost estimates adaptive at runtime.
 *
 * No java.util.* is used because the project engine is required to
 * implement its own array-based scheduling structures.
 */
public class HEFTSchedulerV3 {

    private Task[] tasks;
    private Resource[] resources;
    private int taskCount;
    private int resourceCount;

    private Graph dependencyGraph;

    private double[] upwardRanks;
    private double[] runtimeEstimateMs;
    private double[] actualRuntimeMs;
    private int[] runtimeSamples;

    private boolean[] selectedThisRound;
    private boolean[] completed;
    private boolean[] failed;
    private boolean[] blocked;

    private int[] lastResourceAssignment;
    private double[] lastPredictedFinishMs;

    private long startTime;
    private long endTime;

    private int completedTasks;
    private int failedTasks;
    private int blockedTasks;
    private int scheduleRounds;

    private double totalCpuTime;
    private double totalMemoryTime;

    private static final double ALPHA = 0.30;

    public HEFTSchedulerV3(int maxTasks, int maxResources) {

        tasks = new Task[maxTasks];
        resources = new Resource[maxResources];

        upwardRanks = new double[maxTasks];
        runtimeEstimateMs = new double[maxTasks];
        actualRuntimeMs = new double[maxTasks];
        runtimeSamples = new int[maxTasks];

        selectedThisRound = new boolean[maxTasks];
        completed = new boolean[maxTasks];
        failed = new boolean[maxTasks];
        blocked = new boolean[maxTasks];

        lastResourceAssignment = new int[maxTasks];
        lastPredictedFinishMs = new double[maxTasks];

        taskCount = 0;
        resourceCount = 0;

        dependencyGraph = new Graph(maxTasks);

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;
        scheduleRounds = 0;

        startTime = 0;
        endTime = 0;
        totalCpuTime = 0.0;
        totalMemoryTime = 0.0;

        for (int i = 0; i < maxTasks; i++) {
            upwardRanks[i] = -1.0;
            runtimeEstimateMs[i] = 1000.0;
            actualRuntimeMs[i] = 0.0;
            runtimeSamples[i] = 0;
            lastResourceAssignment[i] = -1;
            lastPredictedFinishMs[i] = 0.0;
        }
    }

    public void addTask(Task task) {
        if (task == null || taskCount >= tasks.length) {
            return;
        }

        tasks[taskCount] = task;
        dependencyGraph.addTask(task.getTaskId());

        double initial = task.getEstimatedDuration() * 1000.0;
        if (initial <= 0.0) {
            initial = 1000.0;
        }
        runtimeEstimateMs[taskCount] = initial;

        taskCount++;
    }

    public void addResource(Resource resource) {
        if (resource == null || resourceCount >= resources.length) {
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

    /**
     * Seeds a measured runtime for every task using the same algorithm.
     * This is useful for calibration before a benchmark.
     */
    public void setRuntimeEstimateForAlgorithm(String algorithm, double milliseconds) {
        if (algorithm == null || milliseconds <= 0.0) {
            return;
        }

        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getAlgorithm().equalsIgnoreCase(algorithm)
                    || normalize(tasks[i].getAlgorithm()).equals(normalize(algorithm))) {
                runtimeEstimateMs[i] = milliseconds;
            }
        }
    }

    public void calculateUpwardRanks() {
        for (int i = 0; i < taskCount; i++) {
            upwardRanks[i] = -1.0;
        }

        for (int i = 0; i < taskCount; i++) {
            upwardRanks[i] = calculateRank(i, new boolean[taskCount]);
        }
    }

    private double calculateRank(int index, boolean[] visiting) {
        if (upwardRanks[index] >= 0.0) {
            return upwardRanks[index];
        }

        if (visiting[index]) {
            return runtimeEstimateMs[index];
        }

        visiting[index] = true;

        double maximumSuccessor = 0.0;

        for (int j = 0; j < taskCount; j++) {
            if (dependencyGraph.hasDependency(
                    tasks[index].getTaskId(),
                    tasks[j].getTaskId())) {

                double successorRank = calculateRank(j, visiting);
                if (successorRank > maximumSuccessor) {
                    maximumSuccessor = successorRank;
                }
            }
        }

        visiting[index] = false;

        upwardRanks[index] = runtimeEstimateMs[index] + maximumSuccessor;
        return upwardRanks[index];
    }

    public void displayHEFTRanks() {
        calculateUpwardRanks();

        System.out.println("\n==============================================");
        System.out.println("       HEFT V3 RUNTIME-AWARE UPWARD RANKS");
        System.out.println("==============================================");

        int[] order = createRankOrder();
        for (int i = 0; i < taskCount; i++) {
            int index = order[i];
            System.out.printf(
                    "%s | %-30s | Runtime = %8.2f ms | Rank = %10.2f%n",
                    tasks[index].getTaskId(),
                    tasks[index].getTaskName(),
                    runtimeEstimateMs[index],
                    upwardRanks[index]);
        }
    }

    private int[] createRankOrder() {
        int[] order = new int[taskCount];
        for (int i = 0; i < taskCount; i++) {
            order[i] = i;
        }

        for (int i = 0; i < taskCount - 1; i++) {
            int best = i;
            for (int j = i + 1; j < taskCount; j++) {
                if (upwardRanks[order[j]] > upwardRanks[order[best]]) {
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

    /**
     * Executes dynamically. No static resource assignment is enforced.
     * Every round recomputes ready tasks and selects the resource with
     * the minimum predicted finish time.
     */
    public void execute() {
        startTime = System.currentTimeMillis();
        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;
        scheduleRounds = 0;
        totalCpuTime = 0.0;
        totalMemoryTime = 0.0;

        resetRuntimeState();

        System.out.println("\n==============================================");
        System.out.println("     HEFT V3 RUNTIME-AWARE DYNAMIC SCHEDULER");
        System.out.println("==============================================");
        System.out.println("Runtime learning        : ENABLED");
        System.out.println("Dynamic resource choice : ENABLED");
        System.out.println("Real algorithm execution: ENABLED");
        System.out.println("Parallel execution      : ENABLED");

        if (dependencyGraph.hasCycle()) {
            System.out.println("ERROR: Dependency graph contains a cycle.");
            endTime = System.currentTimeMillis();
            return;
        }

        while (!allTasksFinished()) {
            scheduleRounds++;
            boolean progress = executeRound();

            if (!progress) {
                blockUnresolvableTasks();
                break;
            }
        }

        endTime = System.currentTimeMillis();
        displayPerformance();
    }

    public void generateSchedule() {
        execute();
    }

    private boolean executeRound() {
        calculateUpwardRanks();

        for (int i = 0; i < taskCount; i++) {
            selectedThisRound[i] = false;
        }

        Task[] selectedTasks = new Task[resourceCount];
        Resource[] selectedResources = new Resource[resourceCount];
        double[] roundPredictedFinish = new double[resourceCount];
        boolean[] resourceSelected = new boolean[resourceCount];

        for (int i = 0; i < resourceCount; i++) {
            roundPredictedFinish[i] = 0.0;
            resourceSelected[i] = false;
        }

        int selectedCount = 0;

        System.out.println("\n----------------------------------------------");
        System.out.println("HEFT V3 ROUND " + scheduleRounds);
        System.out.println("----------------------------------------------");

        while (selectedCount < resourceCount) {
            int taskIndex = selectHighestRankReadyTask();
            if (taskIndex == -1) {
                break;
            }

            int resourceIndex = selectBestResource(
                    taskIndex, roundPredictedFinish, resourceSelected);

            if (resourceIndex == -1) {
                selectedThisRound[taskIndex] = true;
                continue;
            }

            selectedThisRound[taskIndex] = true;
            selectedTasks[selectedCount] = tasks[taskIndex];
            selectedResources[selectedCount] = resources[resourceIndex];

            double predicted = predictedExecutionTime(taskIndex, resourceIndex);
            roundPredictedFinish[resourceIndex] = predicted;

            resourceSelected[resourceIndex] = true;
            lastResourceAssignment[taskIndex] = resourceIndex;
            lastPredictedFinishMs[taskIndex] = predicted;

            System.out.printf(
                    "%s -> %s | Rank %.2f | Predicted %.2f ms%n",
                    tasks[taskIndex].getTaskId(),
                    resources[resourceIndex].getResourceId(),
                    upwardRanks[taskIndex],
                    predicted);

            selectedCount++;
        }

        if (selectedCount == 0) {
            return false;
        }

        for (int i = 0; i < selectedCount; i++) {
            selectedResources[i].allocate(selectedTasks[i]);
            selectedTasks[i].setStatus("RUNNING");
        }

        V3Worker[] workers = new V3Worker[selectedCount];

        for (int i = 0; i < selectedCount; i++) {
            workers[i] = new V3Worker(
                    selectedTasks[i],
                    selectedResources[i],
                    "V3-Worker-" + (i + 1));
        }

        for (int i = 0; i < selectedCount; i++) {
            workers[i].start();
        }

        for (int i = 0; i < selectedCount; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (int i = 0; i < selectedCount; i++) {
            int taskIndex = findTaskIndex(selectedTasks[i].getTaskId());

            if (selectedTasks[i].getStatus() != null
                    && selectedTasks[i].getStatus().equalsIgnoreCase("COMPLETED")) {
                completed[taskIndex] = true;
                completedTasks++;
            } else {
                failed[taskIndex] = true;
                failedTasks++;
            }

            selectedResources[i].release(selectedTasks[i]);
        }

        System.out.println(
                "Round " + scheduleRounds
                + " completed | Completed=" + completedTasks
                + " Failed=" + failedTasks);

        return true;
    }

    private int selectHighestRankReadyTask() {
        int best = -1;
        double bestRank = -1.0;

        for (int i = 0; i < taskCount; i++) {
            if (selectedThisRound[i]) {
                continue;
            }

            if (tasks[i].getStatus() == null
                    || !tasks[i].getStatus().equalsIgnoreCase("WAITING")) {
                continue;
            }

            if (!dependenciesSatisfied(i)) {
                continue;
            }

            if (upwardRanks[i] > bestRank) {
                bestRank = upwardRanks[i];
                best = i;
            }
        }

        return best;
    }

    private int selectBestResource(
            int taskIndex,
            double[] roundPredictedFinish,
            boolean[] resourceSelected) {

        int bestResource = -1;
        double bestFinish = Double.MAX_VALUE;

        for (int r = 0; r < resourceCount; r++) {
            Resource resource = resources[r];

            if (resource == null || resource.isBusy() || resourceSelected[r]) {
                continue;
            }

            if (!resource.canRunTask(tasks[taskIndex])) {
                continue;
            }

            double finish = roundPredictedFinish[r]
                    + predictedExecutionTime(taskIndex, r);

            if (finish < bestFinish) {
                bestFinish = finish;
                bestResource = r;
            }
        }

        return bestResource;
    }

    private double predictedExecutionTime(int taskIndex, int resourceIndex) {
        double base = runtimeEstimateMs[taskIndex];

        if (base <= 0.0) {
            base = 1.0;
        }

        int maximumCpu = 1;
        for (int i = 0; i < resourceCount; i++) {
            if (resources[i].getTotalCpu() > maximumCpu) {
                maximumCpu = resources[i].getTotalCpu();
            }
        }

        double speedFactor =
                (double) resources[resourceIndex].getTotalCpu()
                / (double) maximumCpu;

        if (speedFactor <= 0.0) {
            speedFactor = 1.0;
        }

        return base / speedFactor;
    }

    private boolean dependenciesSatisfied(int taskIndex) {
        String id = tasks[taskIndex].getTaskId();

        for (int p = 0; p < taskCount; p++) {
            String predecessor = tasks[p].getTaskId();

            if (dependencyGraph.hasDependency(predecessor, id)) {
                if (!completed[p]) {
                    return false;
                }
            }
        }

        return true;
    }

    private void updateRuntimeEstimate(int taskIndex, double actualMs) {
        if (actualMs <= 0.0) {
            return;
        }

        actualRuntimeMs[taskIndex] = actualMs;
        runtimeSamples[taskIndex]++;

        if (runtimeSamples[taskIndex] == 1) {
            runtimeEstimateMs[taskIndex] = actualMs;
        } else {
            runtimeEstimateMs[taskIndex] =
                    (1.0 - ALPHA) * runtimeEstimateMs[taskIndex]
                    + ALPHA * actualMs;
        }
    }

    private void resetRuntimeState() {
        for (int i = 0; i < taskCount; i++) {
            completed[i] = false;
            failed[i] = false;
            blocked[i] = false;
            selectedThisRound[i] = false;
            tasks[i].setStatus("WAITING");
        }

        for (int r = 0; r < resourceCount; r++) {
            // Resources are expected to start idle in a fresh scheduler.
            // No reset API exists in Resource, so we simply require the
            // scheduler instance to own fresh Resource objects.
        }
    }

    private void blockUnresolvableTasks() {
        for (int i = 0; i < taskCount; i++) {
            if (!completed[i] && !failed[i]
                    && tasks[i].getStatus() != null
                    && tasks[i].getStatus().equalsIgnoreCase("WAITING")) {
                tasks[i].setStatus("BLOCKED");
                blocked[i] = true;
                blockedTasks++;
            }
        }
    }

    private boolean allTasksFinished() {
        return completedTasks + failedTasks + blockedTasks >= taskCount;
    }

    private int findTaskIndex(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                return i;
            }
        }
        return -1;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');
    }

    private class V3Worker extends Thread {
        private Task task;
        private Resource resource;

        V3Worker(Task task, Resource resource, String name) {
            super(name);
            this.task = task;
            this.resource = resource;
        }

        @Override
        public void run() {
            long start = System.nanoTime();

            System.out.println(
                    task.getTaskId()
                    + " started on "
                    + resource.getResourceId()
                    + " using "
                    + getName());

            TaskExecutor executor = new TaskExecutor();
            executor.execute(task);

            long end = System.nanoTime();
            double actualMs = (end - start) / 1000000.0;

            int index = findTaskIndex(task.getTaskId());

            synchronized (HEFTSchedulerV3.this) {
                if (index >= 0) {
                    updateRuntimeEstimate(index, actualMs);
                }

                totalCpuTime += actualMs * task.getCpuRequired();
                totalMemoryTime += actualMs * task.getMemoryRequired();
            }

            System.out.printf(
                    "%s finished on %s | Actual %.3f ms | New estimate %.3f ms%n",
                    task.getTaskId(),
                    resource.getResourceId(),
                    actualMs,
                    index >= 0 ? runtimeEstimateMs[index] : actualMs);
        }
    }

    public void displayPerformance() {
        long elapsed = endTime - startTime;
        double throughput = elapsed > 0
                ? ((double) completedTasks * 1000.0 / elapsed)
                : 0.0;

        double cpuUtilization = 0.0;
        double memoryUtilization = 0.0;

        if (elapsed > 0 && resourceCount > 0) {
            double totalCapacity = 0.0;
            double memoryCapacity = 0.0;

            for (int i = 0; i < resourceCount; i++) {
                totalCapacity += resources[i].getTotalCpu();
                memoryCapacity += resources[i].getTotalMemory();
            }

            if (totalCapacity > 0.0) {
                cpuUtilization =
                        totalCpuTime
                        / (elapsed * totalCapacity)
                        * 100.0;
            }

            if (memoryCapacity > 0.0) {
                memoryUtilization =
                        totalMemoryTime
                        / (elapsed * memoryCapacity)
                        * 100.0;
            }
        }

        System.out.println("\n==============================================");
        System.out.println("         HEFT V3 PERFORMANCE REPORT");
        System.out.println("==============================================");
        System.out.println("Completed Tasks    : " + completedTasks);
        System.out.println("Failed Tasks       : " + failedTasks);
        System.out.println("Blocked Tasks      : " + blockedTasks);
        System.out.println("Scheduling Rounds  : " + scheduleRounds);
        System.out.println("Makespan (ms)      : " + elapsed);
        System.out.printf("Throughput         : %.2f tasks/sec%n", throughput);
        System.out.printf("CPU Utilization    : %.2f%%%n", cpuUtilization);
        System.out.printf("Memory Utilization : %.2f%%%n", memoryUtilization);
        System.out.println("==============================================");

        displayRuntimeProfiles();
    }

    private void displayRuntimeProfiles() {
        System.out.println("\n==============================================");
        System.out.println("          HEFT V3 RUNTIME PROFILES");
        System.out.println("==============================================");

        for (int i = 0; i < taskCount; i++) {
            System.out.printf(
                    "%s | %-20s | Estimate %.3f ms | Last %.3f ms | Samples %d%n",
                    tasks[i].getTaskId(),
                    tasks[i].getAlgorithm(),
                    runtimeEstimateMs[i],
                    actualRuntimeMs[i],
                    runtimeSamples[i]);
        }
    }

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
        int index = findTaskIndex(taskId);
        return index >= 0 ? upwardRanks[index] : -1.0;
    }

    public int getAssignedResource(String taskId) {
        int index = findTaskIndex(taskId);
        return index >= 0 ? lastResourceAssignment[index] : -1;
    }

    public double getPredictedFinishTime(String taskId) {
        int index = findTaskIndex(taskId);
        return index >= 0 ? lastPredictedFinishMs[index] : -1.0;
    }

    public double getRuntimeEstimate(String taskId) {
        int index = findTaskIndex(taskId);
        return index >= 0 ? runtimeEstimateMs[index] : -1.0;
    }

    public double getLastActualRuntime(String taskId) {
        int index = findTaskIndex(taskId);
        return index >= 0 ? actualRuntimeMs[index] : -1.0;
    }

    public long getMakespanMs() {
        return endTime - startTime;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public Task getTask(int index) {
        if (index < 0 || index >= taskCount) {
            return null;
        }
        return tasks[index];
    }

    public Resource getResource(int index) {
        if (index < 0 || index >= resourceCount) {
            return null;
        }
        return resources[index];
    }
}
