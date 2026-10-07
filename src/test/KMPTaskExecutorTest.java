package test;

import execution.KMPTaskExecutor;
import model.Task;

public class KMPTaskExecutorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       KMP TASK EXECUTOR TEST");

        System.out.println(
                "==============================================");

        /*
         * Create the same type of task
         * that our scheduler will use.
         */
        Task task =
                new Task(
                        "T01",
                        "Pattern Search",
                        "D01",
                        "KMP",
                        "HIGH",
                        2,
                        100,
                        1);

        /*
         * Create KMP task executor.
         */
        KMPTaskExecutor executor =
                new KMPTaskExecutor();

        /*
         * Execute KMP on D01.
         */
        executor.execute(
                task,
                "memory");

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       KMP TASK EXECUTOR TEST COMPLETED");

        System.out.println(
                "==============================================");

        System.out.println(
                "Final Task Status : "
                + task.getStatus());

        System.out.println(
                "==============================================");
    }
}