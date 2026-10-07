package scheduling;

import model.Resource;
import model.Task;

public class IntelligentTaskScorer {

    /*
     * Maximum score is approximately 100.
     *
     * Score components:
     *
     * Priority          → 30 points
     * Resource fit      → 25 points
     * Duration          → 20 points
     * Waiting status     → 15 points
     * CPU efficiency    → 10 points
     */

    public double calculateScore(
            Task task,
            Resource resource) {

        if (task == null) {
            return 0.0;
        }

        double score = 0.0;

        /*
         * ------------------------------------------------
         * 1. PRIORITY SCORE
         * ------------------------------------------------
         */

        if (task.getPriorityValue() == 3) {

            score += 30.0;

        } else if (task.getPriorityValue() == 2) {

            score += 20.0;

        } else {

            score += 10.0;
        }

        /*
         * ------------------------------------------------
         * 2. RESOURCE FIT SCORE
         * ------------------------------------------------
         */

        if (resource != null) {

            if (resource.canRunTask(task)) {

                score += 25.0;

            } else {

                /*
                 * Resource cannot currently execute
                 * the task.
                 */
                score += 0.0;
            }
        }

        /*
         * ------------------------------------------------
         * 3. DURATION SCORE
         *
         * Shorter tasks receive a higher score because
         * completing them early improves throughput and
         * can reduce overall makespan.
         * ------------------------------------------------
         */

        int duration =
                task.getEstimatedDuration();

        if (duration <= 1) {

            score += 20.0;

        } else if (duration <= 2) {

            score += 16.0;

        } else if (duration <= 3) {

            score += 13.0;

        } else if (duration <= 5) {

            score += 9.0;

        } else if (duration <= 8) {

            score += 5.0;

        } else {

            score += 2.0;
        }

        /*
         * ------------------------------------------------
         * 4. WAITING STATUS SCORE
         *
         * Tasks waiting to execute receive a bonus.
         * Already completed/failed tasks should not be
         * preferred.
         * ------------------------------------------------
         */

        if (task.getStatus() != null
                && task.getStatus().equalsIgnoreCase("WAITING")) {

            score += 15.0;

        } else if (task.getStatus() != null
                && task.getStatus().equalsIgnoreCase("READY")) {

            score += 15.0;

        } else if (task.getStatus() != null
                && task.getStatus().equalsIgnoreCase("RUNNING")) {

            score += 5.0;
        }

        /*
         * ------------------------------------------------
         * 5. CPU EFFICIENCY
         *
         * Prefer tasks whose CPU requirement fits well
         * within the available resource capacity.
         * ------------------------------------------------
         */

        if (resource != null
                && resource.canRunTask(task)) {

            int availableCpu =
                    resource.getAvailableCpu();

            int requiredCpu =
                    task.getCpuRequired();

            if (availableCpu > 0) {

                double cpuRatio =
                        (double) requiredCpu
                        / availableCpu;

                if (cpuRatio <= 0.25) {

                    score += 10.0;

                } else if (cpuRatio <= 0.50) {

                    score += 8.0;

                } else if (cpuRatio <= 0.75) {

                    score += 6.0;

                } else {

                    score += 4.0;
                }
            }
        }

        return score;
    }

    /*
     * Calculates a score without a specific resource.
     *
     * Useful when the scheduler first needs to rank tasks
     * before selecting a resource.
     */
    public double calculateTaskScore(Task task) {

        if (task == null) {
            return 0.0;
        }

        double score = 0.0;

        /*
         * Priority
         */
        if (task.getPriorityValue() == 3) {

            score += 30.0;

        } else if (task.getPriorityValue() == 2) {

            score += 20.0;

        } else {

            score += 10.0;
        }

        /*
         * Duration
         */
        int duration =
                task.getEstimatedDuration();

        if (duration <= 1) {

            score += 20.0;

        } else if (duration <= 2) {

            score += 16.0;

        } else if (duration <= 3) {

            score += 13.0;

        } else if (duration <= 5) {

            score += 9.0;

        } else if (duration <= 8) {

            score += 5.0;

        } else {

            score += 2.0;
        }

        /*
         * Waiting / ready state
         */
        if (task.getStatus() != null
                && (task.getStatus().equalsIgnoreCase("WAITING")
                || task.getStatus().equalsIgnoreCase("READY"))) {

            score += 15.0;
        }

        /*
         * Lower CPU requirement receives a small
         * efficiency advantage.
         */
        int cpu =
                task.getCpuRequired();

        if (cpu <= 1) {

            score += 10.0;

        } else if (cpu == 2) {

            score += 8.0;

        } else if (cpu == 3) {

            score += 5.0;

        } else {

            score += 2.0;
        }

        return score;
    }

    /*
     * Returns the task with the highest intelligent score.
     *
     * This method does not modify the original task array.
     */
    public Task selectBestTask(
            Task[] tasks,
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

            /*
             * Do not select completed, failed or blocked tasks.
             */
            if (task.getStatus() != null
                    && (task.getStatus().equalsIgnoreCase("COMPLETED")
                    || task.getStatus().equalsIgnoreCase("FAILED")
                    || task.getStatus().equalsIgnoreCase("BLOCKED"))) {

                continue;
            }

            double score =
                    calculateScore(
                            task,
                            resource);

            if (score > bestScore) {

                bestScore = score;
                bestTask = task;
            }
        }

        return bestTask;
    }

    /*
     * Displays intelligent scores for all supplied tasks.
     */
    public void displayScores(
            Task[] tasks,
            Resource resource) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "         INTELLIGENT TASK SCORING");

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

            double score =
                    calculateScore(
                            task,
                            resource);

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

            System.out.printf(
                    "Intelligent Score : %.2f%n",
                    score);

            System.out.println(
                    "----------------------------------------------");
        }

        System.out.println(
                "==============================================");
    }
}