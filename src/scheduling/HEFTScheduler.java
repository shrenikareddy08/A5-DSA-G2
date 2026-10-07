package scheduling;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import structures.Graph;

public class HEFTScheduler {

    private Task[] tasks;
    private Resource[] resources;

    private int taskCount;
    private int resourceCount;

    private Graph dependencyGraph;

    private double[] upwardRanks;
    private int[] resourceAssignments;

    private long startTime;
    private long endTime;

    private int completedTasks;
    private int failedTasks;
    private int blockedTasks;

    private int scheduleRounds;

    /*
     * --------------------------------------------------
     * CONSTRUCTOR
     * --------------------------------------------------
     */

    public HEFTScheduler(
            int maxTasks,
            int maxResources) {

        tasks =
                new Task[maxTasks];

        resources =
                new Resource[maxResources];

        taskCount = 0;
        resourceCount = 0;

        dependencyGraph =
                new Graph(maxTasks);

        upwardRanks =
                new double[maxTasks];

        resourceAssignments =
                new int[maxTasks];

        for (int i = 0;
             i < maxTasks;
             i++) {

            resourceAssignments[i] = -1;
        }

        startTime = 0;
        endTime = 0;

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;

        scheduleRounds = 0;
    }


    /*
     * --------------------------------------------------
     * TASK MANAGEMENT
     * --------------------------------------------------
     */

    public void addTask(Task task) {

        if (task == null) {
            return;
        }

        if (taskCount >= tasks.length) {

            System.out.println(
                    "Cannot add task. HEFT capacity reached.");

            return;
        }

        tasks[taskCount] =
                task;

        dependencyGraph.addTask(
                task.getTaskId());

        taskCount++;

        System.out.println(
                "HEFT Task "
                + task.getTaskId()
                + " added.");
    }


    /*
     * --------------------------------------------------
     * RESOURCE MANAGEMENT
     * --------------------------------------------------
     */

    public void addResource(Resource resource) {

        if (resource == null) {
            return;
        }

        if (resourceCount >= resources.length) {

            System.out.println(
                    "Cannot add resource. HEFT capacity reached.");

            return;
        }

        resources[resourceCount] =
                resource;

        resourceCount++;

        System.out.println(
                "HEFT Resource "
                + resource.getResourceId()
                + " added.");
    }


    /*
     * --------------------------------------------------
     * DEPENDENCY MANAGEMENT
     * --------------------------------------------------
     */

    public void addDependency(
            String prerequisite,
            String dependent) {

        dependencyGraph.addDependency(
                prerequisite,
                dependent);
    }


    public Graph getDependencyGraph() {

        return dependencyGraph;
    }


    /*
     * --------------------------------------------------
     * UPWARD RANK CALCULATION
     *
     * HEFT:
     *
     * rank_u(i) =
     *
     * average computation cost(i)
     * +
     * max over successors j of
     *
     * communication cost(i,j)
     * +
     * rank_u(j)
     *
     * --------------------------------------------------
     *
     * Our system does not model explicit network
     * communication cost, so we use:
     *
     * rank(i) =
     * estimated duration(i)
     * +
     * maximum successor rank
     *
     * This preserves the critical-path principle.
     * --------------------------------------------------
     */

    public void calculateUpwardRanks() {

        for (int i = 0;
             i < taskCount;
             i++) {

            upwardRanks[i] =
                    calculateRank(i);
        }
    }


    private double calculateRank(
            int taskIndex) {

        /*
         * Prevent invalid indices.
         */

        if (taskIndex < 0
                || taskIndex >= taskCount) {

            return 0.0;
        }

        /*
         * The task's own computation cost.
         */

        Task task =
                tasks[taskIndex];

        double ownCost =
                task.getEstimatedDuration();


        /*
         * Find the maximum rank among successors.
         */

        double maximumSuccessorRank =
                0.0;

        for (int successor = 0;
             successor < taskCount;
             successor++) {

            if (dependencyGraph.hasDependency(
                    task.getTaskId(),
                    tasks[successor].getTaskId())) {

                double successorRank =
                        calculateRankRecursive(
                                successor,
                                new boolean[taskCount]);

                if (successorRank
                        > maximumSuccessorRank) {

                    maximumSuccessorRank =
                            successorRank;
                }
            }
        }

        return ownCost
                + maximumSuccessorRank;
    }


