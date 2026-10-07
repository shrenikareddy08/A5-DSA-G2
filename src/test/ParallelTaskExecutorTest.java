package test;

import model.Task;
import execution.ParallelTaskExecutor;

public class ParallelTaskExecutorTest {

    public static void main(String[] args) {

        Task t1 = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                2,
                100,
                3
        );

        Task t2 = new Task(
                "T02",
                "Document Similarity",
                "D01",
                "Edit Distance",
                "MEDIUM",
                2,
                200,
                4
        );

        Task t3 = new Task(
                "T03",
                "Keyword Search",
                "D01",
                "Rabin-Karp",
                "HIGH",
                1,
                100,
                2
        );

        Task[] tasks = {
                t1,
                t2,
                t3
        };

        ParallelTaskExecutor executor =
                new ParallelTaskExecutor();

        executor.executeTasks(tasks);

        System.out.println("\nTask Status:");

        for (int i = 0; i < tasks.length; i++) {

            System.out.println(
                    tasks[i].getTaskId()
                    + " -> "
                    + tasks[i].getStatus());
        }

        System.out.println("\n==============================================");
        System.out.println("              TEST COMPLETED");
        System.out.println("==============================================");
    }
}