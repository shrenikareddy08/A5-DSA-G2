package test;

import model.Task;
import execution.TaskExecutor;

public class TaskExecutorTest {

    public static void main(String[] args) {

        Task task = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                2,
                100,
                3
        );

        TaskExecutor executor =
                new TaskExecutor();

        long startTime =
                System.currentTimeMillis();

        executor.execute(task);

        long endTime =
                System.currentTimeMillis();

        System.out.println("\n----------------------------------------------");

        System.out.println(
                "Execution time: "
                + (endTime - startTime)
                + " ms");

        System.out.println(
                "Final status: "
                + task.getStatus());

        System.out.println("----------------------------------------------");
    }
}