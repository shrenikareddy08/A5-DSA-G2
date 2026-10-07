package scheduling;

import execution.TaskExecutor;
import model.Resource;
import model.Task;
import structures.Graph;

public class Scheduler {

    private Task[] tasks;
    private Resource[] resources;

    private int taskCount;
    private int resourceCount;

    private Graph dependencyGraph;

    private DependencyAwareTaskScorer dependencyScorer;

    private long scheduleStartTime;
    private long scheduleEndTime;

    private int completedTasks;
    private int failedTasks;
    private int blockedTasks;

    private int roundNumber;

    private final int MAX_RETRIES = 2;

    public Scheduler(int maxTasks, int maxResources) {

        tasks = new Task[maxTasks];
        resources = new Resource[maxResources];

        taskCount = 0;
        resourceCount = 0;

        dependencyGraph =
                new Graph(maxTasks);

        dependencyScorer =
                new DependencyAwareTaskScorer();

        scheduleStartTime = 0;
        scheduleEndTime = 0;

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;

        roundNumber = 0;
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
                    "Cannot add task. Scheduler capacity reached.");

            return;
        }

        tasks[taskCount] = task;

        dependencyGraph.addTask(
                task.getTaskId());

        taskCount++;

        System.out.println(
                "Task "
                + task.getTaskId()
                + " added to scheduler.");
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
                    "Cannot add resource. Scheduler capacity reached.");

            return;
        }

        resources[resourceCount] = resource;

        resourceCount++;

        System.out.println(
                "Resource "
                + resource.getResourceId()
                + " added to scheduler.");
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
     * DISPLAY METHODS
     * --------------------------------------------------
     */

    public void displayTasks() {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             SCHEDULER TASKS");

        System.out.println(
                "==============================================");

        for (int i = 0; i < taskCount; i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            System.out.println(
                    task.getTaskId()
                    + " - "
                    + task.getTaskName()
                    + " - "
                    + task.getPriority());
        }

        System.out.println(
                "==============================================");
    }

    public void displayResources() {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          AVAILABLE RESOURCES");

        System.out.println(
                "==============================================");

        for (int i = 0; i < resourceCount; i++) {

            Resource resource =
                    resources[i];

            if (resource == null) {
                continue;
            }

            resource.displayResource();
        }

        System.out.println(
                "==============================================");
    }

    /*
     * --------------------------------------------------
     * SCHEDULER EXECUTION
     * --------------------------------------------------
     */
    public void generateSchedule() {
        execute();
    }
    public void execute() {

        scheduleStartTime =
                System.currentTimeMillis();

        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       INTELLIGENT PARALLEL SCHEDULER");

        System.out.println(
                "==============================================");

        if (dependencyGraph.hasCycle()) {

            System.out.println(
                    "ERROR: Dependency graph contains a cycle.");

            System.out.println(
                    "Scheduling cannot continue.");

            return;
        }

        System.out.println(
                "Dependency graph is valid.");

        System.out.println(
                "Intelligent scheduling engine: ENABLED.");

        System.out.println(
                "Real algorithm execution: ENABLED.");

        System.out.println(
                "Parallel resource execution: ENABLED.");

        System.out.println(
                "==============================================");

        System.out.println(
                "\n          PERFORMANCE MONITOR");

        System.out.println(
                "==============================================");

        System.out.println(
                "Performance monitoring started.");

        boolean progress = true;

        while (!allTasksFinished()
                && progress) {

            roundNumber++;

            progress = executeRound();

            if (!progress) {

                System.out.println(
                        "\nNo executable task could be scheduled.");

                blockUnresolvableTasks();
            }
        }

        scheduleEndTime =
                System.currentTimeMillis();

        System.out.println(
                "\nPerformance monitoring stopped.");

        displayFinalStatus();

        displayPerformanceResults();
    }

    /*
     * --------------------------------------------------
     * ONE PARALLEL SCHEDULING ROUND
     * --------------------------------------------------
     */

    private boolean executeRound() {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "                 ROUND "
                + roundNumber);

        System.out.println(
                "==============================================");

        /*
         * First identify every task whose dependencies
         * are currently satisfied.
         */
        updateReadyTasks();

        /*
         * One worker can execute one task.
         *
         * We create one worker for every resource that
         * receives a compatible task.
         */
        Task[] selectedTasks =
                new Task[resourceCount];

        Resource[] selectedResources =
                new Resource[resourceCount];

        int selectedCount = 0;

        /*
         * --------------------------------------------------
         * PHASE 1:
         * Select tasks for ALL available resources.
         * --------------------------------------------------
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

            Task bestTask =
                    selectBestTaskForResource(
                            resource,
                            selectedTasks,
                            selectedCount);

            if (bestTask == null) {
                continue;
            }

            double score =
                    dependencyScorer.calculateScore(
                            bestTask,
                            tasks,
                            dependencyGraph,
                            resource);

            selectedTasks[selectedCount] =
                    bestTask;

            selectedResources[selectedCount] =
                    resource;

            selectedCount++;

            System.out.printf(
                    "%s -> %s -> READY | Intelligent Score: %.2f%n",
                    bestTask.getTaskId(),
                    resource.getResourceId(),
                    score);
        }

        /*
         * Nothing could be scheduled in this round.
         */
        if (selectedCount == 0) {

            System.out.println(
                    "No runnable tasks available in this round.");

            return false;
        }

        /*
         * --------------------------------------------------
         * PHASE 2:
         * Allocate resources BEFORE starting workers.
         * --------------------------------------------------
         */
        for (int i = 0;
             i < selectedCount;
             i++) {

            Task task =
                    selectedTasks[i];

            Resource resource =
                    selectedResources[i];

            resource.allocate(task);

            task.setStatus("RUNNING");

            System.out.println(
                    task.getTaskId()
                    + " allocated to "
                    + resource.getResourceId());
        }

        System.out.println(
                "\nStarting intelligent parallel execution...");

        /*
         * --------------------------------------------------
         * PHASE 3:
         * Create ALL worker threads first.
         * --------------------------------------------------
         */
        TaskExecutionWorker[] workers =
                new TaskExecutionWorker[selectedCount];

        for (int i = 0;
             i < selectedCount;
             i++) {

            workers[i] =
                    new TaskExecutionWorker(
                            selectedTasks[i],
                            selectedResources[i],
                            "Worker-" + (i + 1));
        }

        /*
         * --------------------------------------------------
         * PHASE 4:
         * START ALL THREADS.
         *
         * This is the critical parallel section.
         * --------------------------------------------------
         */
        for (int i = 0;
             i < selectedCount;
             i++) {

            workers[i].start();
        }

        /*
         * --------------------------------------------------
         * PHASE 5:
         * WAIT FOR ALL THREADS.
         *
         * We do NOT wait immediately after each start.
         * Therefore all selected workers get a chance
         * to execute concurrently.
         * --------------------------------------------------
         */
        for (int i = 0;
             i < selectedCount;
             i++) {

            try {

                workers[i].join();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println(
                        "Scheduler interrupted while waiting "
                        + "for "
                        + workers[i].getName());
            }
        }

        /*
         * --------------------------------------------------
         * PHASE 6:
         * Release resources after all workers finish.
         * --------------------------------------------------
         */
        for (int i = 0;
             i < selectedCount;
             i++) {

            Task task =
                    selectedTasks[i];

            Resource resource =
                    selectedResources[i];

            if (task.getStatus() != null
                    && task.getStatus()
                    .equalsIgnoreCase("COMPLETED")) {

                completedTasks++;

            } else if (task.getStatus() != null
                    && task.getStatus()
                    .equalsIgnoreCase("FAILED")) {

                failedTasks++;

            } else {

                task.setStatus("FAILED");

                failedTasks++;
            }

            resource.release(task);

            System.out.println(
                    task.getTaskId()
                    + " -> "
                    + task.getStatus());

            System.out.println(
                    resource.getResourceId()
                    + " -> RELEASED");
        }

        System.out.println(
                "\nRound "
                + roundNumber
                + " completed.");

        System.out.println(
                "Completed tasks: "
                + completedTasks
                + "/"
                + taskCount);

        System.out.println(
                "Failed tasks: "
                + failedTasks);

        System.out.println(
                "Blocked tasks: "
                + blockedTasks);

        return true;
    }

    /*
     * --------------------------------------------------
     * UPDATE READY TASKS
     * --------------------------------------------------
     */

    private void updateReadyTasks() {

        for (int i = 0;
             i < taskCount;
             i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            if (task.getStatus() == null) {
                continue;
            }

            if (!task.getStatus()
                    .equalsIgnoreCase("WAITING")) {
                continue;
            }

            if (dependencyScorer.areDependenciesSatisfied(
                    task,
                    tasks,
                    dependencyGraph)) {

                task.setStatus("READY");

                System.out.println(
                        task.getTaskId()
                        + " is READY.");
            }
        }
    }

    /*
     * --------------------------------------------------
     * INTELLIGENT TASK SELECTION
     * --------------------------------------------------
     */

    private Task selectBestTaskForResource(
            Resource resource,
            Task[] selectedTasks,
            int selectedCount) {

        Task bestTask = null;

        double bestScore = -1.0;

        for (int i = 0;
             i < taskCount;
             i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            if (isAlreadySelected(
                    task,
                    selectedTasks,
                    selectedCount)) {

                continue;
            }

            if (!isTaskRunnable(
                    task,
                    resource)) {

                continue;
            }

            double score =
                    dependencyScorer.calculateScore(
                            task,
                            tasks,
                            dependencyGraph,
                            resource);

            if (score > bestScore) {

                bestScore = score;

                bestTask = task;
            }
        }

        return bestTask;
    }

    private boolean isAlreadySelected(
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

    private boolean isTaskRunnable(
            Task task,
            Resource resource) {

        if (task == null
                || resource == null) {

            return false;
        }

        String status =
                task.getStatus();

        if (status == null) {
            return false;
        }

        if (!(status.equalsIgnoreCase("READY")
                || status.equalsIgnoreCase("WAITING"))) {

            return false;
        }

        if (!dependencyScorer.areDependenciesSatisfied(
                task,
                tasks,
                dependencyGraph)) {

            return false;
        }

        if (!resource.canRunTask(task)) {
            return false;
        }

        return true;
    }

    /*
     * --------------------------------------------------
     * RECOVERY + REAL EXECUTION
     * --------------------------------------------------
     */

    private boolean executeTaskWithRecovery(
            Task task) {

        int attempt = 0;

        while (attempt <= MAX_RETRIES) {

            attempt++;

            System.out.println(
                    "\n"
                    + task.getTaskId()
                    + " - Attempt "
                    + attempt);

            /*
             * Simulated transient failure:
             * fail only on the first attempt.
             */
            if (task.isSimulateFailure()
                    && attempt == 1) {

                System.out.println(
                        task.getTaskId()
                        + " simulated transient failure.");

                task.setStatus("FAILED");

                continue;
            }

            /*
             * Permanent failure:
             * never execute the algorithm.
             */
            if (task.isPermanentFailure()) {

                System.out.println(
                        task.getTaskId()
                        + " permanent failure activated.");

                task.setStatus("FAILED");

                break;
            }

            long start =
                    System.currentTimeMillis();

            try {

                /*
                 * ACTUAL ALGORITHM EXECUTION.
                 */
                TaskExecutor executor =
                        new TaskExecutor();

                executor.execute(task);

            } catch (Exception e) {

                System.out.println(
                        task.getTaskId()
                        + " execution exception: "
                        + e.getMessage());

                task.setStatus("FAILED");
            }

            long end =
                    System.currentTimeMillis();

            System.out.println(
                    "Actual algorithm execution time: "
                    + (end - start)
                    + " ms");

            if (task.getStatus() != null
                    && task.getStatus()
                    .equalsIgnoreCase("COMPLETED")) {

                System.out.println(
                        "Attempts used: "
                        + attempt);

                System.out.println(
                        task.getTaskId()
                        + " execution successful.");

                return true;
            }

            if (attempt <= MAX_RETRIES) {

                System.out.println(
                        task.getTaskId()
                        + " failed. Retrying...");
            }
        }

        System.out.println(
                "Attempts used: "
                + attempt);

        System.out.println(
                task.getTaskId()
                + " execution failed after recovery attempts.");

        task.setStatus("FAILED");

        return false;
    }

    /*
     * --------------------------------------------------
     * WORKER THREAD
     * --------------------------------------------------
     */

    private class TaskExecutionWorker
            extends Thread {

        private Task task;
        private Resource resource;

        public TaskExecutionWorker(
                Task task,
                Resource resource,
                String workerName) {

            super(workerName);

            this.task = task;
            this.resource = resource;
        }

        @Override
        public void run() {

            System.out.println(
                    task.getTaskId()
                    + " started on "
                    + getName()
                    + " using "
                    + resource.getResourceId());

            task.setStatus("RUNNING");

            boolean success =
                    executeTaskWithRecovery(task);

            if (success) {

                task.setStatus("COMPLETED");

            } else {

                task.setStatus("FAILED");
            }

            System.out.println(
                    task.getTaskId()
                    + " finished on "
                    + getName()
                    + " -> "
                    + task.getStatus());
        }
    }

    /*
     * --------------------------------------------------
     * BLOCKING
     * --------------------------------------------------
     */

    private void blockUnresolvableTasks() {

        for (int i = 0;
             i < taskCount;
             i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            if (task.getStatus() == null) {
                continue;
            }

            if (task.getStatus()
                    .equalsIgnoreCase("WAITING")
                    || task.getStatus()
                    .equalsIgnoreCase("READY")) {

                task.setStatus("BLOCKED");

                blockedTasks++;

                System.out.println(
                        task.getTaskId()
                        + " -> BLOCKED");
            }
        }
    }

    /*
     * --------------------------------------------------
     * COMPLETION CHECK
     * --------------------------------------------------
     */

    private boolean allTasksFinished() {

        for (int i = 0;
             i < taskCount;
             i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            String status =
                    task.getStatus();

            if (status == null) {
                return false;
            }

            if (!(status.equalsIgnoreCase("COMPLETED")
                    || status.equalsIgnoreCase("FAILED")
                    || status.equalsIgnoreCase("BLOCKED"))) {

                return false;
            }
        }

        return true;
    }

    /*
     * --------------------------------------------------
     * FINAL STATUS
     * --------------------------------------------------
     */

    private void displayFinalStatus() {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             FINAL SCHEDULER STATUS");

        System.out.println(
                "==============================================");

        System.out.println(
                "Completed tasks : "
                + completedTasks);

        System.out.println(
                "Failed tasks    : "
                + failedTasks);

        System.out.println(
                "Blocked tasks   : "
                + blockedTasks);

        if (completedTasks == taskCount) {

            System.out.println(
                    "ALL TASKS COMPLETED SUCCESSFULLY");

        } else {

            System.out.println(
                    "PIPELINE FINISHED WITH NON-COMPLETED TASKS");
        }

        System.out.println(
                "==============================================");
    }

    /*
     * --------------------------------------------------
     * PERFORMANCE RESULTS
     * --------------------------------------------------
     */

    private void displayPerformanceResults() {

        long executionTime =
                scheduleEndTime
                - scheduleStartTime;

        double throughput = 0.0;

        if (executionTime > 0) {

            throughput =
                    ((double) completedTasks
                    / executionTime)
                    * 1000.0;
        }

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             PERFORMANCE RESULTS");

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
                "Execution Time    : "
                + executionTime
                + " ms");

        System.out.printf(
                "Throughput        : %.2f tasks/sec%n",
                throughput);

        System.out.println(
                "Scheduling Rounds : "
                + roundNumber);

        System.out.println(
                "==============================================");
    }

    /*
     * --------------------------------------------------
     * ACCESSORS
     * --------------------------------------------------
     */

    public int getTaskCount() {

        return taskCount;
    }

    public int getResourceCount() {

        return resourceCount;
    }

    public Task getTask(int index) {

        if (index < 0
                || index >= taskCount) {

            return null;
        }

        return tasks[index];
    }

    public Resource getResource(int index) {

        if (index < 0
                || index >= resourceCount) {

            return null;
        }

        return resources[index];
    }

    public long getExecutionTime() {

        if (scheduleEndTime == 0) {

            return 0;
        }

        return scheduleEndTime
                - scheduleStartTime;
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
}