package test;

import execution.EdmondsKarpTaskExecutor;
import model.Task;

public class EdmondsKarpTaskExecutorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "     EDMONDS-KARP TASK EXECUTOR TEST");

        System.out.println(
                "==============================================");

        Task task =
                new Task(
                        "T07",
                        "Network Flow Resource Allocation",
                        "D01",
                        "Edmonds-Karp",
                        "HIGH",
                        2,
                        200,
                        2
                );

        EdmondsKarpTaskExecutor executor =
                new EdmondsKarpTaskExecutor();

        executor.execute(task);

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             TEST RESULT");

        System.out.println(
                "==============================================");

        System.out.println(
                "Task ID     : "
                + task.getTaskId());

        System.out.println(
                "Task Status : "
                + task.getStatus());

        if (task.getStatus()
                .equalsIgnoreCase("COMPLETED")) {

            System.out.println(
                    "SUCCESS: Edmonds-Karp task executor "
                    + "test passed.");

        } else {

            System.out.println(
                    "FAILURE: Edmonds-Karp task executor "
                    + "did not complete.");
        }

        System.out.println(
                "==============================================");
    }
}