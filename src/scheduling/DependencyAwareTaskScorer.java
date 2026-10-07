package scheduling;

import model.Resource;
import model.Task;
import structures.Graph;

public class DependencyAwareTaskScorer {

    private IntelligentTaskScorer intelligentScorer;

    public DependencyAwareTaskScorer() {

        intelligentScorer =
                new IntelligentTaskScorer();
    }

    /*
     * Checks whether every prerequisite of the supplied
     * task has completed.
     *
     * The Graph stores task IDs, while Task stores the
     * current execution status.
     */
    public boolean areDependenciesSatisfied(
            Task task,
            Task[] tasks,
            Graph graph) {

        if (task == null) {
            return false;
        }

        if (tasks == null
                || graph == null) {

            return true;
        }

        int taskIndex =
                findTaskIndex(
                        task.getTaskId(),
                        graph);

        /*
         * If the task does not exist in the graph,
         * treat it as having no dependencies.
         */
        if (taskIndex < 0) {
            return true;
        }

        /*
         * A prerequisite exists if it has an outgoing
         * edge to this task.
         */
        for (int i = 0;
             i < graph.getSize();
             i++) {

            if (graph.hasDependency(
                    graph.getTaskId(i),
                    task.getTaskId())) {

                Task prerequisite =
                        findTask(
                                graph.getTaskId(i),
                                tasks);

                if (prerequisite == null) {
                    return false;
                }

                if (prerequisite.getStatus() == null
                        || !prerequisite.getStatus()
                                .equalsIgnoreCase("COMPLETED")) {

                    return false;
                }
            }
        }

        return true;
    }

    /*
     * Calculates dependency readiness.
     *
     * 25 points:
     *     all dependencies completed
     *
     * 0 points:
     *     one or more dependencies incomplete
     *
     * This score is deliberately strong enough that
     * dependency correctness cannot be overridden merely
     * by a high task priority.
     */
    public double calculateDependencyScore(
            Task task,
            Task[] tasks,
            Graph graph) {

        if (task == null) {
            return 0.0;
        }

        if (areDependenciesSatisfied(
                task,
                tasks,
                graph)) {

            return 25.0;
        }

        return 0.0;
    }

    /*
     * Calculates the complete dependency-aware score.
     *
     * Base intelligent score:
     *     IntelligentTaskScorer
     *
     * Additional:
     *     Dependency readiness
     */
    public double calculateScore(
            Task task,
            Task[] tasks,
            Graph graph,
            Resource resource) {

        if (task == null) {
            return 0.0;
        }

        double baseScore =
                intelligentScorer.calculateScore(
                        task,
                        resource);

        double dependencyScore =
                calculateDependencyScore(
                        task,
                        tasks,
                        graph);

        return baseScore
                + dependencyScore;
    }

    /*
     * Determines whether the task is actually runnable.
     *
     * A task is runnable only when:
     *
     * 1. It exists.
     * 2. It is WAITING or READY.
     * 3. All dependencies are completed.
     * 4. The resource can execute it.
     */
    public boolean isRunnable(
            Task task,
            Task[] tasks,
            Graph graph,
            Resource resource) {

        if (task == null) {
            return false;
        }

        if (task.getStatus() == null) {
            return false;
        }

        boolean validStatus =
                task.getStatus()
                        .equalsIgnoreCase("WAITING")
                || task.getStatus()
                        .equalsIgnoreCase("READY");

        if (!validStatus) {
            return false;
        }

        if (!areDependenciesSatisfied(
                task,
                tasks,
                graph)) {

            return false;
        }

        if (resource == null) {
            return true;
        }

        return resource.canRunTask(task);
    }

    /*
     * Selects the highest-scoring RUNNABLE task.
     *
     * This is the key scheduling decision.
     */
    public Task selectBestRunnableTask(
            Task[] tasks,
            Graph graph,
            Resource resource) {

        if (tasks == null
                || tasks.length == 0) {

            return null;
        }

        Task bestTask = null;
        double bestScore = -1.0;

        for (int i = 0;
             i < tasks.length;
             i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            if (!isRunnable(
                    task,
                    tasks,
                    graph,
                    resource)) {

                continue;
            }

            double score =
                    calculateScore(
                            task,
                            tasks,
                            graph,
                            resource);

            if (score > bestScore) {

                bestScore = score;
                bestTask = task;
            }
        }

        return bestTask;
    }

    /*
     * Finds a task inside the supplied task array.
     */
    private Task findTask(
            String taskId,
            Task[] tasks) {

        if (taskId == null
                || tasks == null) {

            return null;
        }

        for (int i = 0;
             i < tasks.length;
             i++) {

            if (tasks[i] == null) {
                continue;
            }

            if (taskId.equals(
                    tasks[i].getTaskId())) {

                return tasks[i];
            }
        }

        return null;
    }

    /*
     * Finds a task index inside the dependency graph.
     */
    private int findTaskIndex(
            String taskId,
            Graph graph) {

        if (taskId == null
                || graph == null) {

            return -1;
        }

        for (int i = 0;
             i < graph.getSize();
             i++) {

            if (taskId.equals(
                    graph.getTaskId(i))) {

                return i;
            }
        }

        return -1;
    }

    /*
     * Displays dependency-aware scores.
     */
    public void displayScores(
            Task[] tasks,
            Graph graph,
            Resource resource) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "      DEPENDENCY-AWARE TASK SCORING");

        System.out.println(
                "==============================================");

        if (tasks == null
                || tasks.length == 0) {

            System.out.println(
                    "No tasks available.");

            System.out.println(
                    "==============================================");

            return;
        }

        for (int i = 0;
             i < tasks.length;
             i++) {

            Task task = tasks[i];

            if (task == null) {
                continue;
            }

            double baseScore =
                    intelligentScorer.calculateScore(
                            task,
                            resource);

            double dependencyScore =
                    calculateDependencyScore(
                            task,
                            tasks,
                            graph);

            double totalScore =
                    baseScore
                    + dependencyScore;

            boolean dependenciesReady =
                    areDependenciesSatisfied(
                            task,
                            tasks,
                            graph);

            boolean runnable =
                    isRunnable(
                            task,
                            tasks,
                            graph,
                            resource);

            System.out.println(
                    "\nTask ID            : "
                    + task.getTaskId());

            System.out.println(
                    "Task Name          : "
                    + task.getTaskName());

            System.out.println(
                    "Algorithm          : "
                    + task.getAlgorithm());

            System.out.println(
                    "Priority           : "
                    + task.getPriority());

            System.out.println(
                    "Duration           : "
                    + task.getEstimatedDuration()
                    + " sec");

            System.out.println(
                    "Base Score         : "
                    + formatScore(baseScore));

            System.out.println(
                    "Dependency Score   : "
                    + formatScore(dependencyScore));

            System.out.println(
                    "Dependencies Ready : "
                    + dependenciesReady);

            System.out.println(
                    "Resource Available : "
                    + (resource != null
                            && resource.canRunTask(task)));

            System.out.println(
                    "Runnable           : "
                    + runnable);

            System.out.println(
                    "Final Score        : "
                    + formatScore(totalScore));

            System.out.println(
                    "----------------------------------------------");
        }

        System.out.println(
                "==============================================");
    }

    private String formatScore(double score) {

        return String.format(
                "%.2f",
                score);
    }
}