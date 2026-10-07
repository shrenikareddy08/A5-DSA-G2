package execution;

import model.Task;

public class RecoveryManager {

    private int maxRetries;

    public RecoveryManager(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean executeWithRecovery(
            Task task,
            boolean simulateFailure) {

        System.out.println("\n----------------------------------------------");
        System.out.println("          TASK RECOVERY MANAGER");
        System.out.println("----------------------------------------------");

        int attempt = 0;

        while (attempt <= maxRetries) {

            attempt++;

            System.out.println(
                    task.getTaskId()
                    + " - Attempt "
                    + attempt);

            task.setStatus("RUNNING");

            // Simulate a failure on the first attempt
            if (simulateFailure && attempt == 1) {

                task.setStatus("FAILED");

                System.out.println(
                        task.getTaskId()
                        + " FAILED.");

                if (attempt <= maxRetries) {

                    System.out.println(
                            "Recovery Manager: Retrying...");
                }

                continue;
            }

            // Successful execution
            try {

                Thread.sleep(
                        task.getEstimatedDuration()
                        * 1000);

            } catch (InterruptedException e) {

                task.setStatus("FAILED");

                System.out.println(
                        "Task execution interrupted.");

                return false;
            }

            task.setStatus("COMPLETED");

            System.out.println(
                    task.getTaskId()
                    + " COMPLETED successfully.");

            System.out.println(
                    "Attempts used: "
                    + attempt);

            return true;
        }

        task.setStatus("FAILED");

        System.out.println(
                task.getTaskId()
                + " FAILED after "
                + maxRetries
                + " retries.");

        return false;
    }
}