    /*
     * Recursive helper with cycle protection.
     */

    private double calculateRankRecursive(
            int taskIndex,
            boolean[] visiting) {

        if (taskIndex < 0
                || taskIndex >= taskCount) {

            return 0.0;
        }

        if (visiting[taskIndex]) {

            return 0.0;
        }

        visiting[taskIndex] = true;

        Task task =
                tasks[taskIndex];

        double rank =
                task.getEstimatedDuration();

        double maximumSuccessorRank =
                0.0;

        for (int successor = 0;
             successor < taskCount;
             successor++) {

            if (dependencyGraph.hasDependency(
                    task.getTaskId(),
                    tasks[successor].getTaskId())) {

                double successorRank =
                        calculateRankRecursive(
                                successor,
                                visiting);

                if (successorRank
                        > maximumSuccessorRank) {

                    maximumSuccessorRank =
                            successorRank;
                }
            }
        }

        visiting[taskIndex] = false;

        return rank
                + maximumSuccessorRank;
    }


    /*
     * --------------------------------------------------
     * TASK LOOKUP
     * --------------------------------------------------
     */

    private int findTaskIndex(
            String taskId) {

        for (int i = 0;
             i < taskCount;
             i++) {

            if (tasks[i]
                    .getTaskId()
                    .equals(taskId)) {

                return i;
            }
        }

        return -1;
    }


    /*
     * --------------------------------------------------
     * SORT TASKS BY DECREASING UPWARD RANK
     * --------------------------------------------------
     *
     * We do not use java.util.Arrays or Collections.
     * This keeps the implementation compatible with
     * the project's algorithm restrictions.
     * --------------------------------------------------
     */

    private int[] createHEFTOrder() {

        int[] order =
                new int[taskCount];

        for (int i = 0;
             i < taskCount;
             i++) {

            order[i] = i;
        }


        /*
         * Simple selection sort.
         */

        for (int i = 0;
             i < taskCount - 1;
             i++) {

            int best = i;

            for (int j = i + 1;
                 j < taskCount;
                 j++) {

                if (upwardRanks[order[j]]
                        > upwardRanks[order[best]]) {

                    best = j;
                }
            }

            int temporary =
                    order[i];

            order[i] =
                    order[best];

            order[best] =
                    temporary;
        }

        return order;
    }


    /*
     * --------------------------------------------------
     * HEFT RESOURCE SELECTION
     *
     * Earliest Finish Time:
     *
     * EFT =
     * earliest available resource time
     * +
     * estimated execution time
     *
     * We additionally consider:
     *
     * CPU capacity
     * memory capacity
     * resource utilization
     * --------------------------------------------------
     */

    private int selectResourceForTask(
            Task task,
            double[] resourceAvailableTime) {

        int bestResource =
                -1;

        double bestFinishTime =
                Double.MAX_VALUE;


        for (int r = 0;
             r < resourceCount;
             r++) {

            Resource resource =
                    resources[r];

            if (resource == null) {
                continue;
            }


            /*
             * Resource compatibility.
             */

            if (!resource.canRunTask(task)) {
                continue;
            }


            /*
             * CPU factor.
             *
             * A resource with more CPU capacity can
             * theoretically finish CPU-intensive work
             * faster.
             *
             * We normalize relative to the maximum
             * resource CPU capacity.
             */

            int maximumCpu =
                    getMaximumCpu();

            double cpuFactor =
                    1.0;

            if (maximumCpu > 0) {

                cpuFactor =
                        (double) maximumCpu
                        / resource.getTotalCpu();
            }


            /*
             * Estimated execution time on this resource.
             */

            double executionTime =
                    task.getEstimatedDuration()
                    * cpuFactor;


            /*
             * Earliest finish time.
             */

            double finishTime =
                    resourceAvailableTime[r]
                    + executionTime;


            /*
             * Prefer the resource with the
             * smallest EFT.
             */

            if (finishTime
                    < bestFinishTime) {

                bestFinishTime =
                        finishTime;

                bestResource =
                        r;
            }
        }


        return bestResource;
    }


    private int getMaximumCpu() {

        int maximum =
                1;

        for (int i = 0;
             i < resourceCount;
             i++) {

            if (resources[i] != null
                    && resources[i].getTotalCpu()
                    > maximum) {

                maximum =
                        resources[i].getTotalCpu();
            }
        }

        return maximum;
    }


