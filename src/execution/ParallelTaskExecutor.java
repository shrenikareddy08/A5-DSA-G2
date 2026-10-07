package execution;

import model.Task;

public class ParallelTaskExecutor {

    public void executeTasks(Task[] tasks) {

        Thread[] threads = new Thread[tasks.length];

        long startTime = System.currentTimeMillis();

        System.out.println("\n==============================================");
        System.out.println("          PARALLEL TASK EXECUTION");
        System.out.println("==============================================");

        // Create one thread for each task
        for (int i = 0; i < tasks.length; i++) {

            Task task = tasks[i];

            threads[i] = new Thread(() -> {

                System.out.println(
                        task.getTaskId()
                        + " started on "
                        + Thread.currentThread().getName());

                task.setStatus("RUNNING");

                try {

                    Thread.sleep(
                            task.getEstimatedDuration() * 1000);

                } catch (InterruptedException e) {

                    task.setStatus("FAILED");

                    System.out.println(
                            task.getTaskId()
                            + " interrupted.");

                    return;
                }

                task.setStatus("COMPLETED");

                System.out.println(
                        task.getTaskId()
                        + " completed on "
                        + Thread.currentThread().getName());

            });

            threads[i].setName(
                    "Worker-" + (i + 1));
        }

        // Start all threads
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        // Wait for all threads
        for (int i = 0; i < threads.length; i++) {

            try {
                threads[i].join();

            } catch (InterruptedException e) {

                System.out.println(
                        "Main thread interrupted.");
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("----------------------------------------------");

        System.out.println(
                "Parallel execution time: "
                + (endTime - startTime)
                + " ms");

        System.out.println("==============================================");
    }
}