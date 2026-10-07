package execution;

import model.Task;

public class PerformanceComparison {

    public void compare(Task[] tasks) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       SEQUENTIAL VS PARALLEL");

        System.out.println(
                "==============================================");

        /*
         * -------------------------------
         * SEQUENTIAL EXECUTION
         * -------------------------------
         */

        System.out.println(
                "\nStarting sequential execution...");

        long sequentialStart =
                System.currentTimeMillis();

        for (int i = 0; i < tasks.length; i++) {

            Task task = tasks[i];

            System.out.println(
                    task.getTaskId()
                    + " started sequentially.");

            try {

                Thread.sleep(
                        task.getEstimatedDuration()
                        * 1000);

            } catch (InterruptedException e) {

                System.out.println(
                        "Sequential execution interrupted.");

                return;
            }

            System.out.println(
                    task.getTaskId()
                    + " completed.");
        }

        long sequentialEnd =
                System.currentTimeMillis();

        long sequentialTime =
                sequentialEnd - sequentialStart;

        /*
         * -------------------------------
         * PARALLEL EXECUTION
         * -------------------------------
         */

        System.out.println(
                "\nStarting parallel execution...");

        Thread[] threads =
                new Thread[tasks.length];

        long parallelStart =
                System.currentTimeMillis();

        for (int i = 0; i < tasks.length; i++) {

            final Task task = tasks[i];

            threads[i] = new Thread(() -> {

                System.out.println(
                        task.getTaskId()
                        + " started on "
                        + Thread.currentThread()
                        .getName());

                try {

                    Thread.sleep(
                            task.getEstimatedDuration()
                            * 1000);

                } catch (InterruptedException e) {

                    System.out.println(
                            task.getTaskId()
                            + " interrupted.");
                }

                System.out.println(
                        task.getTaskId()
                        + " completed on "
                        + Thread.currentThread()
                        .getName());
            });

            threads[i].setName(
                    "Worker-" + (i + 1));
        }

        for (int i = 0; i < threads.length; i++) {

            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {

            try {

                threads[i].join();

            } catch (InterruptedException e) {

                System.out.println(
                        "Main thread interrupted.");
            }
        }

        long parallelEnd =
                System.currentTimeMillis();

        long parallelTime =
                parallelEnd - parallelStart;

        /*
         * -------------------------------
         * PERFORMANCE CALCULATION
         * -------------------------------
         */

        double speedup = 0;

        if (parallelTime > 0) {

            speedup =
                    (double) sequentialTime
                    / parallelTime;
        }

        double timeSaved = 0;

        if (sequentialTime > 0) {

            timeSaved =
                    ((double)
                    (sequentialTime - parallelTime)
                    / sequentialTime)
                    * 100;
        }

        /*
         * -------------------------------
         * DISPLAY RESULTS
         * -------------------------------
         */

        System.out.println(
                "\n==============================================");

        System.out.println(
                "           PERFORMANCE COMPARISON");

        System.out.println(
                "==============================================");

        System.out.println(
                "Sequential Time : "
                + sequentialTime
                + " ms");

        System.out.println(
                "Parallel Time   : "
                + parallelTime
                + " ms");

        System.out.printf(
                "Speedup         : %.2fx%n",
                speedup);

        System.out.printf(
                "Time Saved      : %.2f%%%n",
                timeSaved);

        System.out.println(
                "==============================================");
    }
}