    /*
     * --------------------------------------------------
     * DISPLAY HEFT RANKS
     * --------------------------------------------------
     */

    public void displayHEFTRanks() {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             HEFT UPWARD RANKS");

        System.out.println(
                "==============================================");

        for (int i = 0;
             i < taskCount;
             i++) {

            System.out.printf(
                    "%s | %-28s | Rank = %.2f%n",
                    tasks[i].getTaskId(),
                    tasks[i].getTaskName(),
                    upwardRanks[i]);
        }

        System.out.println(
                "==============================================");
    }


    /*
     * --------------------------------------------------
     * DISPLAY HEFT RESOURCE PLAN
     * --------------------------------------------------
     */

    public void displayHEFTPlan() {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "           HEFT RESOURCE PLAN");

        System.out.println(
                "==============================================");

        for (int i = 0;
             i < taskCount;
             i++) {

            int resourceIndex =
                    resourceAssignments[i];

            if (resourceIndex >= 0
                    && resourceIndex < resourceCount) {

                System.out.printf(
                        "%s -> %s | Rank %.2f%n",
                        tasks[i].getTaskId(),
                        resources[resourceIndex]
                                .getResourceId(),
                        upwardRanks[i]);

            } else {

                System.out.println(
                        tasks[i].getTaskId()
                        + " -> NO COMPATIBLE RESOURCE");
            }
        }

        System.out.println(
                "==============================================");
    }


    /*
     * --------------------------------------------------
     * CREATE HEFT RESOURCE PLAN
     * --------------------------------------------------
     */

    public void createHEFTPlan() {

        calculateUpwardRanks();

        int[] order =
                createHEFTOrder();


        /*
         * Estimated availability time of each
         * resource.
         */

        double[] resourceAvailableTime =
                new double[resourceCount];

        for (int i = 0;
             i < resourceCount;
             i++) {

            resourceAvailableTime[i] =
                    0.0;
        }


        /*
         * Assign tasks in decreasing upward rank.
         */

        for (int position = 0;
             position < order.length;
             position++) {

            int taskIndex =
                    order[position];

            Task task =
                    tasks[taskIndex];


            int selectedResource =
                    selectResourceForTask(
                            task,
                            resourceAvailableTime);


            resourceAssignments[taskIndex] =
                    selectedResource;


            if (selectedResource >= 0) {

                int maximumCpu =
                        getMaximumCpu();

                double cpuFactor =
                        (double) maximumCpu
                        / resources[selectedResource]
                                .getTotalCpu();

                double executionTime =
                        task.getEstimatedDuration()
                        * cpuFactor;

                resourceAvailableTime[
                        selectedResource]
                        += executionTime;
            }
        }
    }


    /*
     * --------------------------------------------------
     * CHECK DEPENDENCIES
     * --------------------------------------------------
     */

    private boolean dependenciesSatisfied(
            Task task) {

        int taskIndex =
                findTaskIndex(
                        task.getTaskId());

        if (taskIndex == -1) {
            return false;
        }


        /*
         * Every predecessor must be COMPLETED.
         */

        for (int i = 0;
             i < taskCount;
             i++) {

            if (dependencyGraph.hasDependency(
                    tasks[i].getTaskId(),
                    task.getTaskId())) {

                if (!tasks[i]
                        .getStatus()
                        .equalsIgnoreCase(
                                "COMPLETED")) {

                    return false;
                }
            }
        }

        return true;
    }


    /*
     * --------------------------------------------------
     * CHECK WHETHER ALL TASKS FINISHED
     * --------------------------------------------------
     */

    private boolean allTasksFinished() {

        for (int i = 0;
             i < taskCount;
             i++) {

            String status =
                    tasks[i].getStatus();

            if (status == null) {
                return false;
            }

            if (!status.equalsIgnoreCase(
                    "COMPLETED")
                    && !status.equalsIgnoreCase(
                    "FAILED")
                    && !status.equalsIgnoreCase(
                    "BLOCKED")) {

                return false;
            }
        }

        return true;
    }


    /*
     * --------------------------------------------------
     * FIND TASKS THAT CAN RUN NOW
     * --------------------------------------------------
     */

    private Task selectReadyTaskForResource(
            int resourceIndex,
            Task[] alreadySelected,
            int selectedCount) {

        Resource resource =
                resources[resourceIndex];

        Task bestTask =
                null;

        double bestRank =
                -1.0;


        for (int i = 0;
             i < taskCount;
             i++) {

            Task task =
                    tasks[i];

            if (task == null) {
                continue;
            }


            /*
             * Only waiting / ready tasks.
             */

            String status =
                    task.getStatus();

            if (status != null
                    && !status.equalsIgnoreCase(
                            "WAITING")
                    && !status.equalsIgnoreCase(
                            "READY")) {

                continue;
            }


            /*
             * Dependencies must be complete.
             */

            if (!dependenciesSatisfied(task)) {
                continue;
            }


            /*
             * Resource must be compatible.
             */

            if (!resource.canRunTask(task)) {
                continue;
            }


            /*
             * Do not assign one task to two resources
             * during the same round.
             */

            if (alreadySelected(
                    task,
                    alreadySelected,
                    selectedCount)) {

                continue;
            }


            /*
             * HEFT chooses higher upward rank first.
             */

            if (upwardRanks[i]
                    > bestRank) {

                bestRank =
                        upwardRanks[i];

                bestTask =
                        task;
            }
        }


        return bestTask;
    }


    private boolean alreadySelected(
            Task task,
            Task[] selectedTasks,
            int selectedCount) {

        for (int i = 0;
             i < selectedCount;
             i++) {

            if (selectedTasks[i] == task) {
                return true;
            }
        }

        return false;
    }


    /*
     * --------------------------------------------------
     * EXECUTION
     * --------------------------------------------------
     */

    public void execute() {

        if (taskCount == 0) {

            System.out.println(
                    "HEFT scheduler has no tasks.");

            return;
        }


        if (resourceCount == 0) {

            System.out.println(
                    "HEFT scheduler has no resources.");

            return;
        }


        if (dependencyGraph.hasCycle()) {

            System.out.println(
                    "ERROR: Dependency graph contains a cycle.");

            return;
        }


        /*
         * Generate HEFT ranking and resource plan.
         */

        createHEFTPlan();

        displayHEFTRanks();

        displayHEFTPlan();


        /*
         * Reset statistics.
         */

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;
        scheduleRounds = 0;


        startTime =
                System.currentTimeMillis();


        /*
         * --------------------------------------------------
         * EXECUTION ROUNDS
         * --------------------------------------------------
         *
         * At each round:
         *
         * 1. Find all currently ready tasks.
         * 2. Assign them to available resources.
         * 3. Start all workers.
         * 4. Wait for all workers.
         * 5. Release resources.
         * 6. Continue.
         * --------------------------------------------------
         */

        while (!allTasksFinished()) {

            scheduleRounds++;

            Task[] selectedTasks =
                    new Task[resourceCount];

            Resource[] selectedResources =
                    new Resource[resourceCount];

            int selectedCount =
                    0;


            /*
             * Select one HEFT-ranked ready task
             * per resource.
             */

            for (int r = 0;
                 r < resourceCount;
                 r++) {

                Resource resource =
                        resources[r];

                if (resource == null) {
                    continue;
                }

                if (resource.isBusy()) {
                    continue;
                }


                Task selectedTask =
                        selectReadyTaskForResource(
                                r,
                                selectedTasks,
                                selectedCount);


                if (selectedTask == null) {
                    continue;
                }


                selectedTasks[selectedCount] =
                        selectedTask;

                selectedResources[selectedCount] =
                        resource;

                selectedCount++;
            }


            /*
             * If no task is executable, identify
             * unresolved tasks.
             */

            if (selectedCount == 0) {

                boolean unresolved =
                        false;

                for (int i = 0;
                     i < taskCount;
                     i++) {

                    Task task =
                            tasks[i];

                    if (task == null) {
                        continue;
                    }

                    String status =
                            task.getStatus();

                    if (status.equalsIgnoreCase(
                            "WAITING")
                            || status.equalsIgnoreCase(
                            "READY")) {

                        unresolved = true;

                        task.setStatus(
                                "BLOCKED");

                        blockedTasks++;
                    }
                }


                if (unresolved) {

                    System.out.println(
                            "HEFT blocked unresolved tasks.");

                }

                break;
            }


            /*
             * Allocate resources.
             */

            for (int i = 0;
                 i < selectedCount;
                 i++) {

                Task task =
                        selectedTasks[i];

                Resource resource =
                        selectedResources[i];

                resource.allocate(task);

                task.setStatus(
                        "RUNNING");


                System.out.println(
                        "HEFT: "
                        + task.getTaskId()
                        + " -> "
                        + resource.getResourceId());
            }


            /*
             * Start workers.
             */

            HEFTWorker[] workers =
                    new HEFTWorker[
                            selectedCount];


            for (int i = 0;
                 i < selectedCount;
                 i++) {

                workers[i] =
                        new HEFTWorker(
                                selectedTasks[i],
                                selectedResources[i]);

                workers[i].start();
            }


            /*
             * Wait for every worker.
             */

            for (int i = 0;
                 i < selectedCount;
                 i++) {

                try {

                    workers[i].join();

                } catch (InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    System.out.println(
                            "HEFT worker interrupted.");
                }
            }


            /*
             * Release resources.
             */

            for (int i = 0;
                 i < selectedCount;
                 i++) {

                selectedResources[i].release(
                        selectedTasks[i]);
            }
        }


        endTime =
                System.currentTimeMillis();


        /*
         * Calculate final statistics.
         */

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;


        for (int i = 0;
             i < taskCount;
             i++) {

            String status =
                    tasks[i].getStatus();

            if (status.equalsIgnoreCase(
                    "COMPLETED")) {

                completedTasks++;

            } else if (status.equalsIgnoreCase(
                    "FAILED")) {

                failedTasks++;

            } else if (status.equalsIgnoreCase(
                    "BLOCKED")) {

                blockedTasks++;
            }
        }


        displayFinalResults();
    }


    /*
     * --------------------------------------------------
     * WORKER THREAD
     * --------------------------------------------------
     */

    private class HEFTWorker
            extends Thread {

        private Task task;
        private Resource resource;


        public HEFTWorker(
                Task task,
                Resource resource) {

            this.task =
                    task;

            this.resource =
                    resource;
        }


        @Override
        public void run() {

            System.out.println(
                    "HEFT worker started: "
                    + task.getTaskId()
                    + " on "
                    + resource.getResourceId());


            try {

                TaskExecutor executor =
                        new TaskExecutor();

                executor.execute(task);

            } catch (Exception e) {

                task.setStatus(
                        "FAILED");

                System.out.println(
                        "HEFT execution error for "
                        + task.getTaskId()
                        + ": "
                        + e.getMessage());
            }


            System.out.println(
                    "HEFT worker finished: "
                    + task.getTaskId());
        }
    }


    /*
     * --------------------------------------------------
     * FINAL RESULTS
     * --------------------------------------------------
     */

    private void displayFinalResults() {

        double executionTime =
                endTime
                - startTime;


        System.out.println(
                "\n==============================================");

        System.out.println(
                "          HEFT SCHEDULER RESULTS");

        System.out.println(
                "==============================================");

        System.out.println(
                "Total Tasks       : "
                + taskCount);

        System.out.println(
                "Completed Tasks   : "
                + completedTasks);

        System.out.println(
                "Failed Tasks      : "
                + failedTasks);

        System.out.println(
                "Blocked Tasks     : "
                + blockedTasks);

        System.out.println(
                "Scheduling Rounds : "
                + scheduleRounds);

        System.out.printf(
                "Execution Time    : %.3f ms%n",
                executionTime);


        if (executionTime > 0) {

            double throughput =
                    completedTasks
                    / (executionTime / 1000.0);

            System.out.printf(
                    "Throughput        : %.2f tasks/sec%n",
                    throughput);
        }


        System.out.println(
                "==============================================");
    }


    /*
     * --------------------------------------------------
     * ACCESSORS
     * --------------------------------------------------
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


    public double getUpwardRank(
            String taskId) {

        int index =
                findTaskIndex(taskId);

        if (index == -1) {
            return -1.0;
        }

        return upwardRanks[index];
    }


    public String getAssignedResource(
            String taskId) {

        int index =
                findTaskIndex(taskId);

        if (index == -1) {
            return null;
        }

        int resourceIndex =
                resourceAssignments[index];

        if (resourceIndex < 0
                || resourceIndex >= resourceCount) {

            return null;
        }

        return resources[resourceIndex]
                .getResourceId();
    }
}